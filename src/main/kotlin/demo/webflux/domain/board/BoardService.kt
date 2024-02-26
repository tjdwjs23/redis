package demo.webflux.domain.board

import demo.webflux.rest.Board
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BoardService(private val redisTemplate: RedisTemplate<String, Any>) {

    private val valueOperations: ValueOperations<String, Any> = redisTemplate.opsForValue()

    fun save(board: Board): Board {
        if (board.createDate == null) {
            board.createDate = LocalDateTime.now()
        }
        valueOperations.set(board.id, board)
        return board
    }


    fun getAll(): List<Board> {
        val keys = redisTemplate.keys("*") ?: setOf()
        return keys.mapNotNull { id -> valueOperations.get(id) as? Board }
    }
}