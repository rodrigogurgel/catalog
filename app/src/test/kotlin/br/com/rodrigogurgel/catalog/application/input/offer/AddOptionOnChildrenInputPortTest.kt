package br.com.rodrigogurgel.catalog.application.input.offer

import br.com.rodrigogurgel.catalog.application.exception.OfferNotFoundException
import br.com.rodrigogurgel.catalog.application.exception.ProductsNotFoundException
import br.com.rodrigogurgel.catalog.application.exception.StoreNotFoundException
import br.com.rodrigogurgel.catalog.application.port.input.offer.AddOptionOnChildrenInputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.OfferDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.ProductDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.StoreDatastoreOutputPort
import br.com.rodrigogurgel.catalog.domain.exception.CustomizationNotFoundException
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.fixture.mock.mockCustomization
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

class AddOptionOnChildrenInputPortTest {
    private val storeDatastoreOutputPort: StoreDatastoreOutputPort = mockk()
    private val offerDatastoreOutputPort: OfferDatastoreOutputPort = mockk()
    private val productDatastoreOutputPort: ProductDatastoreOutputPort = mockk()

    private val addOptionOnChildrenInputPort = AddOptionOnChildrenInputPort(
        storeDatastoreOutputPort,
        offerDatastoreOutputPort,
        productDatastoreOutputPort
    )

    @Test
    fun `Should successfully add an option on children`() = runTest {
        val storeId = Id()
        val categoryId = Id()
        val option = mockOption()
        val customization = mockCustomization()
        val offer = mockOfferWith {
            customizations = mutableListOf(customization)
        }

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { offerDatastoreOutputPort.findById(storeId, offer.id) } returns Ok(offer)
        coEvery { offerDatastoreOutputPort.update(storeId, categoryId, offer) } returns Ok(Unit)
        coEvery {
            productDatastoreOutputPort.getIfNotExists(storeId, match { ids -> ids.containsAll(offer.getAllProducts().map { product -> product.id }) })
        } returns Ok(emptyList())

        val result = addOptionOnChildrenInputPort.execute(storeId, categoryId, offer.id, customization.id, option)

        result.isOk.shouldBeTrue()

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            offerDatastoreOutputPort.findById(storeId, offer.id)
            productDatastoreOutputPort.getIfNotExists(
                storeId,
                match { ids -> ids.containsAll(offer.getAllProducts().map { product -> product.id }) }
            )
            offerDatastoreOutputPort.update(storeId, categoryId, offer)
        }
    }

    @Test
    fun `Should fail to add an option on children when the parent customization does not exist`() = runTest {
        val storeId = Id()
        val categoryId = Id()
        val option = mockOption()
        val customization = mockCustomization()
        val offer = mockOfferWith {
        }

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { offerDatastoreOutputPort.findById(storeId, offer.id) } returns Ok(offer)

        val result = addOptionOnChildrenInputPort.execute(storeId, categoryId, offer.id, customization.id, option)

        result.isErr.shouldBeTrue()
        result.error shouldBe CustomizationNotFoundException(customization.id)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            offerDatastoreOutputPort.findById(storeId, offer.id)
        }
    }

    @Test
    fun `Should fail to add an option on children when the offer does not exist`() = runTest {
        val storeId = Id()
        val categoryId = Id()
        val option = mockOption()
        val customization = mockCustomization()
        val offer = mockOfferWith {
            customizations = mutableListOf(customization)
        }

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { offerDatastoreOutputPort.findById(storeId, offer.id) } returns Ok(null)

        val result = addOptionOnChildrenInputPort.execute(storeId, categoryId, offer.id, customization.id, option)

        result.isErr.shouldBeTrue()
        result.error shouldBe OfferNotFoundException(storeId, offer.id)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            offerDatastoreOutputPort.findById(storeId, offer.id)
        }
    }

    @Test
    fun `Should fail to add an option on children when the store does not exist`() = runTest {
        val storeId = Id()
        val categoryId = Id()
        val option = mockOption()
        val customization = mockCustomization()
        val offer = mockOfferWith {
            customizations = mutableListOf(customization)
        }

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(false)

        val result = addOptionOnChildrenInputPort.execute(storeId, categoryId, offer.id, customization.id, option)

        result.isErr.shouldBeTrue()
        result.error shouldBe StoreNotFoundException(storeId)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
        }
    }

    @Test
    fun `Should fail to add an option on children when the product does not exist`() = runTest {
        val storeId = Id()
        val categoryId = Id()
        val option = mockOption()
        val customization = mockCustomization()
        val offer = mockOfferWith {
            customizations = mutableListOf(customization)
        }

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { offerDatastoreOutputPort.findById(storeId, offer.id) } returns Ok(offer)
        coEvery { offerDatastoreOutputPort.update(storeId, categoryId, offer) } returns Ok(Unit)
        coEvery {
            productDatastoreOutputPort.getIfNotExists(storeId, match { ids -> ids.containsAll(offer.getAllProducts().map { product -> product.id }) })
        } returns Ok(listOf(option.product!!.id))

        val result = addOptionOnChildrenInputPort.execute(storeId, categoryId, offer.id, customization.id, option)

        result.isErr.shouldBeTrue()
        result.error shouldBe ProductsNotFoundException(listOf(option.product!!.id))

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            offerDatastoreOutputPort.findById(storeId, offer.id)
            productDatastoreOutputPort.getIfNotExists(
                storeId,
                match { ids -> ids.containsAll(offer.getAllProducts().map { product -> product.id }) }
            )
        }
    }
}
