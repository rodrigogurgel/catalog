package br.com.rodrigogurgel.catalog.application.input.offer

import br.com.rodrigogurgel.catalog.application.exception.ProductsNotFoundException
import br.com.rodrigogurgel.catalog.application.exception.StoreNotFoundException
import br.com.rodrigogurgel.catalog.application.port.input.offer.AddCustomizationInputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.OfferDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.ProductDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.StoreDatastoreOutputPort
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.fixture.mock.mockCustomization
import br.com.rodrigogurgel.catalog.fixture.mock.mockOffer
import com.github.michaelbull.result.Ok
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class AddCustomizationInputPortTest {
    private val storeDatastoreOutputPort: StoreDatastoreOutputPort = mockk()
    private val offerDatastoreOutputPort: OfferDatastoreOutputPort = mockk()
    private val productDatastoreOutputPort: ProductDatastoreOutputPort = mockk()

    private val addCustomizationInput = AddCustomizationInputPort(
        storeDatastoreOutputPort,
        offerDatastoreOutputPort,
        productDatastoreOutputPort
    )

    @Test
    fun `Should successfully add a customization`() = runTest {
        val storeId = Id()
        val offer = mockOffer()
        val customization = mockCustomization()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { offerDatastoreOutputPort.findById(storeId, offer.id) } returns Ok(offer)
        coEvery { offerDatastoreOutputPort.update(storeId, offer) } returns Ok(Unit)
        coEvery {
            productDatastoreOutputPort.getIfNotExists(match { ids -> ids.containsAll(offer.getAllProducts().map { product -> product.id }) })
        } returns Ok(emptyList())

        val result = addCustomizationInput.execute(storeId, offer.id, customization)

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
    fun `Should fail to add a customization when the store does not exist`() = runTest {
        val storeId = Id()
        val offer = mockOffer()
        val customization = mockCustomization()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(false)

        val result = addCustomizationInput.execute(storeId, offer.id, customization)

        result.isErr.shouldBeTrue()
        result.error shouldBe StoreNotFoundException(storeId)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
        }
    }

    @Test
    fun `Should fail to add a customization when the product does not exist`() = runTest {
        val storeId = Id()
        val offer = mockOffer()
        val customization = mockCustomization()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { offerDatastoreOutputPort.findById(storeId, offer.id) } returns Ok(offer)
        coEvery {
            productDatastoreOutputPort.getIfNotExists(
                match {
                    it.containsAll(
                        offer.getAllProducts().map { product -> product.id } +
                            customization.options.mapNotNull { option -> option.product?.id }
                    )
                }
            )
        } returns Ok(customization.options.mapNotNull { it.product?.id })

        val result = addCustomizationInput.execute(storeId, offer.id, customization)

        result.isErr.shouldBeTrue()
        result.error shouldBe ProductsNotFoundException(customization.options.mapNotNull { it.product?.id })

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            offerDatastoreOutputPort.findById(storeId, offer.id)
            productDatastoreOutputPort.getIfNotExists(offer.getAllProducts().map { it.id })
        }
    }
}
