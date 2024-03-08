package demo.webflux.adapters.ui.board

import demo.webflux.application.usecases.board.service.BoardService
import demo.webflux.ports.input.BoardRequest
import demo.webflux.ports.output.BoardResponse
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.Parameters
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/v1/board")
@Tag(name = "BoardController", description = "게시판 관련 API")
@SecurityScheme(type = SecuritySchemeType.APIKEY, name = "Authorization" , `in` = SecuritySchemeIn.HEADER)
class BoardController(private val boardService: BoardService) {
    /**
     * 게시글 저장
     * @param boardRequest 게시글
     * @return Mono<BoardResponse> 저장된 게시글
     */
    @PostMapping
    @Operation(summary = "게시글 저장", description = "새 게시글을 저장합니다.")
    fun post(
        @RequestBody boardRequest: BoardRequest,
    ): Mono<BoardResponse> {
        return boardService.save(boardRequest)
    }

    /**
     * 모든 게시글 조회
     * @return Flux<BoardResponse> 게시글 목록
     */
    @GetMapping
    @Operation(summary = "모든 게시글 조회", description = "모든 게시글을 조회합니다.")
    fun getAll(): Flux<BoardResponse> {
        return boardService.getAll()
    }

    /**
     * 특정 게시글 조회
     * @param id 게시글의 고유 식별자
     * @return Mono<BoardResponse> 게시글
     */
    @GetMapping("/{id}")
    @Operation(summary = "특정 게시글 조회", description = "특정 게시글을 조회합니다.")
    fun getById(
            @PathVariable id: Long,
    ):  Mono<BoardResponse> {
        return boardService.getById(id)
    }

    /**
     * 게시글 삭제
     * @param id 게시글의 고유 식별자
     * @return Mono<Boolean> 게시글 삭제 성공 여부
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "게시글 삭제", description = "특정 게시글을 삭제합니다.")
    fun deleteById(
            @PathVariable id: Long,
    ): Mono<Boolean> {
        return boardService.deleteById(id)
    }

    /**
     * 게시글 수정
     * @param id 게시글의 고유 식별자
     * @param boardRequest 수정된 게시글
     * @return Mono<BoardResponse> 수정된 게시글
     */
    @PutMapping("/{id}")
    @Operation(summary = "게시글 수정", description = "특정 게시글을 수정합니다.")
    fun updateById(
            @PathVariable id: Long,
            @RequestBody boardRequest: BoardRequest,
    ): Mono<BoardResponse> {
        return boardService.updateById(id, boardRequest)
    }
}
