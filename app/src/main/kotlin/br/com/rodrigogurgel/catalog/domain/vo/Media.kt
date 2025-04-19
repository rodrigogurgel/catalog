package br.com.rodrigogurgel.catalog.domain.vo

import br.com.rodrigogurgel.catalog.domain.exception.MalformedURLException
import java.net.URL

/**
 * Represents a file of media with path and type.
 *
 * @throws MalformedURLException if the [url] was blank or isn't a valid URL.
 */
class Media(
    val url: String,
    val type: MediaType
) {
    init {
        runCatching {
            URL(url)
        }.getOrElse { throw MalformedURLException(url) }
    }

    override fun toString(): String {
        return url
    }
}
