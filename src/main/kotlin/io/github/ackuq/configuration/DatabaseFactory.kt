package io.github.ackuq.configuration

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.HoconApplicationConfig
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.slf4j.LoggerFactory

interface IDatabaseFactory {
    fun init()
}

object DatabaseFactory : IDatabaseFactory {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val appConfig = HoconApplicationConfig(ConfigFactory.load())
    private val dbURL = appConfig.property("db.jdbcURL").getString()
    private val dbUser = appConfig.property("db.dbUser").getString()
    private val dbPassword = appConfig.property("db.dbPassword").getString()

    override fun init() {
        Database.connect(hikari())
        val flyway = Flyway.configure().dataSource(dbURL, dbUser, dbPassword).load()
        flyway.migrate()
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = "org.postgresql.Driver"
        config.jdbcUrl = dbURL
        config.username = dbUser
        config.password = dbPassword
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)
    }
}
