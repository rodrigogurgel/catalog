package br.com.rodrigogurgel.catalog.application.input.offer

import br.com.rodrigogurgel.catalog.application.exception.OfferNotFoundException
import br.com.rodrigogurgel.catalog.application.exception.ProductsNotFoundException
import br.com.rodrigogurgel.catalog.application.exception.StoreNotFoundException
import br.com.rodrigogurgel.catalog.application.port.input.offer.UpdateCustomizationInputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.OfferDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.ProductDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.StoreDatastoreOutputPort
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.fixture.mock.mockCustomization
import br.com.rodrigogurgel.catalog.fixture.mock.mockCustomizationWith
import br.com.rodrigogurgel.catalog.fixture.mock.mockOfferWith
import com.github.michaelbull.result.Ok
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class UpdateCustomizationInputPortTest {
    private val storeDatastoreOutputPort: StoreDatastoreOutputPort = mockk()
    private val offerDatastoreOutputPort: OfferDatastoreOutputPort = mockk()
    private val productDatastoreOutputPort: ProductDatastoreOutputPort = mockk()

    private val updateCustomizationInputPort = UpdateCustomizationInputPort(
        storeDatastoreOutputPort,
        offerDatastoreOutputPort,
        productDatastoreOutputPort
    )

    @Test
    fun `Should successfully update a customization`() = runTest {
        val storeId = Id()
        val customization = mockCustomization()
        val newCustomization = mockCustomizationWith {
            id = customization.id
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

        val result = updateCustomizationInputPort.execute(storeId, offer.id, newCustomization)

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
    fun `Should fail to update a customization from the offer when the store does not exist`() = runTest {
        val storeId = Id()
        val customization = mockCustomization()
        val newCustomization = mockCustomizationWith {
            id = customization.id
        }
        val offer = mockOfferWith {
            customizations = mutableListOf(customization)
        }

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(false)

        val result = updateCustomizationInputPort.execute(storeId, offer.id, newCustomization)

        result.isErr.shouldBeTrue()
        result.error shouldBe StoreNotFoundException(storeId)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
        }
    }

    @Test
    fun `Should fail to update a customization from the offer when the offer does not exist`() = runTest {
        val storeId = Id()
        val customization = mockCustomization()
        val newCustomization = mockCustomizationWith {
            id = customization.id
        }
        val offer = mockOfferWith {
            customizations = mutableListOf(customization)
        }

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { offerDatastoreOutputPort.findById(storeId, offer.id) } returns Ok(null)

        val result = updateCustomizationInputPort.execute(storeId, offer.id, newCustomization)

        result.isErr.shouldBeTrue()
        result.error shouldBe OfferNotFoundException(storeId, offer.id)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            offerDatastoreOutputPort.findById(storeId, offer.id)
        }
    }

    @Test
    fun `Should fail to update a customization from the offer when the product does not exist`() = runTest {
        val storeId = Id()
        val customization = mockCustomization()
        val newCustomization = mockCustomizationWith {
            id = customization.id
        }
        val offer = mockOfferWith {
            customizations = mutableListOf(customization)
        }

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { offerDatastoreOutputPort.findById(storeId, offer.id) } returns Ok(offer)
        coEvery { offerDatastoreOutputPort.update(storeId, offer) } returns Ok(Unit)
        coEvery {
            productDatastoreOutputPort.getIfNotExists(match { ids -> ids.containsAll(offer.getAllProducts().map { product -> product.id }) })
        } returns Ok(newCustomization.options.map { it.product!!.id })

        val result = updateCustomizationInputPort.execute(storeId, offer.id, newCustomization)

        result.isErr.shouldBeTrue()
        result.error shouldBe ProductsNotFoundException(newCustomization.options.map { it.product!!.id })

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            offerDatastoreOutputPort.findById(storeId, offer.id)
            productDatastoreOutputPort.getIfNotExists(
                match { ids -> ids.containsAll(offer.getAllProducts().map { product -> product.id }) }
            )
        }
    }
}
