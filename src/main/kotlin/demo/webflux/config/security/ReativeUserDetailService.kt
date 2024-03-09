package demo.webflux.config.security

import demo.webflux.application.repositories.user.UserRepository
import demo.webflux.domain.user.User
import demo.webflux.domain.user.UserEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserDetailsService(
        private val userRepository: UserRepository
) : ReactiveUserDetailsService {

    override fun findByUsername(username: String): Mono<UserDetails> {
        return userRepository.findByUsername(username)
                .map { userEntity -> UserDetailsImpl(userEntity) }
    }

}
class UserDetailsImpl(private val user: UserEntity) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        // 이 부분은 사용자의 권한을 반환하는 부분입니다.
        // 여기서는 예시로 사용자의 권한을 'ROLE_USER'로 고정하였습니다.
        return listOf(SimpleGrantedAuthority("ROLE_USER"))
    }

    override fun getPassword(): String {
        // 사용자의 비밀번호를 반환하는 부분입니다.
        return user.password!!
    }

    override fun getUsername(): String {
        // 사용자의 이름을 반환하는 부분입니다.
        return user.username!!
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}
