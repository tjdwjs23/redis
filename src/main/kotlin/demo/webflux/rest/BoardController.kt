package demo.webflux.rest

import demo.webflux.domain.board.BoardService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
class RedisController(private val boardService: BoardService) {
    @PostMapping("/")
    fun post(board : Board) : Board {
        boardService.save(board)
        return board
    }

    @GetMapping("/")
    fun getAll() : List<Board> {
        return boardService.getAll()
    }
}

data class Board(
    val id: String,
    val title: String,
    val content: String,
    var createDate: LocalDateTime? = null
)