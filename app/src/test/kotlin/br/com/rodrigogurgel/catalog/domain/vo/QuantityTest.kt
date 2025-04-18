package br.com.rodrigogurgel.catalog.domain.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class QuantityTest {
    @Test
    fun `Should instantiate quantity with success when minPermitted is 0 and maxPermitted is greater than minPermitted`() {
        val minPermitted = 0
        val maxPermitted = 1
        val quantity = Quantity(minPermitted, maxPermitted)

        quantity.minPermitted shouldBe minPermitted
        quantity.maxPermitted shouldBe maxPermitted
    }

    @Test
    fun `Should instantiate quantity with success when minPermitted greater then 0 and maxPermitted is equals to minPermitted`() {
        val minPermitted = 1
        val maxPermitted = 1
        val quantity = Quantity(minPermitted, maxPermitted)

        quantity.minPermitted shouldBe minPermitted
        quantity.maxPermitted shouldBe maxPermitted
        quantity.minPermitted shouldBe quantity.maxPermitted
    }

    @Test
    fun `Should instantiate quantity with error when minPermitted is less than 0`() {
        val minPermitted = -1
        val maxPermitted = 1
        shouldThrow<IllegalArgumentException> {
            Quantity(minPermitted, maxPermitted)
        }
    }

    @Test
    fun `Should instantiate quantity with error when maxPermitted is less than minPermitted`() {
        val minPermitted = 3
        val maxPermitted = 1
        shouldThrow<IllegalArgumentException> {
            Quantity(minPermitted, maxPermitted)
        }
    }

    @Test
    fun `Should instantiate quantity with error when maxPermitted is equal to minPermitted and minPermitted is 0`() {
        val minPermitted = 0
        val maxPermitted = 0
        shouldThrow<IllegalArgumentException> {
            Quantity(minPermitted, maxPermitted)
        }
    }
}
