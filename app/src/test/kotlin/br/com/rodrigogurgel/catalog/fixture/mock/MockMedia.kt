package br.com.rodrigogurgel.catalog.fixture.mock

import br.com.rodrigogurgel.catalog.domain.vo.Media
import br.com.rodrigogurgel.catalog.domain.vo.MediaType
import br.com.rodrigogurgel.catalog.fixture.utils.randomString

data class MockMedia(
    var url: String = "https://www.${randomString(5)}.com",
    var type: MediaType = MediaType.entries.toTypedArray().random(),
)

fun mockMedia(): Media = MockMedia().run { Media(url, type) }

fun mockMediaWith(block: MockMedia.() -> Unit): Media =
    MockMedia()
        .apply(block)
        .run { Media(url, type) }
