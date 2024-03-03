package demo.webflux.ports.output

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import demo.webflux.domain.board.Board

data class BoardResponse @JsonCreator
constructor(
        @JsonProperty("id") var id: Long? = null,
        @JsonProperty("title") val title: String? = null,
        @JsonProperty("content") val content: String? = null,
        @JsonProperty("createdDate") var createdDate: String? = null,
        @JsonProperty("updatedDate") var updatedDate: String? = null
){
    fun toBoard(): Board {
        return Board(id, title, content, createdDate, updatedDate)
    }
}