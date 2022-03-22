package io.github.ackuq.resources

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/standpoints")
class Standpoints {
    @Serializable
    @Resource("{id}")
    class Id(val parent: Standpoints = Standpoints(), val id: Long)
}