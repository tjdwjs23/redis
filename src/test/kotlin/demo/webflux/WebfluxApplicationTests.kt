package demo.webflux

import demo.webflux.adapters.ui.board.BoardController
import demo.webflux.adapters.ui.user.UserController
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class WebfluxApplicationTests {

    @Autowired
    lateinit var boardController: BoardController

    @Autowired
    lateinit var userController: UserController

    @Test
    fun contextLoads() {
        assertThat(boardController).isNotNull
        assertThat(userController).isNotNull
    }
}
