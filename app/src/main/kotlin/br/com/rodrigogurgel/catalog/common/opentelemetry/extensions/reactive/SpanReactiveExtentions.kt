package br.com.rodrigogurgel.catalog.common.opentelemetry.extensions.reactive

import br.com.rodrigogurgel.catalog.common.opentelemetry.extensions.spanBuilder
import io.opentelemetry.api.trace.Span
import io.opentelemetry.api.trace.SpanBuilder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

inline fun <T> monoSpan(
    name: String,
    builder: SpanBuilder.() -> Unit = { },
    crossinline block: (span: Span?) -> Mono<T>
): Mono<T> {
    val span = spanBuilder("inline-reactive-span", name)
        .apply { builder() }
        .startSpan()

    val scope = span.makeCurrent()
    return runCatching {
        block(span).doFinally {
            span.end()
            scope.close()
        }
    }.onFailure {
        span.recordException(it)
    }.getOrThrow()
}

inline fun <T> fluxSpan(
    name: String,
    builder: SpanBuilder.() -> Unit = { },
    crossinline block: (span: Span?) -> Flux<T>
): Flux<T> {
    val span = spanBuilder("inline-reactive-span", name)
        .apply { builder() }
        .startSpan()

    val scope = span.makeCurrent()

    return runCatching {
        block(span).doFinally {
            span.end()
            scope.close()
        }
    }.onFailure {
        span.recordException(it)
    }.getOrThrow()
}
