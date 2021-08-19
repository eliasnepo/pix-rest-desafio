package br.com.zupacademy.shared.validations

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.hateoas.JsonError
import io.micronaut.http.server.exceptions.ExceptionHandler
import javax.inject.Singleton

@Singleton
class ExceptionHandler : ExceptionHandler<StatusRuntimeException, HttpResponse<Any>> {

    override fun handle(request: HttpRequest<*>, exception: StatusRuntimeException): HttpResponse<Any> {

        val statusCode = exception.status.code
        val statusDescription = exception.status.description

        val httpStatus = when (statusCode) {
            Status.NOT_FOUND.code -> HttpStatus.NOT_FOUND
            Status.INVALID_ARGUMENT.code -> HttpStatus.BAD_REQUEST
            Status.ALREADY_EXISTS.code -> HttpStatus.UNPROCESSABLE_ENTITY
            Status.PERMISSION_DENIED.code -> HttpStatus.BAD_REQUEST
            Status.FAILED_PRECONDITION.code -> HttpStatus.UNPROCESSABLE_ENTITY
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }

        return HttpResponse.status<Any?>(httpStatus).body(JsonError(statusDescription))
    }
}