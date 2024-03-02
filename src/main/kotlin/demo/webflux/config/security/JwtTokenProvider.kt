package demo.webflux.config.security

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
        val claims: MutableMap<String, Any> = HashMap()
        claims["sub"] = username
        val now = Date()
        val validity = Date(now.time + validityInMilliseconds)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(Keys.hmacShaKeyFor(secretKey.toByteArray(StandardCharsets.UTF_8)))
            .compact()
    }
}
