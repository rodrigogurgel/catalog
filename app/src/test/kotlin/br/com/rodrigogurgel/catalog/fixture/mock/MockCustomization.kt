package br.com.rodrigogurgel.catalog.fixture.mock

import br.com.rodrigogurgel.catalog.domain.entity.Customization
import br.com.rodrigogurgel.catalog.domain.entity.Option
import br.com.rodrigogurgel.catalog.domain.vo.Description
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.domain.vo.Name
import br.com.rodrigogurgel.catalog.domain.vo.Quantity
import br.com.rodrigogurgel.catalog.domain.vo.Status
import br.com.rodrigogurgel.catalog.domain.vo.Status.AVAILABLE
import br.com.rodrigogurgel.catalog.fixture.utils.randomString

data class MockCustomization(
    var id: Id = Id(),
    var name: Name = Name(randomString(30)),
    var description: Description? = Description(randomString(100)),
    var quantity: Quantity = Quantity(0, 1),
    var status: Status = AVAILABLE,
    var options: MutableList<Option> = mutableListOf(mockOption()),
)

fun mockCustomization(): Customization =
    MockCustomization().run {
        Customization(Id(), name, description, quantity, status, options)
    }

fun mockCustomizationWith(block: MockCustomization.() -> Unit): Customization =
    MockCustomization()
        .apply(block)
        .run { Customization(id, name, description, quantity, status, options) }
