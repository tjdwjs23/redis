package demo.webflux.application.repositories.user

import demo.webflux.domain.user.User
import demo.webflux.domain.user.UserEntity
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface UserRepository : ReactiveCrudRepository<UserEntity, String> {
    fun findByUsername(username: String): Mono<UserEntity>
}

interface UserRedisRepository {
    fun findAll(): Flux<User>

    fun findByUsername(username: String): Mono<User>

    fun save(user: User): Mono<User>

    fun findById(id: String): Mono<User>
}

@Repository
class UserRedisRepositoryImpl(
    private val operations: ReactiveRedisOperations<String, Any>,
) : UserRedisRepository {
    override fun findAll(): Flux<User> {
        return operations.opsForList().range("user", 0, -1).cast(User::class.java)
    }

    override fun findByUsername(username: String): Mono<User> {
        return findAll()
            .filter { p: User -> p.userName == username }
            .singleOrEmpty()
    }

    override fun save(user: User): Mono<User> {
        return operations.opsForList().rightPush("user", user).map { user }
    }

    override fun findById(id: String): Mono<User> {
        return findAll().filter { p: User -> p.id.toString() == id }.last()
    }
}
