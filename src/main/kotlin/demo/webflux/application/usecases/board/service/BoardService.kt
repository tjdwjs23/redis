package demo.webflux.application.usecases.board.service

import demo.webflux.application.repositories.board.BoardRepository
import demo.webflux.domain.board.Board
import demo.webflux.domain.board.BoardEntity
import demo.webflux.ports.input.BoardRequest
import demo.webflux.ports.output.BoardResponse
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class BoardService(
        private val boardRepository: BoardRepository,
        private val redisTemplate: RedisTemplate<String, Any>
) {
    private val valueOperations = redisTemplate.opsForValue()

    /**
     * 게시글 저장
     * @param board 게시글
     * @return 저장된 게시글
     */
    @Transactional
    fun save(boardRequest: BoardRequest): Mono<BoardResponse> {
        boardRequest.createdDate = boardRequest.createdDate ?: LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val board = boardRequest.toBoard()

        // RDB에 저장
        val entity = BoardEntity().apply {
            title = board.title
            content = board.content
            createdDate = LocalDateTime.parse(board.createdDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            updatedDate = LocalDateTime.now()
        }

        return boardRepository.save(entity)
                .flatMap { savedEntity ->
                    board.id = savedEntity.id
                    valueOperations.set(savedEntity.id.toString(), board)

                    Mono.just(savedEntity.toResponse())
                }
                .onErrorMap { e -> throw RuntimeException("게시글 저장에 실패했습니다.", e) }
    }

    /**
     * 모든 게시글 조회
     * @return 게시글 목록
     * @throws RuntimeException 게시글 조회에 실패했을 때
     */
    @Transactional
    fun getAll(): Flux<BoardResponse> {
        val redisKeys = redisTemplate.keys("*") ?: emptySet()

        return Flux.fromIterable(redisKeys)
                .flatMap { id ->
                    Mono.justOrEmpty(valueOperations.get(id) as? Board)
                            .map(Board::toBoardResponse)
                }
                .switchIfEmpty(
                        boardRepository.findAll()
                                .map(BoardEntity::toResponse)
                )
                .onErrorMap { e -> throw RuntimeException("게시글 조회에 실패했습니다.", e) }
    }

    /**
     * 특정 게시글 조회
     * @param id 게시글의 고유 식별자
     * @return 게시글
     * @throws RuntimeException 게시글 조회에 실패했을 때
     */
    @Transactional
    fun getById(id: Long): Mono<BoardResponse> {
        return Mono.justOrEmpty(valueOperations.get(id.toString()) as? Board)
                .map(Board::toBoardResponse)
                .switchIfEmpty(
                        boardRepository.findById(id.toString())
                                .map(BoardEntity::toResponse)
                )
                .onErrorMap { e -> throw RuntimeException("게시글 조회에 실패했습니다.", e) }
    }

    /**
     * 게시글 수정
     * @param id 게시글의 고유 식별자
     * @param updatedBoard 수정된 게시글
     * @return 수정된 게시글
     * @throws RuntimeException 게시글 수정에 실패했을 때
     */
    @Transactional
    fun updateById(id: Long, boardRequest: BoardRequest): Mono<BoardResponse> {
        return boardRepository.findById(id.toString())
                .flatMap { entity ->
                    entity.title = boardRequest.title
                    entity.content = boardRequest.content
                    entity.updatedDate = LocalDateTime.now()
                    boardRepository.save(entity)
                }
                .doOnSuccess { savedEntity ->
                    boardRequest.id = savedEntity.id!!
                    boardRequest.createdDate = savedEntity.createdDate!!.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    boardRequest.updatedDate = savedEntity.updatedDate!!.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    valueOperations.set(id.toString(), boardRequest.toBoard())
                }
                .map(BoardEntity::toResponse)
                .onErrorMap { e -> throw RuntimeException("게시글 수정에 실패했습니다.", e) }
    }


    /**
     * 게시글 삭제
     * @param id 게시글의 고유 식별자
     * @throws RuntimeException 게시글 삭제에 실패했을 때
     */
    @Transactional
    fun deleteById(id: Long): Mono<Boolean> {
        return boardRepository.findById(id.toString())
                .flatMap { board ->
                    boardRepository.delete(board)
                            .then(Mono.just(true))
                }
                .defaultIfEmpty(false)
                .doOnSuccess { isDeleted ->
                    if (isDeleted) {
                        redisTemplate.delete(id.toString())
                    }
                }
                .onErrorMap { e -> throw RuntimeException("게시글 삭제에 실패했습니다.", e) }
    }

}

fun BoardEntity.toResponse() = BoardResponse(
        this.id!!,
        this.title!!,
        this.content!!,
        this.createdDate!!.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        this.updatedDate!!.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
)