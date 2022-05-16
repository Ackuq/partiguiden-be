package io.github.ackuq.routes

import io.github.ackuq.configuration.OAuthConfiguration
import io.github.ackuq.models.dto.NewSubjectDTO
import io.github.ackuq.models.dto.UpdateSubjectDTO
import io.github.ackuq.models.services.SubjectService
import io.github.ackuq.routes.resources.Subjects
import io.github.ackuq.routes.utils.handleApiSuccess
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.request.receive
import io.ktor.server.resources.get
import io.ktor.server.resources.patch
import io.ktor.server.resources.post
import io.ktor.server.routing.Route

fun Route.subjectRoutes() {
    get<Subjects> {
        val subjects = SubjectService.getAllSubjects().map { it.toDTO() }
        handleApiSuccess(subjects, HttpStatusCode.OK, call)
    }
    get<Subjects.Id> {
        val subject = SubjectService.getSubject(it.id)
            ?: throw NotFoundException("Subject could not be found")
        handleApiSuccess(subject.toDetailedDTO(), HttpStatusCode.OK, call)
    }
    authenticate(OAuthConfiguration.authName) {
        post<Subjects> {
            val newSubject = call.receive<NewSubjectDTO>()
            val subject = SubjectService.createSubject(newSubject)
            handleApiSuccess(subject.toDTO(), HttpStatusCode.Created, call)
        }
        patch<Subjects.Id> {
            val subject = SubjectService.getSubject(it.id)
                ?: throw NotFoundException("Subject could not be found")
            val updateSubjectDTO = call.receive<UpdateSubjectDTO>()
            val updatedSubject = SubjectService.updateSubject(subject, updateSubjectDTO)
            handleApiSuccess(updatedSubject.toDTO(), HttpStatusCode.OK, call)
        }
    }
}
