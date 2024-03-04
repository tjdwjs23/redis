package demo.webflux.application.repositories.user

import demo.webflux.domain.user.UserEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserRepository: ReactiveCrudRepository<UserEntity, String> {
    fun findByUsername(username: String): Mono<UserEntity>
}