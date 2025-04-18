package br.com.rodrigogurgel.catalog.common.opentelemetry.extensions

import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.api.trace.Span
import io.opentelemetry.api.trace.SpanBuilder
import io.opentelemetry.api.trace.Tracer
import io.opentelemetry.context.Context

fun spanBuilder(
    instrumentationScopeName: String,
    spanName: String,
    builder: SpanBuilder.() -> Unit = { },
): SpanBuilder {
    val tracer: Tracer = GlobalOpenTelemetry.getTracer("${object {}.javaClass.packageName} $instrumentationScopeName")
    return tracer.spanBuilder(spanName)
        .setParent(Context.current())
        .apply { builder() }
}

inline fun <T> span(
    name: String,
    builder: SpanBuilder.() -> Unit = { },
    crossinline block: (span: Span?) -> T
): T {
    val span: Span = spanBuilder("inline-span", name)
        .apply { builder() }
        .startSpan()

    span.makeCurrent().use {
        return runCatching {
            block(span)
        }.onFailure {
            span.recordException(it)
        }.also {
            span.end()
        }.getOrThrow()
    }
}

@Suppress("NOTHING_TO_INLINE", "unused")
inline fun defaultSpanName(): String {
    val callingStackFrame = Thread.currentThread().stackTrace[1]

    val simpleClassName = Class.forName(callingStackFrame.className).simpleName
    val methodName = callingStackFrame.methodName

    return "$simpleClassName.$methodName"
}
