package br.com.rodrigogurgel.catalog.domain.vo

import br.com.rodrigogurgel.catalog.domain.exception.MalformedURLException
import br.com.rodrigogurgel.catalog.domain.vo.MediaType.GIF
import br.com.rodrigogurgel.catalog.domain.vo.MediaType.IMAGE
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class MediaTest {
    @Test
    fun `Should instantiate Media with success when path is a valid url`() {
        val path = "https://www.example.com"
        val medias = Media(path, GIF)

        medias.url shouldBe path
    }

    @Test
    fun `Should instantiate Media with error when path is not a valid url`() {
        val path = "www.example.com"
        shouldThrow<MalformedURLException> {
            Media(path, IMAGE)
        }
    }
}
