package io.github.ackuq.configuration

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import java.time.Duration

object Storage {
    data class State(val redirectPath: String)

    val stateCache: Cache<String, State> =
        Caffeine.newBuilder().maximumSize(10_000)
            .expireAfterWrite(Duration.ofMinutes(5))
            .build()
}
