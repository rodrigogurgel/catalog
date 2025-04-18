package br.com.rodrigogurgel.catalog.domain.vo

import br.com.rodrigogurgel.catalog.fixture.utils.randomString
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class DescriptionTest {
    @Test
    fun `Should instantiate Description with success when value is between 3 and 1000 letters`() {
        val random1000Letters = randomString(1000)
        val random3Letters = randomString(3)

        val descriptionWith1000Letters = Description(random1000Letters)
        val descriptionWith3Letters = Description(random3Letters)

        descriptionWith1000Letters.value shouldBe random1000Letters
        descriptionWith3Letters.value shouldBe random3Letters
    }

    @Test
    fun `Should instantiate Description with error when value is empty`() {
        shouldThrow<IllegalArgumentException> {
            Description("")
        }
    }

    @Test
    fun `Should instantiate Description with error when value is less than 3 and greater than 1001`() {
        val random2Letters = randomString(2)
        val random1001Letters = randomString(1001)

        shouldThrow<IllegalArgumentException> {
            Description(random2Letters)
        }

        shouldThrow<IllegalArgumentException> {
            Description(random1001Letters)
        }
    }
}
