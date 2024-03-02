package demo.webflux.ports.input

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import demo.webflux.domain.board.Board

data class BoardRequest @JsonCreator
constructor(
        @JsonProperty("id") var id: String,
        @JsonProperty("title") val title: String,
        @JsonProperty("content") val content: String,
        @JsonProperty("createDate") var createDate: String? = null,
){
    fun toBoard(): Board {
        return Board(id, title, content, createDate?:"")
    }
}