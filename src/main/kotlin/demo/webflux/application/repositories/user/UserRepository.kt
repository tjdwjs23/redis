package demo.webflux.application.repositories.user

import demo.webflux.domain.user.UserEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface UserRepository: ReactiveCrudRepository<UserEntity, String> {
    fun findByUsername(username: String): Mono<UserEntity>
}