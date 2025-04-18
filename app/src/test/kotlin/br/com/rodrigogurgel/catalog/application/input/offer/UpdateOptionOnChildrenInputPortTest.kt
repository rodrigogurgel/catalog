package br.com.rodrigogurgel.catalog.application.input.offer

import br.com.rodrigogurgel.catalog.application.exception.OfferNotFoundException
import br.com.rodrigogurgel.catalog.application.exception.ProductsNotFoundException
import br.com.rodrigogurgel.catalog.application.exception.StoreNotFoundException
import br.com.rodrigogurgel.catalog.application.port.input.offer.UpdateOptionOnChildrenInputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.OfferDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.ProductDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.StoreDatastoreOutputPort
import br.com.rodrigogurgel.catalog.domain.exception.CustomizationNotFoundException
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.fixture.mock.mockCustomizationWith
import br.com.rodrigogurgel.catalog.fixture.mock.mockOffer
import br.com.rodrigogurgel.catalog.fixture.mock.mockOfferWith
import br.com.rodrigogurgel.catalog.fixture.mock.mockOption
import br.com.rodrigogurgel.catalog.fixture.mock.mockOptionWith
import com.github.michaelbull.result.Ok
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class UpdateOptionOnChildrenInputPortTest {
    private val storeDatastoreOutputPort: StoreDatastoreOutputPort = mockk()
    private val offerDatastoreOutputPort: OfferDatastoreOutputPort = mockk()
    private val productDatastoreOutputPort: ProductDatastoreOutputPort = mockk()

    private val updateOptionOnChildrenInputPort = UpdateOptionOnChildrenInputPort(
        storeDatastoreOutputPort,
        offerDatastoreOutputPort,
        productDatastoreOutputPort
    )

    @Test
    fun `Should successfully update an option on children`() = runTest {
        val storeId = Id()
        val option = mockOption()
        val newOption = mockOptionWith {
            id = option.id
        }
        val customization = mockCustomizationWith {
            options = mutableListOf(option, mockOption())
        }
        val offer = mockOfferWith {
            customizations = mutableListOf(customization)
        }

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { offerDatastoreOutputPort.findById(storeId, offer.id) } returns Ok(offer)
        coEvery { offerDatastoreOutputPort.update(storeId, offer) } returns Ok(Unit)
        coEvery {
            productDatastoreOutputPort.getIfNotExists(match { ids -> ids.containsAll(offer.getAllProducts().map { product -> product.id }) })
        } returns Ok(emptyList())

        val result = updateOptionOnChildrenInputPort.execute(storeId, offer.id, customization.id, newOption)

        result.isOk.shouldBeTrue()

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            offerDatastoreOutputPort.findById(storeId, offer.id)
            productDatastoreOutputPort.getIfNotExists(
                match { ids -> ids.containsAll(offer.getAllProducts().map { product -> product.id }) }
            )
            offerDatastoreOutputPort.update(storeId, offer)
        }
    }

    @Test
    fun `Should fail to update an option on children when the parent option does not exist`() = runTest {
        val storeId = Id()
        val option = mockOption()
        val newOption = mockOptionWith {
            id = option.id
        }
        val customization = mockCustomizationWith {
            options = mutableListOf(option, mockOption())
        }
        val offer = mockOffer()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { offerDatastoreOutputPort.findById(storeId, offer.id) } returns Ok(offer)

        val result = updateOptionOnChildrenInputPort.execute(storeId, offer.id, customization.id, newOption)

        result.isErr.shouldBeTrue()
        result.error shouldBe CustomizationNotFoundException(customization.id)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            offerDatastoreOutputPort.findById(storeId, offer.id)
        }
    }

    @Test
    fun `Should fail to update an option on children when the store does not exist`() = runTest {
        val storeId = Id()
        val option = mockOption()
        val newOption = mockOptionWith {
            id = option.id
        }
        val customization = mockCustomizationWith {
            options = mutableListOf(option, mockOption())
        }
        val offer = mockOfferWith {
            customizations = mutableListOf(customization)
        }

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(false)

        val result = updateOptionOnChildrenInputPort.execute(storeId, offer.id, customization.id, newOption)

        result.isErr.shouldBeTrue()
        result.error shouldBe StoreNotFoundException(storeId)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
        }
    }

    @Test
    fun `Should fail to update an option on children when the offer does not exist`() = runTest {
        val storeId = Id()
        val option = mockOption()
        val newOption = mockOptionWith {
            id = option.id
        }
        val customization = mockCustomizationWith {
            options = mutableListOf(option, mockOption())
        }
        val offer = mockOfferWith {
            customizations = mutableListOf(customization)
        }

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { offerDatastoreOutputPort.findById(storeId, offer.id) } returns Ok(null)

        val result = updateOptionOnChildrenInputPort.execute(storeId, offer.id, customization.id, newOption)

        result.isErr.shouldBeTrue()
        result.error shouldBe OfferNotFoundException(storeId, offer.id)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            offerDatastoreOutputPort.findById(storeId, offer.id)
        }
    }

    @Test
    fun `Should fail to update an option on children when the product does not exist`() = runTest {
        val storeId = Id()
        val option = mockOption()
        val newOption = mockOptionWith {
            id = option.id
        }
        val customization = mockCustomizationWith {
            options = mutableListOf(option, mockOption())
        }
        val offer = mockOfferWith {
            customizations = mutableListOf(customization)
        }

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { offerDatastoreOutputPort.findById(storeId, offer.id) } returns Ok(offer)
        coEvery {
            productDatastoreOutputPort.getIfNotExists(match { ids -> ids.containsAll(offer.getAllProducts().map { product -> product.id }) })
        } returns Ok(listOf(option.product!!.id))

        val result = updateOptionOnChildrenInputPort.execute(storeId, offer.id, customization.id, newOption)

        result.isErr.shouldBeTrue()
        result.error shouldBe ProductsNotFoundException(listOf(option.product!!.id))

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            offerDatastoreOutputPort.findById(storeId, offer.id)
            productDatastoreOutputPort.getIfNotExists(
                match { ids -> ids.containsAll(offer.getAllProducts().map { product -> product.id }) }
            )
        }
    }
}
