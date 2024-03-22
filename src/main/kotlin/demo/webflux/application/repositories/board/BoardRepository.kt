package demo.webflux.application.repositories.board

import demo.webflux.domain.board.Board
import demo.webflux.domain.board.BoardEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Range
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@Repository
interface BoardRepository : ReactiveCrudRepository<BoardEntity, String>

interface BoardRedisRepository {
    fun save(board: Board): Mono<Board>

    fun findAll(pageable: Pageable): Flux<Board>

    fun findById(id: String): Mono<Board>

    fun deleteById(id: String): Mono<Boolean>

    fun deleteAll(): Mono<Boolean>
}

@Repository
class BoardRedisRepositoryImpl(
    private val operations: ReactiveRedisOperations<String, Any>,
) : BoardRedisRepository {
    private val pageable = Pageable.unpaged()

    override fun save(board: Board): Mono<Board> {
        // ISO 8601 날짜-시간 문자열을 파싱하기 위한 포매터
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        // board.createdDate 문자열을 LocalDateTime 객체로 파싱
        val localDateTime = LocalDateTime.parse(board.createdDate, formatter)
        // LocalDateTime을 시스템 기본 시간대의 ZonedDateTime으로 변환 후, Epoch 초로 변환
        val score = localDateTime.atZone(ZoneId.of("Asia/Seoul")).toInstant().getEpochSecond().toDouble()

        return operations.opsForZSet()
            .add("board", board, score)
            .map { board }
    }


    override fun findAll(pageable: Pageable): Flux<Board> {
        val start = pageable.offset * pageable.pageSize
        val end = start + pageable.pageSize - 1

        val range = Range.closed(start, end)

        // ZREVRANGE를 사용하여 데이터 조회
        return operations.opsForZSet()
            .reverseRange("board", range)
            .map { it as Board}
    }

    override fun findById(id: String): Mono<Board> {
        return findAll(pageable).filter { p: Board -> p.id.toString() == id }.last()
    }

    override fun deleteById(id: String): Mono<Boolean> {
        return findAll(pageable)
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
