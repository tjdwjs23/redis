package demo.webflux.application.usecases.user.service

import demo.webflux.application.repositories.user.UserRedisRepository
import demo.webflux.application.repositories.user.UserRepository
import demo.webflux.config.security.JwtTokenProvider
import demo.webflux.domain.user.User
import demo.webflux.domain.user.UserEntity
import demo.webflux.ports.input.UserRequest
import demo.webflux.ports.output.UserResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserService(
        private val passwordEncoder: PasswordEncoder,
        private val jwtTokenProvider: JwtTokenProvider,
        private val userRepository: UserRepository,
        private  val userRedisRepository: UserRedisRepository
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
                            .doOnSuccess { savedEntity ->
                                if (savedEntity != null) {
                                    userRedisRepository.save(User(savedEntity.id!!, savedEntity.username!!, savedEntity.password!!)).subscribe({
                                        // 성공했을 때의 처리
                                    }, { e ->
                                        // 에러가 발생했을 때의 처리
                                        log.error("save error on Board cache.", e)
                                    })
                                }
                            }
                            .map { savedEntity ->
                                UserResponse(savedEntity.username!!, savedEntity.password!!)
                            }
                })
    }



    /**
     * 로그인
     * @param userRequest 사용자
     * @return 토큰
     */
    fun login(username: String, password: String): Mono<String> {
        return userRedisRepository.findByUsername(username)
            .flatMap { user ->
                if (passwordEncoder.matches(password, user.password)) {
                    Mono.just(jwtTokenProvider.createToken(username))
                } else {
                    Mono.error<String>(RuntimeException("비밀번호가 일치하지 않습니다."))
                }
            }
            .switchIfEmpty(
                userRepository.findByUsername(username)
                    .flatMap { userEntity ->
                        if (passwordEncoder.matches(password, userEntity.password)) {
                            Mono.just(jwtTokenProvider.createToken(username))
                        } else {
                            Mono.error<String>(RuntimeException("비밀번호가 일치하지 않습니다."))
                        }
                    }
            )
            .onErrorReturn("로그인에 실패했습니다.")
    }


}
