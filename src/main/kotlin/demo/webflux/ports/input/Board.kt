package demo.webflux.ports.input

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import demo.webflux.domain.board.Board

data class BoardRequest (
       var id: Long,
       val title: String,
       val content: String,
       var createdDate: String? = null,
       var updatedDate: String? = null
){
    fun toBoard(): Board {
        return Board(id, title, content, createdDate, updatedDate)
    }
}