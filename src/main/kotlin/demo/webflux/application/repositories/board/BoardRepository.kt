package demo.webflux.application.repositories.board

import demo.webflux.domain.board.Board
import demo.webflux.domain.board.BoardEntity
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Repository
interface BoardRepository: ReactiveCrudRepository<BoardEntity, String>

interface BoardRedisRepository {
    fun save(board: Board): Mono<Board>
    fun findAll(): Flux<Board>
    fun findById(id: String): Mono<Board>
    fun deleteById(id: String): Mono<Boolean>

    fun deleteAll(): Mono<Boolean>
}

@Repository
class BoardRedisRepositoryImpl(
    private val operations: ReactiveRedisOperations<String, Any>
) : BoardRedisRepository {

    override fun save(board: Board): Mono<Board> {
        return operations.opsForList().rightPush("board", board).map { board }
    }

    override fun findAll(): Flux<Board> {
        return operations.opsForList().range("board", 0, -1).cast(Board::class.java)
    }

    override fun findById(id: String): Mono<Board> {
        return findAll().filter { p: Board -> p.id.toString() == id }.last();
    }

    override fun deleteById(id: String): Mono<Boolean> {
        return findAll()
            .collectList()
            .flatMap { list ->
                if (list.any { it.id.toString() == id }) {
                    operations.delete("board")
                        .then(operations.opsForList().rightPushAll("board", list.filter { it.id.toString() != id }))
                        .thenReturn(true)
                } else {
                    Mono.just(false)
                }
            }
    }

    override fun deleteAll(): Mono<Boolean> {
        return operations.opsForList().delete("board")
    }
}
