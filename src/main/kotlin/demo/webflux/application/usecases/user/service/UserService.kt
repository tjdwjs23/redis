package demo.webflux.application.usecases.user.service

import demo.webflux.application.repositories.user.UserRedisRepository
import demo.webflux.application.repositories.user.UserRepository
import demo.webflux.config.security.JwtSupport
import demo.webflux.config.security.UserDetailsService
import demo.webflux.domain.user.User
import demo.webflux.domain.user.UserEntity
import demo.webflux.ports.input.UserRequest
import demo.webflux.ports.output.UserResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserService(
        private val passwordEncoder: PasswordEncoder,
        private val userRepository: UserRepository,
        private  val userRedisRepository: UserRedisRepository,
        private val userDetailsService: UserDetailsService,
        private val jwtSupport: JwtSupport
) {

    val log = KotlinLogging.logger {}

    /**
     * 사용자 등록
     * @param userRequest 사용자
     * @return 등록된 사용자
     */
    fun save(userRequest: UserRequest): Mono<UserResponse> {
        return userRedisRepository.findByUsername(userRequest.username)
                .flatMap { existingUser ->
                    Mono.error<UserResponse>(RuntimeException("사용자 이름이 이미 존재합니다: ${userRequest.username}"))
                }
                .switchIfEmpty(Mono.defer {
                    val encodedPassword = passwordEncoder.encode(userRequest.password)
                    val userEntity = UserEntity().apply {
                        username = userRequest.username
                        password = encodedPassword
                    }

                    userRepository.save(userEntity)
                            .zipWhen({ savedEntity ->
                                if (savedEntity != null) {
                                    userRedisRepository.save(User(savedEntity.id!!, savedEntity.username!!, savedEntity.password!!)).subscribe({
                                        // 성공했을 때의 처리
                                        log.info("save success on Board cache.")
                                    }, { e ->
                                        // 에러가 발생했을 때의 처리
                                        log.error("save error on Board cache.", e)
                                    })
                                }
                                Mono.just(savedEntity)
                            }, { savedEntity, _ ->
                                UserResponse(savedEntity.username!!, savedEntity.password!!)
                            })
                })
    }

    /**
     * 로그인
     * @param userRequest 사용자
     * @return 토큰
     */
    fun login(request: UserRequest): UserResponse {
        val token = jwtSupport.generate(request.username)
        return UserResponse(request.username, token.principal)
    }
}
