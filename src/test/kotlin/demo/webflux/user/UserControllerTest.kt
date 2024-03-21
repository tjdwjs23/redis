package demo.webflux.user

import demo.webflux.adapters.ui.user.UserController
import demo.webflux.application.usecases.user.service.UserService
import demo.webflux.ports.input.UserRequest
import demo.webflux.ports.output.UserResponse
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldNotBe
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.mock
import reactor.core.publisher.Mono

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
        val userRequest = UserRequest(username = "username", password = "password")
        given(userService.login(userRequest)).willReturn(Mono.just(UserResponse("username", "password")))

        // when
        val result = userController.login(userRequest).block()

        // then
        result shouldNotBe null
        result?.username shouldBe "username"
        result?.password shouldBe "password"
        then(userService.login(userRequest))
    }
}
