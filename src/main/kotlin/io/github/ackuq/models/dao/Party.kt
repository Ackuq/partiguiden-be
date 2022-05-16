package io.github.ackuq.models.dao

import io.github.ackuq.models.dto.PartyDTO
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.transactions.transaction

object Parties : IntIdTable(name = "parties", columnName = "id") {
    val name: Column<String> = varchar("name", 255).uniqueIndex()
    val abbreviation: Column<String> = varchar("abbreviation", 2).uniqueIndex()
}

class Party(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Party>(Parties)

    var name by Parties.name
    var abbreviation by Parties.abbreviation

    fun toDTO(): PartyDTO = transaction {
        PartyDTO(
            id = this@Party.id.value,
            name = this@Party.name,
            abbreviation = this@Party.abbreviation
        )
    }
}
