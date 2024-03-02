package demo.webflux.rest

import demo.webflux.domain.user.User
import demo.webflux.domain.user.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserController(private val userService: UserService) {
    @PostMapping("/signup")
    fun signup(
        @RequestBody user: User,
    ): ResponseEntity<User> {
        val savedUser = userService.save(user)
        return ResponseEntity.ok(savedUser)
    }

    @PostMapping("/login")
    fun login(
        @RequestBody user: User,
    ): ResponseEntity<String> {
        val token = userService.login(user.username, user.password)
        return ResponseEntity.ok(token)
    }
}
