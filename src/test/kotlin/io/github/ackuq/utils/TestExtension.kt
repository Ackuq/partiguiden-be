package io.github.ackuq.utils

import io.github.ackuq.dao.Parties
import io.github.ackuq.dao.Standpoints
import io.github.ackuq.dao.Subjects
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.AfterTest

open class TestExtension {
    @AfterTest
    fun clearDB() {
        transaction {
            Parties.deleteAll()
            Standpoints.deleteAll()
            Subjects.deleteAll()
        }
    }
}
