package br.com.rodrigogurgel.catalog.fixture.mock

import br.com.rodrigogurgel.catalog.domain.entity.Category
import br.com.rodrigogurgel.catalog.domain.vo.Description
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.domain.vo.Name
import br.com.rodrigogurgel.catalog.domain.vo.Status
import br.com.rodrigogurgel.catalog.domain.vo.Status.AVAILABLE
import br.com.rodrigogurgel.catalog.fixture.utils.randomString

data class MockCategory(
    var id: Id = Id(),
    var name: Name = Name(randomString(30)),
    var description: Description = Description(randomString(100)),
    var status: Status = AVAILABLE,
)

fun mockCategory(): Category =
    MockCategory().run {
        Category(Id(), name, description, status)
    }

fun mockCategoryWith(block: MockCategory.() -> Unit): Category =
    MockCategory()
        .apply(block)
        .run { Category(id, name, description, status) }
