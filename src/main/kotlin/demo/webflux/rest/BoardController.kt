package demo.webflux.rest

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import demo.webflux.domain.board.BoardService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
class BoardController(private val boardService: BoardService) {
    @PostMapping("/")
    fun post(@RequestBody board : Board) : Board {
        boardService.save(board)
        return board
    }

    @GetMapping("/")
    fun getAll() : List<Board> {
        return boardService.getAll()
    }
}

data class Board @JsonCreator constructor(
    @JsonProperty("id") val id: String,
    @JsonProperty("title") val title: String,
    @JsonProperty("content") val content: String,
    @JsonProperty("createDate") var createDate: String? = null
)