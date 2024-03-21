package demo.webflux.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {
    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun securityWebFilterChain(
        http: ServerHttpSecurity,
        authManager: JwtAuthenticationManager,
        converter: JwtAuthenticationConverter,
    ): SecurityWebFilterChain {
        val authenticationWebFilter = AuthenticationWebFilter(authManager)
        authenticationWebFilter.setServerAuthenticationConverter(converter)

        return http
            .csrf { csrf -> csrf.disable() }
            .authorizeExchange { exchanges ->
                exchanges
                    .pathMatchers("/v1/board/**").authenticated() // '/v1/board/**' 경로는 인증이 필요합니다.
                    .anyExchange().permitAll() // 그 외의 모든 경로는 인증 없이 접근 가능합니다.
            }
            .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION) // JWT 인증 필터를 추가합니다.
            .httpBasic { httpBasic -> httpBasic.disable() } // HTTP 기본 인증을 비활성화합니다.
            .formLogin { formLogin -> formLogin.disable() }
            .build()
    }
}
