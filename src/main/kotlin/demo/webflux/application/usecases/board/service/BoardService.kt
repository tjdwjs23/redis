package demo.webflux.application.usecases.board.service

import demo.webflux.ports.input.BoardRequest
import demo.webflux.ports.output.BoardResponse
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class BoardService(private val redisTemplate: RedisTemplate<String, Any>) {
    private val valueOperations: ValueOperations<String, Any> = redisTemplate.opsForValue()

    /**
     * 게시글 저장
     * @param board 게시글
     * @return 저장된 게시글
     */
    fun save(boardRequest: BoardRequest): BoardResponse {
        runCatching {
            if (boardRequest.createDate == null) {
                boardRequest.createDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            }
            valueOperations.set(boardRequest.id, boardRequest)
            return BoardResponse(
                boardRequest.id,
                boardRequest.title,
                boardRequest.content,
                boardRequest.createDate,
            )
        }.getOrElse {
            throw RuntimeException("게시글 저장에 실패했습니다. 원인: ${it.message}")
        }
    }

    /**
     * 모든 게시글 조회
     * @return 게시글 목록
     * @throws RuntimeException 게시글 조회에 실패했을 때
     */
    fun getAll(): List<BoardResponse> {
        runCatching {
            val keys = redisTemplate.keys("*")
            return keys.mapNotNull { id -> valueOperations.get(id) as? BoardResponse }
        }.getOrElse {
            throw RuntimeException("게시글 조회에 실패했습니다. 원인: ${it.message}")
        }
    }

    /**
     * 특정 게시글 조회
     * @param id 게시글의 고유 식별자
     * @return 게시글
     * @throws RuntimeException 게시글 조회에 실패했을 때
     */
    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: String,
    ): BoardResponse {
        val board = valueOperations.get(id) as? BoardResponse
        if (board != null) {
            return board
        } else {
            throw RuntimeException("게시글을 찾을 수 없습니다.")
        }
    }

    /**
     * 게시글 수정
     * @param id 게시글의 고유 식별자
     * @param updatedBoard 수정된 게시글
     * @return 수정된 게시글
     * @throws RuntimeException 게시글 수정에 실패했을 때
     */
    @GetMapping("/{id}")
    fun updateById(
        @PathVariable id: String,
        boardRequest: BoardRequest,
    ): BoardResponse {
        val existingBoard = valueOperations.get(id) as? BoardResponse
        if (existingBoard != null) {
            boardRequest.id = existingBoard.id
            valueOperations.set(id, boardRequest)
            return BoardResponse(
                boardRequest.id,
                boardRequest.title,
                boardRequest.content,
                boardRequest.createDate,
            )
        } else {
            throw RuntimeException("게시글을 찾을 수 없습니다.")
        }
    }

    /**
     * 게시글 삭제
     * @param id 게시글의 고유 식별자
     * @throws RuntimeException 게시글 삭제에 실패했을 때
     */
    @GetMapping("/{id}")
    fun deleteById(
        @PathVariable id: String,
    ) {
        runCatching {
            redisTemplate.delete(id)
        }.getOrElse {
            throw RuntimeException("게시글 삭제에 실패했습니다. 원인: ${it.message}")
        }
    }
}
