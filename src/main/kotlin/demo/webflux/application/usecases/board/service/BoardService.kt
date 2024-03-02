package demo.webflux.application.usecases.board.service

import demo.webflux.domain.board.Board
import demo.webflux.ports.input.BoardRequest
import demo.webflux.ports.output.BoardResponse
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class BoardService(private val redisTemplate: RedisTemplate<String, Any>) {
    private val valueOperations = redisTemplate.opsForValue()

    /**
     * 게시글 저장
     * @param board 게시글
     * @return 저장된 게시글
     */
    fun save(boardRequest: BoardRequest): BoardResponse {
        boardRequest.createDate = boardRequest.createDate ?: LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        return boardRequest.toBoard().also { valueOperations.set(it.id, it) }.toBoardResponse()
    }

    /**
     * 모든 게시글 조회
     * @return 게시글 목록
     * @throws RuntimeException 게시글 조회에 실패했을 때
     */
    fun getAll(): List<BoardResponse> {
        return (redisTemplate.keys("*") ?: emptySet()).mapNotNull { id ->
            (valueOperations.get(id) as? Board)?.toBoardResponse()
        }
    }

    /**
     * 특정 게시글 조회
     * @param id 게시글의 고유 식별자
     * @return 게시글
     * @throws RuntimeException 게시글 조회에 실패했을 때
     */
    fun getById(id: String): BoardResponse {
        return (valueOperations.get(id) as? Board)?.toBoardResponse() ?: throw RuntimeException("게시글을 찾을 수 없습니다.")
    }

    /**
     * 게시글 수정
     * @param id 게시글의 고유 식별자
     * @param updatedBoard 수정된 게시글
     * @return 수정된 게시글
     * @throws RuntimeException 게시글 수정에 실패했을 때
     */
    fun updateById(id : String, boardRequest: BoardRequest): BoardResponse {
        return (valueOperations.get(id) as? Board)?.let { existingBoard ->
            boardRequest.createDate = existingBoard.createDate
            boardRequest.toBoard().also { valueOperations.set(id, it) }.toBoardResponse()
        } ?: throw RuntimeException("게시글을 찾을 수 없습니다.")
    }

    /**
     * 게시글 삭제
     * @param id 게시글의 고유 식별자
     * @throws RuntimeException 게시글 삭제에 실패했을 때
     */
    fun deleteById(id: String) {
        redisTemplate.delete(id) ?: throw RuntimeException("게시글 삭제에 실패했습니다.")
    }
}
