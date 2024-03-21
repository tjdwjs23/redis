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
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserService(
    private val passwordEncoder: PasswordEncoder,
    private val userRepository: UserRepository,
    private val userRedisRepository: UserRedisRepository,
    private val userDetailsService: UserDetailsService,
    private val jwtSupport: JwtSupport,
) {
    val log = KotlinLogging.logger {}

    /**
     * 사용자 등록
     * @param userRequest(username, password)
     * @return userResponse (username, password)
     */
    fun save(userRequest: UserRequest): Mono<UserResponse> {
        return userRepository.findByUsername(userRequest.username)
            .flatMap {
                Mono.error<UserResponse>(RuntimeException("사용자 이름이 이미 존재합니다: ${userRequest.username}"))
            }
            .switchIfEmpty(
                Mono.defer {
                    val encodedPassword = passwordEncoder.encode(userRequest.password)
                    val userEntity =
                        UserEntity().apply {
                            username = userRequest.username
                            password = encodedPassword
                        }

                    userRepository.save(userEntity)
                        .zipWhen({ savedEntity ->
                            if (savedEntity != null) {
                                userRedisRepository.save(
                                    User(savedEntity.id!!, savedEntity.username!!, savedEntity.password!!),
                                ).subscribe({
                                    // 성공했을 때의 처리
                                    log.info { "save success on Board cache." }
                                }, { e ->
                                    // 에러가 발생했을 때의 처리
                                    log.error { "save error on Board cache.$e" }
                                })
                            }
                            Mono.just(savedEntity)
                        }, { savedEntity, _ ->
                            UserResponse(savedEntity.username!!, savedEntity.password!!)
                        })
                },
            )
    }

    /**
     * 로그인
     * @param userRequest(username, password)
     * @return userResponse (username, token)
     */
    fun login(userRequest: UserRequest): Mono<UserResponse> {
        return userRedisRepository.findByUsername(userRequest.username)
            .zipWhen({ user ->
                if (passwordEncoder.matches(userRequest.password, user.password)) {
                    Mono.just(UserResponse(userRequest.username, jwtSupport.generate(userRequest.username).principal))
                } else {
                    Mono.error(RuntimeException("비밀번호가 일치하지 않습니다."))
                }
            }, { _, token -> token })
            .switchIfEmpty(
                userRepository.findByUsername(userRequest.username)
                    .zipWhen({ userEntity ->
                        if (passwordEncoder.matches(userRequest.password, userEntity.password)) {
                            Mono.just(UserResponse(userRequest.username, jwtSupport.generate(userRequest.username).principal))
                        } else {
                            Mono.error(RuntimeException("비밀번호가 일치하지 않습니다."))
                        }
                    }, { _, token -> token }),
            )
            .onErrorResume { Mono.just(UserResponse("오류", "로그인에 실패했습니다.")) }
    }
}
