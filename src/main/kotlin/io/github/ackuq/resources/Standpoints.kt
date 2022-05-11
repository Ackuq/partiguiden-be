package io.github.ackuq.resources

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/standpoints")
class Standpoints {
    @Serializable
    @Resource("{id}")
    class Id(@Suppress("unused") val parent: Standpoints = Standpoints(), val id: Int)
}
