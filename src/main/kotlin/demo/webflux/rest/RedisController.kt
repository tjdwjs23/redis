package demo.webflux.rest

import demo.webflux.domain.redis.RedisService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RedisController(private val redisService: RedisService) {

    @GetMapping("/")
    fun test() : String {
        redisService.redisString();
        return "test";
    }
}