package br.com.rodrigogurgel.catalog.common.opentelemetry.extensions.coroutines

import br.com.rodrigogurgel.catalog.common.opentelemetry.extensions.spanBuilder
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.getOrThrow
import com.github.michaelbull.result.onFailure
import io.opentelemetry.api.trace.Span
import io.opentelemetry.api.trace.SpanBuilder
import io.opentelemetry.extension.kotlin.asContextElement
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext

@OptIn(DelicateCoroutinesApi::class)
suspend inline fun <T> suspendSpan(
    name: String,
    builder: SpanBuilder.() -> Unit = { },
    crossinline block: suspend (span: Span?) -> T
): T {
    val span: Span = spanBuilder("inline-suspend-span", name)
        .apply {
            coroutineContext[CoroutineName]?.let { setAttribute("coroutine.name", it.name) }
        }
        .apply { builder() }
        .startSpan()

    return withContext(span.asContextElement() + MDCContext()) {
        runSuspendCatching {
            block(span)
        }.onFailure {
            span.recordException(it)
        }.also {
            span.end()
        }.getOrThrow()
    }
}
