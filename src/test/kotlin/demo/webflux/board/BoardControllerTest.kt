package demo.webflux.board

import demo.webflux.application.usecases.board.service.BoardService
import demo.webflux.config.security.JwtSupport
import demo.webflux.ports.input.BoardRequest
import demo.webflux.ports.output.BoardResponse
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Pageable
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "36000")
class BoardControllerTest {
    @Value("\${security.jwt.token.secret-key}")
    private lateinit var key: ByteArray

    @Mock
    private lateinit var boardService: BoardService

    @Autowired
    private lateinit var webTestClient: WebTestClient

    private val pageable = Pageable.unpaged()

    private fun `토큰생성`(): String {
        return JwtSupport(key).generate(username = "string").principal
    }

    @Test
    fun `게시글 저장 테스트`() {
        // given
        val boardRequest = BoardRequest(title = "제목", content = "내용")
        val jwtToken = 토큰생성()

        val boardResponse = BoardResponse(title = "제목", content = "내용", writeId = "string")
        given(boardService.save(boardRequest)).willReturn(Mono.just(boardResponse))

        // when
        webTestClient.post().uri("/v1/board")
            .header("Authorization", "Bearer $jwtToken")
            .bodyValue(boardRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.title").isEqualTo("제목")
            .jsonPath("$.content").isEqualTo("내용")
            .jsonPath("$.writeId").isEqualTo("string")

        // then
        then(boardService).should().save(boardRequest)
    }

    @Test
    fun `모든 게시글 조회 테스트`() {
        // given
        val jwtToken = 토큰생성()
        val boardResponseList =
            listOf(BoardResponse(id = 1, title = "제목", content = "내용"), BoardResponse(id = 2, title = "제목", content = "내용"))
        given(boardService.getAll(pageable)).willReturn(Flux.fromIterable(boardResponseList))

        // when
        webTestClient.get().uri("/v1/board")
            .header("Authorization", "Bearer $jwtToken")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.[0].title").isEqualTo("제목")
            .jsonPath("$.[0].content").isEqualTo("내용")
            .jsonPath("$.[1].title").isEqualTo("제목")
            .jsonPath("$.[1].content").isEqualTo("내용")

        // then
        then(boardService).should().getAll(pageable)
    }

    @Test
    fun `특정 게시글 조회 테스트`() {
        // given
        val jwtToken = 토큰생성()
        val boardResponse = BoardResponse(id = 35, title = "제목", content = "내용")
        given(boardService.getById(35)).willReturn(Mono.just(boardResponse))

        // when
        webTestClient.get().uri("/v1/board/1")
            .header("Authorization", "Bearer $jwtToken")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.title").isEqualTo("제목")
            .jsonPath("$.content").isEqualTo("내용")

        // then
        then(boardService).should().getById(1)
    }

    @Test
    fun `게시글 삭제 테스트`() {
        // given
        val jwtToken = 토큰생성()
        given(boardService.deleteById(35)).willReturn(Mono.just(true))

        // when
        webTestClient.delete().uri("/v1/board/1")
            .header("Authorization", "Bearer $jwtToken")
            .exchange()
            .expectStatus().isOk
            .expectBody(Boolean::class.java)
            .isEqualTo(true)

        // then
        then(boardService).should().deleteById(35)
    }
}
