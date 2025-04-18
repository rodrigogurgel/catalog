package br.com.rodrigogurgel.catalog.fixture.mock

import br.com.rodrigogurgel.catalog.domain.entity.Customization
import br.com.rodrigogurgel.catalog.domain.entity.Option
import br.com.rodrigogurgel.catalog.domain.entity.Product
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.domain.vo.Media
import br.com.rodrigogurgel.catalog.domain.vo.Name
import br.com.rodrigogurgel.catalog.domain.vo.Price
import br.com.rodrigogurgel.catalog.domain.vo.Quantity
import br.com.rodrigogurgel.catalog.domain.vo.Status
import br.com.rodrigogurgel.catalog.domain.vo.Status.AVAILABLE
import br.com.rodrigogurgel.catalog.fixture.utils.randomString
import java.math.BigDecimal
import kotlin.random.Random

data class MockOption(
    var id: Id = Id(),
    var name: Name = Name(randomString(30)),
    var product: Product = mockProduct(),
    var price: Price = Price(BigDecimal.valueOf(Random.nextDouble(0.0, 100.0))),
    var quantity: Quantity = Quantity(0, 1),
    var status: Status = AVAILABLE,
    var customizations: MutableList<Customization> = mutableListOf(),
    var medias: List<Media> = emptyList(),
)

fun mockOption() =
    MockOption()
        .run { Option(Id(), name, product, quantity, status, price, customizations, medias) }

fun mockOptionWith(block: MockOption.() -> Unit): Option =
    MockOption()
        .apply(block)
        .run { Option(id, name, product, quantity, status, price, customizations, medias) }
