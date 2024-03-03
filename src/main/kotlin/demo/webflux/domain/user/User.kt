package demo.webflux.domain.user

import org.springframework.data.annotation.Id
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

data class User(
    var id: Long,
    val userName: String,
    val password: String
)