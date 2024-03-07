package demo.webflux.application.repositories.user

import demo.webflux.domain.user.User
import demo.webflux.domain.user.UserEntity
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserRepository: ReactiveCrudRepository<UserEntity, String> {
    fun findByUsername(username: String): Mono<UserEntity>
}

interface UserRedisRepository {
    fun findByUsername(username: String): Mono<User>
    fun save(user: User): Mono<User>
    fun findById(id: String): Mono<User>
}

@Repository
class UserRedisRepositoryImpl(
    private val operations: ReactiveRedisOperations<String, Any>
) : UserRedisRepository {

    override fun findByUsername(username: String): Mono<User> {
        return operations.opsForValue().get(username).cast(User::class.java)
    }

    override fun save(user: User): Mono<User> {
        return operations.opsForValue().set(user.userName, user).thenReturn(user)
    }

    override fun findById(id: String): Mono<User> {
        return operations.opsForValue().get(id).cast(User::class.java)
    }
}
