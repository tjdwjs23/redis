package demo.webflux.domain.user

import org.springframework.security.core.GrantedAuthority

// User 모델 클래스
data class User(
        val username: String,
        val password: String,
        val authorities: List<GrantedAuthority>
)

// UserRepository 인터페이스
interface UserRepository {
    fun findByUsername(username: String): User?
    fun save(user: User)
}
