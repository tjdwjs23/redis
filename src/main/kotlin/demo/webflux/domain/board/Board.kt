package demo.webflux.domain.board

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
    @Column("ID")
    var id: Long? = null

    @Column("TITLE")
    var title: String? = null

    @Column("CONTENT")
    var content: String? = null

    @Column("WRITE_ID")
    var writeId: String? = null

    @Column("CREATED_DATE")
    var createdDate: LocalDateTime? = null

    @Column("UPDATED_DATE")
    var updatedDate: LocalDateTime? = null
}

// Redis에 저장하기 위한 Data 클래스
@RedisHash("board")
data class Board(
    @Id var id: Long? = null,
    var title: String? = null,
    var content: String? = null,
    var writeId: String? = null,
    var createdDate: String? = null,
    var updatedDate: String? = null,
) {
    fun toBoardResponse(): BoardResponse {
        return BoardResponse(id, title, content, writeId, createdDate, updatedDate)
    }
}
