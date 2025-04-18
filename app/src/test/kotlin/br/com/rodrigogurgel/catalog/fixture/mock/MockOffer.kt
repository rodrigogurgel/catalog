package br.com.rodrigogurgel.catalog.fixture.mock

import br.com.rodrigogurgel.catalog.domain.entity.Customization
import br.com.rodrigogurgel.catalog.domain.entity.Offer
import br.com.rodrigogurgel.catalog.domain.entity.Product
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.domain.vo.Media
import br.com.rodrigogurgel.catalog.domain.vo.Name
import br.com.rodrigogurgel.catalog.domain.vo.Price
import br.com.rodrigogurgel.catalog.domain.vo.Status
import br.com.rodrigogurgel.catalog.fixture.utils.randomString
import java.math.BigDecimal
import java.util.UUID
import kotlin.random.Random

data class MockOffer(
    var id: Id = Id(UUID.randomUUID()),
    var name: Name = Name(randomString(30)),
    var product: Product = mockProduct(),
    var price: Price = Price(BigDecimal.valueOf(Random.nextDouble(0.1, 100.0))),
    var status: Status = Status.AVAILABLE,
    var customizations: MutableList<Customization> = mutableListOf(),
    var medias: List<Media> = emptyList(),
)

fun mockOffer(): Offer =
    MockOffer().run {
        Offer(Id(UUID.randomUUID()), name, product, price, status, customizations, medias)
    }

fun mockOfferWith(block: MockOffer.() -> Unit): Offer =
    MockOffer()
        .apply(block)
        .run { Offer(id, name, product, price, status, customizations, medias) }
