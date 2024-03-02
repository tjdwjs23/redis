package demo.webflux.rest

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import demo.webflux.domain.board.BoardService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/board")
class BoardController(private val boardService: BoardService) {
    /**
     * 게시글 저장
     * @param board 게시글
     * @return 저장된 게시글
     */
    @PostMapping
    fun post(
        @RequestBody board: Board,
    ): Board {
        boardService.save(board)
        return board
    }

    /**
     * 모든 게시글 조회
     * @return 게시글 목록
     * @throws RuntimeException 게시글 조회에 실패했을 때
     */
    @GetMapping
    fun getAll(): List<Board> {
        return boardService.getAll()
    }

    /**
     * 특정 게시글 조회
     * @param id 게시글의 고유 식별자
     * @return 게시글
     * @throws RuntimeException 게시글 조회에 실패했을 때
     */
    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: String,
    ): Board {
        return boardService.getById(id)
    }

    /**
     * 게시글 삭제
     * @param id 게시글의 고유 식별자
     * @throws RuntimeException 게시글 삭제에 실패했을 때
     */
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: String,
    ) {
        boardService.deleteById(id)
    }

    /**
     * 게시글 수정
     * @param id 게시글의 고유 식별자
     * @param updatedBoard 수정된 게시글
     * @return 수정된 게시글
     * @throws RuntimeException 게시글 수정에 실패했을 때
     */
    @PutMapping("/{id}")
    fun updateById(
        @PathVariable id: String,
        @RequestBody updatedBoard: Board,
    ): Board {
        return boardService.updateById(id, updatedBoard)
    }
}

data class Board
    @JsonCreator
    constructor(
        @JsonProperty("id") var id: String,
        @JsonProperty("title") val title: String,
        @JsonProperty("content") val content: String,
        @JsonProperty("createDate") var createDate: String? = null,
    )
