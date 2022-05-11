package io.github.ackuq.services

import io.github.ackuq.dao.Subject
import io.github.ackuq.dao.Subjects
import io.github.ackuq.dto.NewSubjectDTO
import io.github.ackuq.dto.UpdateSubjectDTO
import io.ktor.server.plugins.BadRequestException
import org.jetbrains.exposed.sql.transactions.transaction

object SubjectService {
    fun getSubject(id: Int): Subject? = transaction {
        Subject.findById(id)
    }

    fun getSubjectByName(name: String): Subject? = transaction {
        Subject.find { Subjects.name eq name }.firstOrNull()
    }

    fun getAllSubjects(): List<Subject> = transaction {
        Subject.all().toList()
    }

    fun createSubject(newSubjectDTO: NewSubjectDTO): Subject = transaction {
        if (getSubjectByName(newSubjectDTO.name) != null) {
            throw BadRequestException("Subject with this name already exists")
        }
        Subject.new {
            name = newSubjectDTO.name
        }
    }

    fun updateSubject(subject: Subject, newData: UpdateSubjectDTO): Subject = transaction {
        newData.name?.let { subject.name = it }
        subject
    }
}
