package demo.webflux.ports.input

import demo.webflux.domain.board.Board

data class BoardRequest(
    var id: Long? = null,
    val title: String,
    val content: String,
    var writeId: String? = null,
    var createdDate: String? = null,
    var updatedDate: String? = null,
) {
    fun toBoard(): Board {
        return Board(id, title, content, writeId, createdDate, updatedDate)
    }
}
