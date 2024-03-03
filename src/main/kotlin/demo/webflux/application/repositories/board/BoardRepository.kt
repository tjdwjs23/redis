package demo.webflux.application.repositories.board

import demo.webflux.domain.board.BoardEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface BoardRepository: ReactiveCrudRepository<BoardEntity, String>