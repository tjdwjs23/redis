package demo.webflux.application.usecases.user.service

import demo.webflux.application.repositories.user.UserRepository
import demo.webflux.config.security.JwtTokenProvider
import demo.webflux.domain.user.User
import demo.webflux.domain.user.UserEntity
import demo.webflux.ports.input.UserRequest
import demo.webflux.ports.output.UserResponse
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserService(
        private val redisTemplate: RedisTemplate<String, Any>,
        private val passwordEncoder: PasswordEncoder,
        private val jwtTokenProvider: JwtTokenProvider,
        private val userRepository: UserRepository,
) {
    private val valueOperations = redisTemplate.opsForValue()

    /**
     * 사용자 등록
     * @param userRequest 사용자
     * @return 등록된 사용자
     */
    fun save(userRequest: UserRequest): Mono<UserResponse> {
        return Mono.justOrEmpty(valueOperations.get(userRequest.username))
                .flatMap<UserResponse> { existingUser ->
                    Mono.error(RuntimeException("사용자 이름이 이미 존재합니다: ${userRequest.username}"))
                }
                .switchIfEmpty(
                        Mono.defer {
                            val encodedPassword = passwordEncoder.encode(userRequest.password)
                            val entity = UserEntity().apply {
                                username = userRequest.username
                                password = encodedPassword
                            }

                            userRepository.save(entity)
                                    .doOnSuccess { savedEntity ->
                                        if (savedEntity != null) {
                                            valueOperations.set(userRequest.username, encodedPassword)
                                        }
                                    }
                                    .map { savedEntity ->
                                        UserResponse(savedEntity.username!!, savedEntity.password!!)
                                    }
                        }
                )
    }

    /**
     * 로그인
     * @param userRequest 사용자
     * @return 토큰
     */
    fun login(username: String, password: String): Mono<String> {
        val storedPassword = valueOperations.get(username) as? String
        return if (storedPassword != null && passwordEncoder.matches(password, storedPassword)) {
            Mono.just(jwtTokenProvider.createToken(username))
        } else {
            userRepository.findByUsername(username)
                    .flatMap { user ->
                        if (passwordEncoder.matches(password, user.password)) {
                            Mono.just(jwtTokenProvider.createToken(username))
                        } else {
                            Mono.error<String>(RuntimeException("비밀번호가 일치하지 않습니다."))
                        }
                    }
                    .switchIfEmpty(Mono.error(RuntimeException("로그인에 실패했습니다.")))
        }
    }


}
