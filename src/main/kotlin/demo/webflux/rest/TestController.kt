package demo.webflux.rest

import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "example", description = "Swagger 테스트용 API")
@RestController
@RequestMapping("/")
class ExampleController {
    @Operation(summary = "문자열 반복", description = "파라미터로 받은 문자열을 2번 반복합니다.")
    @Parameter(name = "str", description = "2번 반복할 문자열")
    @GetMapping("/returnStr")
    fun returnStr(
        @RequestParam str: String,
    ) = "$str\n$str"

    @GetMapping("/example")
    fun example() = "예시 API"

    @Hidden
    @GetMapping("/ignore")
    fun ignore() = "무시되는 API"
}
