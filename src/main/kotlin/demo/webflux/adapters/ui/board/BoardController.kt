package demo.webflux.adapters.ui.board

import demo.webflux.application.usecases.board.service.BoardService
import demo.webflux.ports.input.BoardRequest
import demo.webflux.ports.output.BoardResponse
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

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
        @RequestBody boardRequest: BoardRequest,
    ): Mono<BoardResponse> {
        return boardService.save(boardRequest)
    }

    /**
     * 모든 게시글 조회
     * @return 게시글 목록
     * @throws RuntimeException 게시글 조회에 실패했을 때
     */
    @GetMapping
    fun getAll(): Flux<BoardResponse> {
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
        @PathVariable id: Long,
    ):  Mono<BoardResponse> {
        return boardService.getById(id)
    }

    /**
     * 게시글 삭제
     * @param id 게시글의 고유 식별자
     * @throws RuntimeException 게시글 삭제에 실패했을 때
     */
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
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
        @PathVariable id: Long,
        @RequestBody boardRequest: BoardRequest,
    ): Mono<BoardResponse> {
        return boardService.updateById(id, boardRequest)
    }
}