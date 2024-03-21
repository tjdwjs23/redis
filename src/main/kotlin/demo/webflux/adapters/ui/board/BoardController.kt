package demo.webflux.adapters.ui.board

import demo.webflux.application.usecases.board.service.BoardService
import demo.webflux.ports.input.BoardRequest
import demo.webflux.ports.output.BoardResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.PageRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/v1/board")
@Tag(name = "BoardController", description = "게시판 관련 API")
@SecurityRequirement(name = "Authorization")
@SecurityScheme(type = SecuritySchemeType.APIKEY, name = "Authorization", `in` = SecuritySchemeIn.HEADER)
class BoardController(private val boardService: BoardService) {
    var log = KotlinLogging.logger {}

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
        return ReactiveSecurityContextHolder.getContext()
            .map { it.authentication }
            .doOnNext { authentication ->
                log.info { "Authentication type: ${authentication.javaClass.name}" }
            }
            .flatMap { authentication ->
                when (authentication) {
                    is UsernamePasswordAuthenticationToken -> {
                        boardRequest.writeId = authentication.name.toString()
                        return@flatMap boardService.save(boardRequest)
                    }

                    else -> {
                        Mono.error(IllegalStateException("지원하지 않는 인증 타입입니다."))
                    }
                }
            }
    }

    /**
     * 모든 게시글 조회
     * @return Flux<BoardResponse> 게시글 목록
     */
    @GetMapping
    @Operation(summary = "모든 게시글 조회", description = "모든 게시글을 조회합니다.")
    fun getAll(
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "10") size: Int,
    ): Flux<BoardResponse> {
        return boardService.getAll(PageRequest.of(page, size))
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
    ): Mono<BoardResponse> {
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
        return ReactiveSecurityContextHolder.getContext()
            .map { it.authentication }
            .doOnNext { authentication ->
                log.info { "Authentication type: ${authentication.javaClass.name}" }
            }
            .flatMap { authentication ->
                when (authentication) {
                    is UsernamePasswordAuthenticationToken -> {
                        boardRequest.writeId = authentication.name.toString()
                        println("writeId: ${boardRequest.writeId}")
                        return@flatMap boardService.updateById(id, boardRequest)
                    }

                    else -> {
                        Mono.error(IllegalStateException("지원하지 않는 인증 타입입니다."))
                    }
                }
            }
    }
}
