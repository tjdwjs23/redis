package demo.webflux.config.security

import demo.webflux.domain.user.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.util.*

@Service
class JwtTokenProvider {
    @Value("\${security.jwt.token.secret-key}")
    private val secretKey: String = ""

    @Value("\${security.jwt.token.expire-length}")
    private val validityInMilliseconds: Long = 3600000 // 1h

    fun createToken(username: String): String {
        val additionalClaims: Map<String, Any> = emptyMap()
        val now = Date()
        val validity = Date(now.time + validityInMilliseconds)

        return Jwts.builder()
            .claims() // claim 추가
            .subject(username)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(validity)
            .add(additionalClaims)
            .and()
            .signWith(Keys.hmacShaKeyFor(secretKey.toByteArray(StandardCharsets.UTF_8)))
            .compact();
    }
}
