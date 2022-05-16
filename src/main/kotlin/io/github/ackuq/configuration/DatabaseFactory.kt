package io.github.ackuq.configuration

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.ApplicationConfig
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database

data class DataSourceConfig(
    val jdbcUrl: String,
    val driverClassName: String,
    val dbUser: String,
    val dbPassword: String,
    val maximumPoolSize: Int,
    val autoCommit: Boolean,
) {
    companion object {
        fun fromApplicationConfig(
            applicationConfig: ApplicationConfig
        ) = DataSourceConfig(
            jdbcUrl = applicationConfig.property("storage.jdbcURL").getString(),
            driverClassName = applicationConfig.property("storage.driverClassName").getString(),
            dbUser = applicationConfig.property("storage.dbUser").getString(),
            dbPassword = applicationConfig.property("storage.dbPassword").getString(),
            maximumPoolSize = applicationConfig.property("storage.poolSize").getString().toInt(),
            autoCommit = applicationConfig.property("storage.autoCommit").getString().toBoolean(),
        )
    }
}

object DatabaseFactory {
    lateinit var db: Database
    lateinit var flyway: Flyway

    fun init(
        dataSourceConfig: DataSourceConfig
    ) {
        val dataSource = hikari(dataSourceConfig)
        db = Database.connect(dataSource)
        flyway = Flyway.configure().dataSource(dataSource).load()
        flyway.migrate()
    }

    private fun hikari(dataSourceConfig: DataSourceConfig): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName =
            dataSourceConfig.driverClassName
        config.jdbcUrl = dataSourceConfig.jdbcUrl
        config.username = dataSourceConfig.dbUser
        config.password =
            dataSourceConfig.dbPassword
        config.maximumPoolSize =
            dataSourceConfig.maximumPoolSize
        config.isAutoCommit = dataSourceConfig.autoCommit
        config.validate()
        return HikariDataSource(config)
    }
}
