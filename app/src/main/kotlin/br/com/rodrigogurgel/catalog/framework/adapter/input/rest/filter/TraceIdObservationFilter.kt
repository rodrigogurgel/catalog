package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.filter

import io.micrometer.observation.Observation.Scope
import io.micrometer.observation.ObservationRegistry
import io.opentelemetry.api.trace.Span
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.web.filter.ServerHttpObservationFilter

@Component
@Profile("!test")
class TraceIdObservationFilter(
    observationRegistry: ObservationRegistry
) : ServerHttpObservationFilter(observationRegistry) {
    companion object {
        private const val TRACE_ID_HEADER = "Trace-Id"
        private const val TRACE_PARENT_HEADER = "traceparent"
        private const val SERVER_TIMING_HEADER = "Server-Timing"
    }

    override fun onScopeOpened(
        scope: Scope,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        Span.current()?.let { span ->
            val traceId = span.spanContext.traceId
            val parentSpanId = span.spanContext.spanId ?: "0"
            val sampled = if (span.spanContext.isSampled) "01" else "00"

            response.setHeader(TRACE_PARENT_HEADER, "00-$traceId-$parentSpanId-$sampled")
            response.setHeader(TRACE_ID_HEADER, traceId)
            response.setHeader(SERVER_TIMING_HEADER, "Trace-ID;desc=$traceId")
        }
    }
}
