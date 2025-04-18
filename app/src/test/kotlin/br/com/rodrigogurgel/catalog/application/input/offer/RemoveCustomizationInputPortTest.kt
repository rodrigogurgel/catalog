package br.com.rodrigogurgel.catalog.application.input.offer

import br.com.rodrigogurgel.catalog.application.exception.OfferNotFoundException
import br.com.rodrigogurgel.catalog.application.exception.StoreNotFoundException
import br.com.rodrigogurgel.catalog.application.port.input.offer.RemoveCustomizationInputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.OfferDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.StoreDatastoreOutputPort
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.fixture.mock.mockCustomization
import br.com.rodrigogurgel.catalog.fixture.mock.mockOfferWith
import com.github.michaelbull.result.Ok
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class RemoveCustomizationInputPortTest {

    private val storeDatastoreOutputPort: StoreDatastoreOutputPort = mockk()
    private val offerDatastoreOutputPort: OfferDatastoreOutputPort = mockk()

    private val removeCustomizationInputPort = RemoveCustomizationInputPort(
        storeDatastoreOutputPort,
        offerDatastoreOutputPort
    )

    @Test
    fun `Should successfully remove a customization`() = runTest {
        val storeId = Id()
        val customization = mockCustomization()
        val offer = mockOfferWith {
            customizations = mutableListOf(customization)
        }

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { offerDatastoreOutputPort.findById(storeId, offer.id) } returns Ok(offer)
        coEvery { offerDatastoreOutputPort.update(storeId, offer) } returns Ok(Unit)

        val result = removeCustomizationInputPort.execute(storeId, offer.id, customization.id)

        result.isOk.shouldBeTrue()

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            offerDatastoreOutputPort.findById(storeId, offer.id)
            offerDatastoreOutputPort.update(storeId, offer)
        }
    }

    @Test
    fun `Should fail to remove a customization from the offer when the offer does not exist`() = runTest {
        val storeId = Id()
        val customization = mockCustomization()
        val offer = mockOfferWith {
            customizations = mutableListOf(customization)
        }

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { offerDatastoreOutputPort.findById(storeId, offer.id) } returns Ok(null)

        val result = removeCustomizationInputPort.execute(storeId, offer.id, customization.id)

        result.isErr.shouldBeTrue()
        result.error shouldBe OfferNotFoundException(storeId, offer.id)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            offerDatastoreOutputPort.findById(storeId, offer.id)
        }
    }

    @Test
    fun `Should fail to remove a customization from the offer when the store does not exist`() = runTest {
        val storeId = Id()
        val customization = mockCustomization()
        val offer = mockOfferWith {
            customizations = mutableListOf(customization)
        }

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(false)

        val result = removeCustomizationInputPort.execute(storeId, offer.id, customization.id)

        result.isErr.shouldBeTrue()
        result.error shouldBe StoreNotFoundException(storeId)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
        }
    }
}
