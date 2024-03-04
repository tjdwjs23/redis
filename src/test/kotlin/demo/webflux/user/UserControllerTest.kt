package demo.webflux.user

import demo.webflux.adapters.ui.user.UserController
import demo.webflux.application.usecases.user.service.UserService
import demo.webflux.ports.input.UserRequest
import demo.webflux.ports.output.UserResponse
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldNotBe
import reactor.core.publisher.Mono
import org.junit.jupiter.api.Test

class UserControllerTest {

    private val userService: UserService = mockk()
    private val userController = UserController(userService)

    @Test
    fun `사용자 등록 테스트`() {
        // given
        val userRequest = UserRequest(username = "username", password = "password")
        every { userService.save(userRequest) } returns Mono.just(UserResponse("username", "password"))

        // when
        val result = userController.signup(userRequest).block()

        // then
        result shouldNotBe null
        result?.username shouldBe "username"
        result?.password shouldBe "password"
        verify { userService.save(userRequest) }
    }

    @Test
    fun `로그인 테스트`() {
        // given
        val username = "username"
        val password = "password"
        every { userService.login(username, password) } returns Mono.just("token")

        // when
        val result = userController.login(UserRequest(username, password)).block()

        // then
        result shouldNotBe null
        result shouldBe "token"
        verify { userService.login(username, password) }
    }
}
