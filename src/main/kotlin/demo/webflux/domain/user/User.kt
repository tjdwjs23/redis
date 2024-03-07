package demo.webflux.domain.user

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("USER")
class UserEntity {
    @Id
    @Column("ID") var id: Long? = null
    @Column("USER_NAME")
    var username: String? = null
    @Column("PASSWORD")
    var password: String? = null

}

@RedisHash("user")
data class User(
    @Id
    var id: Long? = null,
    val userName: String? = null,
    val password: String? = null
)