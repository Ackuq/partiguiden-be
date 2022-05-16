package io.github.ackuq.models.dao

import array
import io.github.ackuq.models.dto.StandpointDTO
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

object Standpoints : IntIdTable(name = "standpoints", columnName = "id") {
    // Hashed version of the url to identify the objects
    val link: Column<String> = varchar("link", 150).uniqueIndex()
    val title: Column<String> = varchar("title", 100)

    val content = array<String>("content", TextColumnType())
    val updateDate: Column<LocalDateTime> = datetime("update_date")
    val party = reference(
        "party",
        Parties,
        onDelete = ReferenceOption.CASCADE,
        fkName = "fk_standpoints_party"
    )
    val subject = reference(
        "subject",
        Subjects,
        onDelete = ReferenceOption.SET_NULL,
        fkName = "fk_standpoints_subject"
    ).nullable()
}

class Standpoint(id: EntityID<Int>) : IntEntity(id) {
    companion object : EntityClass<Int, Standpoint>(Standpoints)

    var link by Standpoints.link
    var title by Standpoints.title
    var content by Standpoints.content
    var updateDate by Standpoints.updateDate
    var party by Party referencedOn Standpoints.party
    var subject by Subject optionalReferencedOn Standpoints.subject

    fun toDTO(): StandpointDTO = transaction {
        StandpointDTO(
            id = this@Standpoint.id.value,
            link = this@Standpoint.link,
            title = this@Standpoint.title,
            content = this@Standpoint.content.toList(),
            updateDate = this@Standpoint.updateDate.toString(),
            party = this@Standpoint.party.abbreviation,
            subject = this@Standpoint.subject?.id?.value,
        )
    }
}
