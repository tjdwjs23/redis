package demo.webflux.domain.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Service

@Service
class RedisService(private val redisTemplate: RedisTemplate<String, Any>) {

    private val valueOperations: ValueOperations<String, Any> = redisTemplate.opsForValue()

    fun redisString() {
        valueOperations!!.set("testKey", "testValue")
        val redis = valueOperations.get("testKey")
        println(redis)
    }
}