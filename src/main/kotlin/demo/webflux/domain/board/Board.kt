package demo.webflux.domain.board

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import demo.webflux.ports.output.BoardResponse
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

// RDB에 저장하기 위한 Entity 클래스
@Table("BOARD")
class BoardEntity {
    @Id
    @Column("ID") var id: Long? = null

    @Column("TITLE") var title: String? = null

    @Column("CONTENT") var content: String? = null

    @Column("CREATED_DATE") var createdDate: LocalDateTime? = null

    @Column("UPDATED_DATE") var updatedDate: LocalDateTime? = null
}

// Redis에 저장하기 위한 클래스
@RedisHash("board")
data class Board(
        @Id var id: Long?,
        var title: String?,
        var content: String?,
        var createdDate: String?,
        var updatedDate: String?
){
    fun toBoardResponse(): BoardResponse {
        return BoardResponse(id, title, content, createdDate, updatedDate)
    }
}