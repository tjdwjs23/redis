package demo.webflux.domain.user

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

// RDB에 저장하기 위한 Entity 클래스
@Table("USER")
class UserEntity {
    @Id
    @Column("ID") var id: Long? = null
    @Column("USER_NAME")
    var username: String? = null
    @Column("PASSWORD")
    var password: String? = null

}

// Redis 저장하기 위한 Data 클래스
@RedisHash("user")
data class User(
    @Id
    var id: Long? = null,
    val userName: String? = null,
    val password: String? = null
)