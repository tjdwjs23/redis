package demo.webflux.application.usecases.user.service

import demo.webflux.config.security.JwtTokenProvider
import demo.webflux.ports.input.UserRequest
import demo.webflux.ports.output.UserResponse
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
) {
    private val valueOperations = redisTemplate.opsForValue()

    fun save(userRequest: UserRequest): UserResponse {
        runCatching {
            val encodedPassword = passwordEncoder.encode(userRequest.password)
            valueOperations.set(userRequest.username, encodedPassword)
            return UserResponse(userRequest.username, encodedPassword)
        }.getOrElse {
            throw RuntimeException("사용자 저장에 실패했습니다. 원인: ${it.message}")
        }
    }

    fun login(
        username: String,
        password: String,
    ): String {
        val storedPassword = valueOperations.get(username) as? String
        if (storedPassword != null && passwordEncoder.matches(password, storedPassword)) {
            return jwtTokenProvider.createToken(username)
        } else {
            throw RuntimeException("로그인에 실패했습니다.")
        }
    }
}
