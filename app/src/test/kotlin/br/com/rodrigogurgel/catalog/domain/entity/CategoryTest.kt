package br.com.rodrigogurgel.catalog.domain.entity

import br.com.rodrigogurgel.catalog.domain.vo.Description
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.domain.vo.Name
import br.com.rodrigogurgel.catalog.domain.vo.Status.AVAILABLE
import br.com.rodrigogurgel.catalog.fixture.mock.mockCategory
import br.com.rodrigogurgel.catalog.fixture.utils.randomString
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class CategoryTest {
    @Test
    fun `Should successfully instantiate a category`() {
        val id = Id()
        val name = Name(randomString(30))
        val description = Description(randomString(100))
        val status = AVAILABLE
        val category =
            Category(
                id,
                name,
                description,
                status,
            )

        category.id shouldBe id
        category.name shouldBe name
        category.description shouldBe description
        category.status shouldBe status
    }

    @Test
    fun `Should successfully update a category`() {
        val name = Name(randomString(30))
        val description = Description(randomString(100))
        val status = AVAILABLE

        val category = mockCategory()

        category.name = name
        category.description = description
        category.status = status

        category.name shouldBe name
        category.description shouldBe description
        category.status shouldBe status
    }
}
