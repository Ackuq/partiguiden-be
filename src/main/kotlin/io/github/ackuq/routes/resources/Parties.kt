package io.github.ackuq.routes.resources

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/parties")
class Parties {
    @Serializable
    @Resource("{abbreviation}")
    class Abbreviation(@Suppress("unused") val parent: Parties = Parties(), val abbreviation: String) {
        @Serializable
        @Resource("scrape")
        class Scrape(val parent: Abbreviation) {
            @Serializable
            @Resource("dry")
            class Dry(val parent: Scrape, val max: Int? = 10)
        }
    }
}
