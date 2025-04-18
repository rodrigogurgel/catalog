package br.com.rodrigogurgel.catalog.fixture.mock

import br.com.rodrigogurgel.catalog.domain.entity.Product
import br.com.rodrigogurgel.catalog.domain.vo.Description
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.domain.vo.Media
import br.com.rodrigogurgel.catalog.domain.vo.MediaType.IMAGE
import br.com.rodrigogurgel.catalog.domain.vo.Name
import br.com.rodrigogurgel.catalog.fixture.utils.randomString
import java.util.UUID

data class MockProduct(
    var id: Id = Id(UUID.randomUUID()),
    var name: Name = Name(randomString(30)),
    var description: Description? = Description(randomString(100)),
    var medias: List<Media> = listOf(Media("https://www.${randomString(5)}.com", IMAGE)),
)

fun mockProduct() =
    MockProduct().run {
        Product(Id(UUID.randomUUID()), name, description, medias)
    }

fun mockProductWith(block: MockProduct.() -> Unit) =
    MockProduct()
        .apply(block)
        .run { Product(id, name, description, medias) }
