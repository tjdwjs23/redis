package demo.webflux.domain.redis

import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Service

@Service
class RedisService {
    private val valueOperations: ValueOperations<String, Any>? = null

    fun redisString() {
        valueOperations!!.set("testKey", "testValue")
        val redis = valueOperations.get("testKey")
        println(redis)
    }
}