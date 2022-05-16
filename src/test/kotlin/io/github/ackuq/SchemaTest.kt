package io.github.ackuq

import io.github.ackuq.configuration.DataSourceConfig
import io.github.ackuq.configuration.DatabaseFactory
import io.github.ackuq.models.dao.Parties
import io.github.ackuq.models.dao.Standpoints
import io.github.ackuq.models.dao.Subjects
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

val TestDatabaseConfig = DataSourceConfig(
    jdbcUrl = "jdbc:h2:mem:test;DATABASE_TO_UPPER=false;MODE=PostgreSQL",
    driverClassName = "org.h2.Driver",
    dbUser = "sa",
    dbPassword = "",
    maximumPoolSize = 3,
    autoCommit = false,
)

class SchemaTest {

    @BeforeTest
    fun initDB() {
        DatabaseFactory.init(TestDatabaseConfig)
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
