package br.com.rodrigogurgel.catalog.application.input.offer

import br.com.rodrigogurgel.catalog.application.exception.OfferNotFoundException
import br.com.rodrigogurgel.catalog.application.exception.StoreNotFoundException
import br.com.rodrigogurgel.catalog.application.port.input.offer.RemoveOptionOnChildrenInputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.OfferDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.StoreDatastoreOutputPort
import br.com.rodrigogurgel.catalog.domain.exception.CustomizationNotFoundException
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.fixture.mock.mockCustomizationWith
import br.com.rodrigogurgel.catalog.fixture.mock.mockOffer
import br.com.rodrigogurgel.catalog.fixture.mock.mockOfferWith
import br.com.rodrigogurgel.catalog.fixture.mock.mockOption
import com.github.michaelbull.result.Ok
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class RemoveOptionOnChildrenInputPortTest {
    private val storeDatastoreOutputPort: StoreDatastoreOutputPort = mockk()
    private val offerDatastoreOutputPort: OfferDatastoreOutputPort = mockk()

    private val removeOptionOnChildrenInputPort = RemoveOptionOnChildrenInputPort(
        storeDatastoreOutputPort,
        offerDatastoreOutputPort
    )

    @Test
    fun `Should successfully remove an option on children`() = runTest {
        val storeId = Id()
        val option = mockOption()
        val customization = mockCustomizationWith {
            options = mutableListOf(option, mockOption())
        }
        val offer = mockOfferWith {
            customizations = mutableListOf(customization)
        }

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { offerDatastoreOutputPort.findById(storeId, offer.id) } returns Ok(offer)
        coEvery { offerDatastoreOutputPort.update(storeId, offer) } returns Ok(Unit)

        val result = removeOptionOnChildrenInputPort.execute(storeId, offer.id, customization.id, option.id)

        result.isOk.shouldBeTrue()

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            offerDatastoreOutputPort.findById(storeId, offer.id)
            offerDatastoreOutputPort.update(storeId, offer)
        }
    }

    @Test
    fun `Should fail to remove an option on children when the parent customization does not exist`() = runTest {
        val storeId = Id()
        val option = mockOption()
        val customization = mockCustomizationWith {
            options = mutableListOf(option, mockOption())
        }
        val offer = mockOffer()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { offerDatastoreOutputPort.findById(storeId, offer.id) } returns Ok(offer)
        coEvery { offerDatastoreOutputPort.update(storeId, offer) } returns Ok(Unit)

        val result = removeOptionOnChildrenInputPort.execute(storeId, offer.id, customization.id, option.id)

        result.isErr.shouldBeTrue()
        result.error shouldBe CustomizationNotFoundException(customization.id)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            offerDatastoreOutputPort.findById(storeId, offer.id)
        }
    }

    @Test
    fun `Should fail to remove an option from a child when the store does not exist`() = runTest {
        val storeId = Id()
        val option = mockOption()
        val customization = mockCustomizationWith {
            options = mutableListOf(option, mockOption())
        }
        val offer = mockOfferWith {
            customizations = mutableListOf(customization)
        }

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(false)

        val result = removeOptionOnChildrenInputPort.execute(storeId, offer.id, customization.id, option.id)

        result.isErr.shouldBeTrue()
        result.error shouldBe StoreNotFoundException(storeId)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
        }
    }

    @Test
    fun `Should fail to remove an option from a child when the offer does not exist`() = runTest {
        val storeId = Id()
        val option = mockOption()
        val customization = mockCustomizationWith {
            options = mutableListOf(option, mockOption())
        }
        val offer = mockOfferWith {
            customizations = mutableListOf(customization)
        }

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { offerDatastoreOutputPort.findById(storeId, offer.id) } returns Ok(null)

        val result = removeOptionOnChildrenInputPort.execute(storeId, offer.id, customization.id, option.id)

        result.isErr.shouldBeTrue()
        result.error shouldBe OfferNotFoundException(storeId, offer.id)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            offerDatastoreOutputPort.findById(storeId, offer.id)
        }
    }
}
