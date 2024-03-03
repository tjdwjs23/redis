package demo.webflux.application.usecases.board.service

import demo.webflux.application.repositories.board.BoardRepository
import demo.webflux.domain.board.Board
import demo.webflux.domain.board.BoardEntity
import demo.webflux.ports.input.BoardRequest
import demo.webflux.ports.output.BoardResponse
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
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

        return boardRepository.save(entity).flatMap { savedEntity ->
            board.id = savedEntity.id
            // Redis에 저장
            valueOperations.set(savedEntity.id.toString(), board)

            Mono.just(
                BoardResponse(
                    savedEntity.id!!,
                    savedEntity.title!!,
                    savedEntity.content!!,
                    savedEntity.createdDate!!.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    savedEntity.updatedDate!!.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
            )
        }
    }



    /**
     * 모든 게시글 조회
     * @return 게시글 목록
     * @throws RuntimeException 게시글 조회에 실패했을 때
     */
    fun getAll(): Flux<BoardResponse> {
        val redisKeys = redisTemplate.keys("*") ?: emptySet<String>()
        return Flux.fromIterable(redisKeys)
                .flatMap { id ->
                    Mono.justOrEmpty(valueOperations.get(id) as? Board)
                            .map(Board::toBoardResponse)
                }
                .collectList()
                .flatMapMany { list ->
                    if (list.isEmpty()) {
                        boardRepository.findAll().map { entity ->
                            BoardResponse(
                                    entity.id!!,
                                    entity.title!!,
                                    entity.content!!,
                                    entity.createdDate!!.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                                    entity.updatedDate!!.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                            )
                        }
                    } else {
                        Flux.fromIterable(list)
                    }
                }
    }


    /**
     * 특정 게시글 조회
     * @param id 게시글의 고유 식별자
     * @return 게시글
     * @throws RuntimeException 게시글 조회에 실패했을 때
     */
    fun getById(id: Long): Mono<BoardResponse> {
        return Mono.justOrEmpty(valueOperations.get(id.toString()) as? Board)
                .map(Board::toBoardResponse)
                .switchIfEmpty(
                        boardRepository.findById(id.toString()).map { entity ->
                            BoardResponse(
                                    entity.id!!,
                                    entity.title!!,
                                    entity.content!!,
                                    entity.createdDate!!.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                                    entity.updatedDate!!.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                            )
                        }
                )
    }

    /**
     * 게시글 수정
     * @param id 게시글의 고유 식별자
     * @param updatedBoard 수정된 게시글
     * @return 수정된 게시글
     * @throws RuntimeException 게시글 수정에 실패했을 때
     */
    fun updateById(id: Long, boardRequest: BoardRequest): Mono<BoardResponse> {
        return boardRepository.findById(id.toString())
                .flatMap { entity ->
                    entity.title = boardRequest.title
                    entity.content = boardRequest.content
                    entity.updatedDate = LocalDateTime.now()
                    boardRepository.save(entity)
                }
                .doOnSuccess { savedEntity ->
                    valueOperations.set(id.toString(), boardRequest.toBoard())
                }
                .map { savedEntity ->
                    BoardResponse(
                            savedEntity.id!!,
                            savedEntity.title!!,
                            savedEntity.content!!,
                            savedEntity.createdDate!!.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                            savedEntity.updatedDate!!.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )
                }
    }


    /**
     * 게시글 삭제
     * @param id 게시글의 고유 식별자
     * @throws RuntimeException 게시글 삭제에 실패했을 때
     */
    fun deleteById(id: Long): Mono<Void> {
        return boardRepository.deleteById(id.toString())
                .doOnSuccess {
                    redisTemplate.delete(id.toString())
                }
    }
}
