package demo.webflux.adapters.ui.user

import demo.webflux.application.usecases.user.service.UserService
import demo.webflux.ports.input.UserRequest
import demo.webflux.ports.output.UserResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserController(private val userService: UserService) {

    /**
     * 사용자 등록
     * @param userRequest 사용자
     * @return 등록된 사용자
     */
    @PostMapping("/signup")
    fun signup(
            @RequestBody userRequest: UserRequest,
    ): ResponseEntity<UserResponse> {
        val savedUser = userService.save(userRequest)
        return ResponseEntity.ok(savedUser)
    }

    /**
     * 로그인
     * @param userRequest 사용자
     * @return 토큰
     */
    @PostMapping("/login")
    fun login(
        @RequestBody userRequest: UserRequest
    ): ResponseEntity<String> {
        val token = userService.login(userRequest.username, userRequest.password)
        return ResponseEntity.ok(token)
    }
}
