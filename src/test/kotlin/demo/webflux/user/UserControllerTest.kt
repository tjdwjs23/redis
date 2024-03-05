package demo.webflux.user

import demo.webflux.adapters.ui.user.UserController
import demo.webflux.application.usecases.board.service.BoardService
import demo.webflux.application.usecases.user.service.UserService
import demo.webflux.ports.input.UserRequest
import demo.webflux.ports.output.UserResponse
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldNotBe
import reactor.core.publisher.Mono
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.*

class UserControllerTest {

    private val userService: UserService = mock(UserService::class.java)
    private val userController = UserController(userService)

    @Test
    fun `사용자 등록 테스트`() {
        // given
        val userRequest = UserRequest(username = "username", password = "password")
        given(userService.save(userRequest)).willReturn(Mono.just(UserResponse("username", "password")))

        // when
        val result = userController.signup(userRequest).block()

        // then
        result shouldNotBe null
        result?.username shouldBe "username"
        result?.password shouldBe "password"
        then(userService.save(userRequest))
    }

    @Test
    fun `로그인 테스트`() {
        // given
        val username = "username"
        val password = "password"
        given(userService.login(username, password)).willReturn(Mono.just("token"))

        // when
        val result = userController.login(UserRequest(username, password)).block()

        // then
        result shouldNotBe null
        result shouldBe "token"
        then(userService.login(username, password))
    }
}
