package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.extensions

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity

fun <V> success(value: V, statusCode: HttpStatusCode = HttpStatus.OK): ResponseEntity<V> {
    return when (value) {
        Unit -> ResponseEntity.status(statusCode).build()
        else -> ResponseEntity.status(statusCode).body(value)
    }
}

fun <T> failure(
    throwable: Throwable,
    statusCode: HttpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR,
    vararg properties: Pair<String, String>
): ResponseEntity<T> {
    val problemDetail = ProblemDetail.forStatusAndDetail(statusCode, throwable.message)
    problemDetail.title = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase
    problemDetail.status = statusCode.value()
    problemDetail.properties = mapOf(*properties)

    return ResponseEntity.of(problemDetail).build()
}
