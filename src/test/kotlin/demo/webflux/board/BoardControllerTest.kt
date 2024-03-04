package demo.webflux.board

import demo.webflux.adapters.ui.board.BoardController
import demo.webflux.application.usecases.board.service.BoardService
import demo.webflux.ports.input.BoardRequest
import demo.webflux.ports.output.BoardResponse
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldNotBe
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class BoardControllerTest {

    private val boardService: BoardService = mockk()
    private val boardController = BoardController(boardService)

    @Test
    fun `게시글 저장 테스트`() {
        // given
        val boardRequest = BoardRequest(id = 1, title = "제목", content = "내용")
        every { boardService.save(boardRequest) } returns Mono.just(BoardResponse(1, "제목","내용"))

        // when
        val result = boardController.post(boardRequest).block()

        // then
        result shouldNotBe null
        result?.title shouldBe "제목"
        result?.content shouldBe "내용"
        verify { boardService.save(boardRequest) }
    }

    @Test
    fun `모든 게시글 조회 테스트`() {
        // given
        val boardResponseList = listOf(BoardResponse(id = 1, title = "제목", content = "내용"), BoardResponse(id = 2, title = "제목", content = "내용"))
        every { boardService.getAll() } returns Flux.fromIterable(boardResponseList)

        // when
        val result = boardController.getAll().collectList().block()

        // then
        result shouldNotBe null
        result?.size shouldBe 2
        result?.get(0)?.title shouldBe "제목"
        result?.get(0)?.content shouldBe "내용"
        result?.get(1)?.title shouldBe "제목"
        result?.get(1)?.content shouldBe "내용"
        verify { boardService.getAll() }
    }

    @Test
    fun `특정 게시글 조회 테스트`() {
        // given
        val boardResponse = BoardResponse(id = 1, title = "제목", content = "내용")
        every { boardService.getById(1) } returns Mono.just(boardResponse)

        // when
        val result = boardController.getById(1).block()

        // then
        result shouldNotBe null
        result?.title shouldBe "제목"
        result?.content shouldBe "내용"
        verify { boardService.getById(1) }
    }

    @Test
    fun `게시글 삭제 테스트`() {
        // given
        every { boardService.deleteById(1) } returns Mono.just(true)

        // when
        val result = boardController.deleteById(1).block()

        // then
        result shouldBe true
        verify { boardService.deleteById(1) }
    }

}