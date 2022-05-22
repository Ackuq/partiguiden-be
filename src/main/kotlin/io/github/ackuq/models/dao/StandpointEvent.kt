package io.github.ackuq.models.dao

import array
import io.github.ackuq.models.dto.StandpointUpdateEventDTO
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.TextColumnType
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

object StandpointUpdateEvents : IntIdTable(name = "standpoint_update_events", columnName = "id") {
    val newLink: Column<String> = varchar("new_link", 150).uniqueIndex("idx_standpoint_update_events_new_link_unique")
    val newTitle: Column<String> = varchar("new_title", 100)
    val newContent = array<String>("new_content", TextColumnType())
    val newParagraph: Column<String?> = text("new_paragraph").nullable()
    val updateDate: Column<LocalDateTime> = datetime("update_date")
    val party = reference(
        "party",
        Parties,
        onDelete = ReferenceOption.CASCADE,
        fkName = "fk_standpoint_update_events_party"
    ).index()
    val standpoint = reference(
        "standpoint",
        Standpoints,
        onDelete = ReferenceOption.CASCADE,
        fkName = "fk_standpoint_update_events_standpoints"
    ).nullable().index()
}

class StandpointUpdateEvent(id: EntityID<Int>) : IntEntity(id) {
    companion object : EntityClass<Int, StandpointUpdateEvent>(StandpointUpdateEvents)

    var newLink by StandpointUpdateEvents.newLink
    var newTitle by StandpointUpdateEvents.newTitle
    var newContent by StandpointUpdateEvents.newContent
    var newParagraph by StandpointUpdateEvents.newParagraph
    var updateDate by StandpointUpdateEvents.updateDate
    var party by Party referencedOn StandpointUpdateEvents.party
    var standpoint by Standpoint optionalReferencedOn StandpointUpdateEvents.standpoint

    fun toDTO(): StandpointUpdateEventDTO = transaction {
        StandpointUpdateEventDTO(
            id = this@StandpointUpdateEvent.id.value,
            newLink = this@StandpointUpdateEvent.newLink,
            newTitle = this@StandpointUpdateEvent.newTitle,
            newContent = this@StandpointUpdateEvent.newContent.toList(),
            newParagraph = this@StandpointUpdateEvent.newParagraph,
            updateDate = this@StandpointUpdateEvent.updateDate.toString(),
            party = this@StandpointUpdateEvent.party.abbreviation,
            standpoint = this@StandpointUpdateEvent.standpoint?.id?.value,
        )
    }
}

object StandpointDeleteEvents : IntIdTable(name = "standpoint_delete_events", columnName = "id") {
    val standpoint = reference(
        "standpoint",
        Standpoints,
        onDelete = ReferenceOption.CASCADE,
        fkName = "fk_standpoint_delete_events_standpoints"
    ).index()
}

class StandpointDeleteEvent(id: EntityID<Int>) : IntEntity(id) {
    companion object : EntityClass<Int, StandpointDeleteEvent>(StandpointDeleteEvents)

    var standpoint by Standpoint referencedOn StandpointDeleteEvents.standpoint
}
