package demo.webflux.domain.board

import demo.webflux.rest.Board
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class BoardService(private val redisTemplate: RedisTemplate<String, Any>) {

    private val valueOperations: ValueOperations<String, Any> = redisTemplate.opsForValue()

    fun save(board: Board): Board {
        runCatching {
            if (board.createDate == null) {
                board.createDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            }
            valueOperations.set(board.id, board)
            return board
        }.getOrElse {
            throw RuntimeException("게시글 저장에 실패했습니다. 원인: ${it.message}")
        }
    }


    fun getAll(): List<Board> {
        runCatching {
            val keys = redisTemplate.keys("*") ?: setOf()
            return keys.mapNotNull { id -> valueOperations.get(id) as? Board }
        }.getOrElse {
            throw RuntimeException("게시글 조회에 실패했습니다. 원인: ${it.message}")
        }
    }
}