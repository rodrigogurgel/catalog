package br.com.rodrigogurgel.catalog.domain.vo

import br.com.rodrigogurgel.catalog.domain.exception.MalformedURLException
import java.net.URL

/**
 * Represents a file of media with path and type.
 *
 * @throws MalformedURLException if the [path] was blank or isn't a valid URL.
 */
class Media(
    val path: String,
    val type: MediaType
) {
    init {
        runCatching {
            URL(path)
        }.getOrElse { throw MalformedURLException(path) }
    }

    override fun toString(): String {
        return path
    }
}
