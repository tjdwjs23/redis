package demo.webflux.domain.board

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import demo.webflux.ports.output.BoardResponse
import org.springframework.data.annotation.Id
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
data class Board @JsonCreator constructor(
        @JsonProperty("id") var id: Long?,
        @JsonProperty("title") var title: String?,
        @JsonProperty("content") var content: String?,
        @JsonProperty("createdDate") var createdDate: String?,
        @JsonProperty("updatedDate") var updatedDate: String?
){
    fun toBoardResponse(): BoardResponse {
        return BoardResponse(id, title, content, createdDate, updatedDate)
    }
}