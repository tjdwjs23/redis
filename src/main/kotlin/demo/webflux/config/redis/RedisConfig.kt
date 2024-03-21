package demo.webflux.config.redis

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.*

@Configuration
@EnableRedisRepositories
class RedisConfig {
    @Value("\${server.data.redis.host}")
    private val host: String? = null

    @Value("\${server.data.redis.port}")
    private val port = 0

    @Bean
    fun reactiveRedisConnectionFactory(): LettuceConnectionFactory {
        return LettuceConnectionFactory(RedisStandaloneConfiguration(host!!, port))
    }

    @Bean
    fun reactiveRedisTemplate(factory: ReactiveRedisConnectionFactory): ReactiveRedisOperations<String, Any> {
        val stringSerializer: RedisSerializer<String> = StringRedisSerializer()
        val jsonSerializer = GenericJackson2JsonRedisSerializer()

        val serializationContext =
            RedisSerializationContext
                .newSerializationContext<String, Any>()
                .key(stringSerializer)
                .value(jsonSerializer)
                .hashKey(stringSerializer)
                .hashValue(jsonSerializer)
                .build()

        return ReactiveRedisTemplate(factory, serializationContext)
    }
}
