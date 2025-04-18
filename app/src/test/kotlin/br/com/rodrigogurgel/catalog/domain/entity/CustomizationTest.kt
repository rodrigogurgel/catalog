package br.com.rodrigogurgel.catalog.domain.entity

import br.com.rodrigogurgel.catalog.domain.exception.CustomizationMinPermittedException
import br.com.rodrigogurgel.catalog.domain.exception.CustomizationOptionsIsEmptyException
import br.com.rodrigogurgel.catalog.domain.exception.OptionAlreadyExistsException
import br.com.rodrigogurgel.catalog.domain.exception.OptionNotFoundException
import br.com.rodrigogurgel.catalog.domain.vo.Description
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.domain.vo.Name
import br.com.rodrigogurgel.catalog.domain.vo.Price
import br.com.rodrigogurgel.catalog.domain.vo.Quantity
import br.com.rodrigogurgel.catalog.domain.vo.Status.AVAILABLE
import br.com.rodrigogurgel.catalog.domain.vo.Status.UNAVAILABLE
import br.com.rodrigogurgel.catalog.fixture.mock.mockCustomization
import br.com.rodrigogurgel.catalog.fixture.mock.mockCustomizationWith
import br.com.rodrigogurgel.catalog.fixture.mock.mockOption
import br.com.rodrigogurgel.catalog.fixture.mock.mockOptionWith
import br.com.rodrigogurgel.catalog.fixture.utils.randomString
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldHaveSameHashCodeAs
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class CustomizationTest {
    @Test
    fun `Should successfully instantiate a customization when minPermitted is 0 and options is not empty`() {
        val id = Id()
        val name = Name(randomString(30))
        val description = Description(randomString(1000))
        val quantity = Quantity(0, 1)
        val status = AVAILABLE
        val options = mutableListOf(mockOption())

        val customization =
            Customization(
                id,
                name,
                description,
                quantity,
                status,
                options,
            )

        customization.id shouldBe id
        customization.name shouldBe name
        customization.description shouldBe description
        customization.quantity shouldBe quantity
        customization.status shouldBe status
        customization.options shouldBe options
    }

    @Test
    fun `Should fail to instantiate a customization when the number of AVAILABLE options is less than minPermitted`() {
        val id = Id()
        val name = Name(randomString(30))
        val description = Description(randomString(1000))
        val quantity = Quantity(1, 1)
        val status = AVAILABLE
        val option = mockOption()

        val customization =
            Customization(
                id,
                name,
                description,
                quantity,
                status,
                mutableListOf(option),
            )

        customization.id shouldBe id
        customization.name shouldBe name
        customization.description shouldBe description
        customization.quantity shouldBe quantity
        customization.status shouldBe status
        customization.options shouldContain option

        option.status = UNAVAILABLE

        shouldThrow<CustomizationMinPermittedException> {
            customization.updateOption(option)
        }
    }

    @Test
    fun `Should successfully update mutable values of a customization`() {
        val id = Id()
        val name = Name(randomString(30))
        val description = Description(randomString(1000))
        val quantity = Quantity(0, 1)
        val status = AVAILABLE
        val options = mutableListOf(mockOption())

        val customization =
            Customization(
                id,
                name,
                description,
                quantity,
                status,
                options,
            )

        customization.id shouldBe id
        customization.name shouldBe name
        customization.description shouldBe description
        customization.quantity shouldBe quantity
        customization.status shouldBe status
        customization.options shouldBe options

        val updatedName = Name(randomString(30))
        val updatedDescription = Description(randomString(1000))
        val updatedQuantity = Quantity(1, 1)

        customization.status = UNAVAILABLE
        customization.name = updatedName
        customization.description = updatedDescription
        customization.quantity = updatedQuantity

        customization.id shouldBe id
        customization.name.value shouldBe updatedName.value
        customization.description?.value shouldBe updatedDescription.value
        customization.quantity shouldBe updatedQuantity
        customization.status shouldBe UNAVAILABLE
        customization.options shouldBe options
    }

    @Test
    fun `Should successfully instantiate a customization when minPermitted is 1 and options size matches minPermitted`() {
        val id = Id()
        val name = Name(randomString(30))
        val description = Description(randomString(1000))
        val quantity = Quantity(1, 1)
        val status = AVAILABLE
        val options = mutableListOf(mockOption())

        val customization =
            Customization(
                id,
                name,
                description,
                quantity,
                status,
                options,
            )

        customization.id shouldBe id
        customization.name shouldBe name
        customization.quantity shouldBe quantity
        customization.status shouldBe status
        customization.options shouldBe options
        customization.quantity.minPermitted shouldBe options.size
    }

    @Test
    fun `Should fail to instantiate a customization when options are empty`() {
        val id = Id()
        val name = Name(randomString(30))
        val description = Description(randomString(1000))
        val quantity = Quantity(0, 2)
        val status = AVAILABLE
        val options = mutableListOf<Option>()

        val exception =
            shouldThrow<CustomizationOptionsIsEmptyException> {
                Customization(
                    id,
                    name,
                    description,
                    quantity,
                    status,
                    options,
                )
            }

        exception.message shouldContain id.toString()
    }

    @Test
    fun `Should calculate minimal price as 10 when minPermitted is 2 and options have minimal price of 5`() {
        val option1 =
            mockOptionWith {
                quantity = Quantity(2, 3)
                price = Price(2.5.toBigDecimal())
            }
        val option2 =
            mockOptionWith {
                quantity = Quantity(1, 3)
                price = Price(5.toBigDecimal())
            }

        val customization =
            mockCustomizationWith {
                quantity = Quantity(minPermitted = 2, maxPermitted = 2)
                options = mutableListOf(option1, option2)
            }

        customization.minimalPrice().value shouldBe Price(10.toBigDecimal()).value
    }

    @Test
    fun `Should calculate minimal price as 10 when customization minPermitted is 2, options have minimal price of 5, but an option price is 0`() {
        val subOption1 =
            mockOptionWith {
                quantity = Quantity(minPermitted = 1, maxPermitted = 2)
                price = Price(2.5.toBigDecimal())
            }
        val subOption2 =
            mockOptionWith {
                quantity = Quantity(minPermitted = 0, maxPermitted = 1)
                price = Price(2.5.toBigDecimal())
            }

        val option1 =
            mockOptionWith {
                quantity = Quantity(2, 3)
                price = Price(BigDecimal.ZERO)
                customizations =
                    mutableListOf(
                        mockCustomizationWith {
                            quantity = Quantity(2, 2)
                            options = mutableListOf(subOption1, subOption2)
                        },
                    )
            }

        val subOption3 =
            mockOptionWith {
                quantity = Quantity(minPermitted = 1, maxPermitted = 2)
                price = Price(5.toBigDecimal())
            }
        val subOption4 =
            mockOptionWith {
                quantity = Quantity(minPermitted = 0, maxPermitted = 1)
                price = Price(10.toBigDecimal())
            }

        val option2 =
            mockOptionWith {
                quantity = Quantity(0, 3)
                price = Price(0.toBigDecimal())
                customizations =
                    mutableListOf(
                        mockCustomizationWith {
                            quantity = Quantity(1, 2)
                            options = mutableListOf(subOption3, subOption4)
                        },
                    )
            }

        option2.minimalPrice()

        val customization =
            mockCustomizationWith {
                quantity = Quantity(minPermitted = 2, maxPermitted = 2)
                options = mutableListOf(option1, option2)
            }

        customization.minimalPrice().value shouldBe Price(10.toBigDecimal()).value
    }

    @Test
    fun `Should successfully add an option to a customization`() {
        val option = mockOption()
        val customization = mockCustomization()

        customization.addOption(option)

        customization.options shouldContain option
    }

    @Test
    fun `Should fail to add an option when the option already exists in the customization`() {
        val option = mockOption()
        val customization = mockCustomization()

        customization.addOption(option)
        shouldThrow<OptionAlreadyExistsException> {
            customization.addOption(option)
        }

        customization.options shouldContain option
    }

    @Test
    fun `Should successfully remove an option from a customization`() {
        val option = mockOption()
        val customization = mockCustomization()

        customization.addOption(option)

        customization.options shouldContain option

        customization.removeOption(option.id)

        customization.options shouldNotContain option
    }

    @Test
    fun `Should successfully update an option in a customization`() {
        val option = mockOption()
        val updatedOption =
            mockOptionWith {
                id = option.id
            }
        val customization = mockCustomization()

        customization.addOption(option)

        customization.options shouldContain option

        customization.updateOption(updatedOption)

        customization.options shouldContain updatedOption
    }

    @Test
    fun `Should fail to update an option when the option does not exist in the customization`() {
        val option = mockOption()
        val newOption = mockOption()
        val customization = mockCustomization()

        customization.addOption(option)

        customization.options shouldContain option

        assertThrows<OptionNotFoundException> {
            customization.updateOption(newOption)
        }

        customization.options shouldNotContain newOption
    }

    @Test
    fun `Should throw CustomizationMinPermittedException when minimum quantity is greater than the quantity of available options`() {
        val option =
            mockOptionWith {
                status = UNAVAILABLE
            }
        val id = Id()
        val name = Name(randomString(30))
        val description = Description(randomString(1000))
        val quantity = Quantity(0, 1)
        val status = AVAILABLE

        val customization =
            Customization(
                id,
                name,
                description,
                quantity,
                status,
                mutableListOf(option),
            )

        shouldThrow<CustomizationMinPermittedException> {
            customization.quantity = Quantity(1, 1)
            customization
        }
    }

    @Test
    fun `Should be equal when comparing identical customizations`() {
        val customization =
            mockCustomizationWith {
                description = null
            }
        val other =
            customization.run {
                Customization(id, name, description, quantity, status, options)
            }

        customization shouldBeEqual customization
        customization shouldBeEqual other
    }

    @Test
    fun `Should not be equal when comparing different customizations`() {
        val customization =
            mockCustomizationWith {
                description = null
            }
        val other =
            customization.run {
                Customization(id, name, Description(randomString(1000)), quantity, status, options)
            }

        customization shouldNotBeEqual Any()
        customization shouldNotBeEqual other
    }

    @Test
    fun `Should have the same hash code when comparing identical customizations`() {
        val customization =
            mockCustomizationWith {
                description = null
            }
        val other =
            customization.run {
                Customization(id, name, description, quantity, status, options)
            }

        customization shouldHaveSameHashCodeAs other
    }
}
