package demo.webflux.config.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date

class BearerToken(val value: String) : AbstractAuthenticationToken(AuthorityUtils.NO_AUTHORITIES) {
    override fun getCredentials() = value
    override fun getPrincipal() = value
}

@Component
class JwtSupport(
        @Value("\${security.jwt.token.secret-key}")
        private val key: ByteArray
) {

    private val jwtKey = Keys.hmacShaKeyFor(key)
    private val parser = Jwts.parserBuilder().setSigningKey(jwtKey).build()

    fun generate(username: String): BearerToken {
        val builder = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(15, ChronoUnit.MINUTES)))
                .signWith(jwtKey)
        return BearerToken(builder.compact())
    }

    fun getMemberEmail(token: BearerToken): String {
        return parser.parseClaimsJws(token.value).body.subject
    }

    fun isValid(token: BearerToken, userDetails: UserDetails?): Boolean {
        val claims = parser.parseClaimsJws(token.value).body
        val unexpired = claims.expiration.after(Date.from(Instant.now()))
        return unexpired && (claims.subject == userDetails?.username)
    }
}