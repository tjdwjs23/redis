package demo.webflux.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User


@Configuration
@EnableWebFluxSecurity
class HelloWebfluxSecurityConfig {
    /**
     * 사용자 정보를 제공하는 서비스 빈 등록
     * @return 사용자 정보 제공 서비스
     */
    @Bean
    fun userDetailsService(): ReactiveUserDetailsService {
        val userDetails = User.withDefaultPasswordEncoder()
                .username("user")
                .password("user")
                .roles("USER")
                .build()
        return MapReactiveUserDetailsService(userDetails)
    }
}