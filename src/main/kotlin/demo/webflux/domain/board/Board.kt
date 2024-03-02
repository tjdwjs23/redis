package demo.webflux.domain.board

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import demo.webflux.ports.output.BoardResponse

data class Board @JsonCreator
constructor(
        @JsonProperty("id") var id: String,
        @JsonProperty("title") val title: String,
        @JsonProperty("content") val content: String,
        @JsonProperty("createDate") var createDate: String? = null,
) {
    fun toBoardResponse(): BoardResponse {
        return BoardResponse(id, title, content, createDate!!)
    }
}