package br.com.rodrigogurgel.catalog.domain.entity

import br.com.rodrigogurgel.catalog.domain.vo.Description
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.domain.vo.Media
import br.com.rodrigogurgel.catalog.domain.vo.MediaType.IMAGE
import br.com.rodrigogurgel.catalog.domain.vo.Name
import br.com.rodrigogurgel.catalog.fixture.mock.mockProduct
import br.com.rodrigogurgel.catalog.fixture.utils.randomString
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class ProductTest {
    @Test
    fun `Should instantiate Product with success`() {
        val id = Id()
        val name = Name(randomString(30))
        val description = Description(randomString(30))
        val medias = listOf(Media("https://www.${randomString(10)}.com", type = IMAGE))

        val product =
            Product(
                id = id,
                name = name,
                description = description,
                medias = medias,
            )

        product.id shouldBe id
        product.id.value shouldBe id.value
        product.name shouldBe name
        product.description.shouldNotBeNull()
        product.description?.value shouldBe description.value
        product.medias shouldBe medias
        product.medias.shouldNotBeNull()
        product.medias shouldBe medias
    }

    @Test
    fun `Should instantiate Product with success with optional values null`() {
        val id = Id()
        val name = Name(randomString(30))
        val description = null
        val medias = emptyList<Media>()
        val product =
            Product(
                id = id,
                name = name,
                description = description,
                medias = medias,
            )

        product.id shouldBe id
        product.id.value shouldBe id.value
        product.name shouldBe name
        product.description shouldBe null
        product.medias shouldBe medias
    }

    @Test
    fun `Should instantiate Product and update with success`() {
        val name = Name(randomString(30))
        val description = Description(randomString(30))
        val medias = listOf(Media("https://www.${randomString(10)}.com", IMAGE))

        val product = mockProduct()

        product.name = name
        product.description = description
        product.medias = medias

        product.name shouldBe name
        product.description.shouldNotBeNull()
        product.description?.value shouldBe description.value
        product.medias shouldBe medias
        product.medias.shouldNotBeNull()
        product.medias shouldBe medias
    }
}
