package io.github.ackuq

import io.github.ackuq.dao.Parties
import io.github.ackuq.dao.Standpoints
import io.github.ackuq.dao.Subjects
import io.github.ackuq.utils.TestDatabaseFactory
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SchemaTest {

    private val databaseFactory: TestDatabaseFactory = TestDatabaseFactory()

    @BeforeTest
    fun setup() {
        databaseFactory.init()
    }

    @AfterTest
    fun tearDown() {
        databaseFactory.close()
    }

    @Test
    fun testSchema() {
        val tables = arrayOf<Table>(
            Parties,
            Standpoints,
            Subjects
        )

        transaction {
            assertEquals(emptyList(), SchemaUtils.statementsRequiredToActualizeScheme(*tables))
            assertEquals(emptyList(), SchemaUtils.addMissingColumnsStatements(*tables))
            assertEquals(emptyList(), SchemaUtils.checkMappingConsistence(*tables))
        }
    }
}
