package br.com.rodrigogurgel.catalog.fixture.mock

import br.com.rodrigogurgel.catalog.domain.entity.Customization
import br.com.rodrigogurgel.catalog.domain.entity.Offer
import br.com.rodrigogurgel.catalog.domain.entity.Product
import br.com.rodrigogurgel.catalog.domain.vo.Description
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.domain.vo.Media
import br.com.rodrigogurgel.catalog.domain.vo.Name
import br.com.rodrigogurgel.catalog.domain.vo.Price
import br.com.rodrigogurgel.catalog.domain.vo.Status
import br.com.rodrigogurgel.catalog.domain.vo.Status.AVAILABLE
import br.com.rodrigogurgel.catalog.fixture.utils.randomString
import java.math.BigDecimal
import kotlin.random.Random

data class MockOffer(
    var id: Id = Id(),
    var name: Name = Name(randomString(30)),
    var description: Description? = Description(randomString(100)),
    var product: Product = mockProduct(),
    var price: Price = Price(BigDecimal.valueOf(Random.nextDouble(0.1, 100.0))),
    var status: Status = AVAILABLE,
    var customizations: MutableList<Customization> = mutableListOf(),
    var medias: List<Media> = List(Random.nextInt(0, 11)) {
        mockMedia()
    },
)

fun mockOffer(): Offer =
    MockOffer().run {
        Offer(Id(), name, description, product, price, status, customizations, medias)
    }

fun mockOfferWith(block: MockOffer.() -> Unit): Offer =
    MockOffer()
        .apply(block)
        .run { Offer(id, name, description, product, price, status, customizations, medias) }
