package br.com.rodrigogurgel.catalog.domain.vo

import br.com.rodrigogurgel.catalog.fixture.utils.randomString
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class NameTest {
    @Test
    fun `Should instantiate Name with success when value is between 3 and 30 letters`() {
        val random30Letters = randomString(30)
        val random3Letters = randomString(3)

        val nameWith30Letters = Name(random30Letters)
        val nameWith3Letters = Name(random3Letters)

        nameWith30Letters.value shouldBe random30Letters
        nameWith3Letters.value shouldBe random3Letters
    }

    @Test
    fun `Should instantiate Name with error when value is empty`() {
        shouldThrow<IllegalArgumentException> {
            Name("")
        }
    }

    @Test
    fun `Should instantiate Name with error when value is less than 3 and greater than 50`() {
        val random2Letters = randomString(2)
        val random31Letters = randomString(51)

        shouldThrow<IllegalArgumentException> {
            Name(random2Letters)
        }
        shouldThrow<IllegalArgumentException> {
            Name(random31Letters)
        }
    }
}
