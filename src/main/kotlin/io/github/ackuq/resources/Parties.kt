package io.github.ackuq.resources

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
        class Scrape(@Suppress("unused") val parent: Abbreviation)
    }
}
