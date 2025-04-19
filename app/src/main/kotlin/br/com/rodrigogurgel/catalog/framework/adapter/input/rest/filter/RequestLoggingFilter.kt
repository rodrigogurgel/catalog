package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.filter

import br.com.rodrigogurgel.catalog.common.logger.extensions.requestReceived
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.requestservelet.CachedBodyHttpServletRequest
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Profile("!test")
class RequestLoggingFilter : OncePerRequestFilter() {
    private val loggerSlf4j = LoggerFactory.getLogger(this::class.java)

    companion object {
        private const val REQUEST_URI = "request_uri"
        private const val CONTEXT_PATH = "context_path"
        private const val METHOD = "method"
        private const val PARAMETER_MAP = "parameter_map"
        private const val HEADERS = "headers"
        private const val CONTENT_LENGTH = "content_length"
        private const val QUERY_STRING = "query_string"
        private const val BODY = "body"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val cachedRequest = CachedBodyHttpServletRequest(request)
        loggerSlf4j.requestReceived(
            REQUEST_URI to request.requestURI,
            CONTEXT_PATH to request.contextPath,
            METHOD to request.method,
            PARAMETER_MAP to request.parameterMap,
            HEADERS to request.headerNames.asSequence().map { it to request.getHeader(it) }.toMap(),
            CONTENT_LENGTH to request.contentLength,
            QUERY_STRING to request.queryString,
            BODY to cachedRequest.body(),
        )
        filterChain.doFilter(cachedRequest, response)
    }
}
