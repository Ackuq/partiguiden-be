package io.github.ackuq.routes.resources

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/subjects")
class Subjects {
    @Serializable
    @Resource("{id}")
    class Id(@Suppress("unused") val parent: Subjects = Subjects(), val id: Int)
}
