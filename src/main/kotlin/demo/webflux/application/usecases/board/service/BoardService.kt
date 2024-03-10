package demo.webflux.application.usecases.board.service

import demo.webflux.application.repositories.board.BoardRedisRepository
import demo.webflux.application.repositories.board.BoardRepository
import demo.webflux.domain.board.Board
import demo.webflux.domain.board.BoardEntity
import demo.webflux.ports.input.BoardRequest
import demo.webflux.ports.output.BoardResponse
import io.github.oshai.kotlinlogging.KotlinLogging
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
    private val boardRedisRepository: BoardRedisRepository
) {

    val log = KotlinLogging.logger {}

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
            writeId = board.writeId
            createdDate = LocalDateTime.parse(board.createdDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            updatedDate = LocalDateTime.now()
        }

        return boardRepository.save(entity)
                .zipWhen({ savedEntity ->
                    // 저장된 Entity 정보를 board에 반영
                    board.id = savedEntity.id
                    board.writeId = savedEntity.writeId
                    // Redis에 저장
                    boardRedisRepository.save(board).subscribe({
                        // 성공했을 때의 처리
                    }, { e ->
                        // 에러가 발생했을 때의 처리
                        log.error { "save error on Board cache. ${e}" }
                    })
                    Mono.just(savedEntity)
                }) { entity, _ ->
                    entity.toResponse()
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
        return try {
            boardRedisRepository.findAll()
                .map(Board::toBoardResponse)
        } catch (e: Exception) {
            log.error { "Received error on Board cache. ${e}"}
            boardRepository.findAll()
                .map(BoardEntity::toResponse)
        }.onErrorMap { e -> throw RuntimeException("게시글 조회에 실패했습니다.", e) }
    }




    /**
     * 특정 게시글 조회
     * @param id 게시글의 고유 식별자
     * @return 게시글
     * @throws RuntimeException 게시글 조회에 실패했을 때
     */
    @Transactional
    fun getById(id: Long): Mono<BoardResponse> {
        return boardRedisRepository.findById(id.toString())
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
            .zipWhen({ entity ->
                entity.title = boardRequest.title
                entity.content = boardRequest.content
                entity.writeId = boardRequest.writeId
                entity.updatedDate = LocalDateTime.now()

                boardRepository.save(entity)
            }, { entity, savedEntity ->
                if (savedEntity != null) {
                    boardRequest.id = savedEntity.id!!
                    boardRequest.writeId = savedEntity.writeId
                    boardRequest.createdDate = savedEntity.createdDate?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    boardRequest.updatedDate = savedEntity.updatedDate?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    boardRedisRepository.save(boardRequest.toBoard()).subscribe()
                }
                entity.toResponse()
            })
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
                .zipWhen({ board ->
                    boardRepository.delete(board)
                            .then(Mono.just(true))
                }, { _, result ->
                    if (result) {
                        boardRedisRepository.deleteById(id.toString()).subscribe()
                    }
                    result
                })
                .defaultIfEmpty(false)
                .onErrorMap { e -> throw RuntimeException("게시글 삭제에 실패했습니다.", e) }
    }

}

fun BoardEntity.toResponse() = BoardResponse(
        this.id!!,
        this.title!!,
        this.content!!,
        this.writeId!!,
        this.createdDate!!.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        this.updatedDate!!.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
)