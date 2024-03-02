package demo.webflux.ports.output

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class BoardResponse @JsonCreator
constructor(
        @JsonProperty("id") var id: String,
        @JsonProperty("title") val title: String,
        @JsonProperty("content") val content: String,
        @JsonProperty("createDate") var createDate: String? = null,
)