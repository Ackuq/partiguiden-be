package io.github.ackuq.dao

import io.github.ackuq.dto.SubjectDTO
import io.github.ackuq.dto.SubjectDetailedDTO
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.transactions.transaction

object Subjects : IntIdTable(name = "subjects", columnName = "id") {
    val name: Column<String> = varchar("name", 255).uniqueIndex()
}

class Subject(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Subject>(Subjects)

    var name by Subjects.name
    val standpoints by Standpoint optionalReferrersOn Standpoints.subject

    fun toDTO(): SubjectDTO = transaction {
        SubjectDTO(
            id = this@Subject.id.value,
            name = this@Subject.name,
            standpoints = this@Subject.standpoints.map { it.id.value }
        )
    }

    fun toDetailedDTO(): SubjectDetailedDTO = transaction {
        SubjectDetailedDTO(
            id = this@Subject.id.value,
            name = this@Subject.name,
            standpoints = this@Subject.standpoints.map { it.toDTO() }
        )
    }
}
