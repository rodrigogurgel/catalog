package br.com.rodrigogurgel.catalog.application.input.offer

import br.com.rodrigogurgel.catalog.application.exception.OfferNotFoundException
import br.com.rodrigogurgel.catalog.application.exception.ProductsNotFoundException
import br.com.rodrigogurgel.catalog.application.exception.StoreNotFoundException
import br.com.rodrigogurgel.catalog.application.port.input.offer.UpdateOfferInputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.OfferDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.ProductDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.StoreDatastoreOutputPort
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.fixture.mock.mockOffer
import com.github.michaelbull.result.Ok
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class UpdateOfferInputPortTest {
    private val storeDatastoreOutputPort: StoreDatastoreOutputPort = mockk()
    private val offerDatastoreOutputPort: OfferDatastoreOutputPort = mockk()
    private val productDatastoreOutputPort: ProductDatastoreOutputPort = mockk()

    private val updateOfferInputPort = UpdateOfferInputPort(
        storeDatastoreOutputPort,
        productDatastoreOutputPort,
        offerDatastoreOutputPort
    )

    @Test
    fun `Should successfully update an offer`() = runTest {
        val storeId = Id()
        val categoryId = Id()
        val offer = mockOffer()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { offerDatastoreOutputPort.findById(storeId, offer.id) } returns Ok(offer)
        coEvery { offerDatastoreOutputPort.update(storeId, categoryId, offer) } returns Ok(Unit)
        coEvery {
            productDatastoreOutputPort.getIfNotExists(storeId, match { ids -> ids.containsAll(offer.getAllProducts().map { product -> product.id }) })
        } returns Ok(emptyList())

        val result = updateOfferInputPort.execute(storeId, categoryId, offer)

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
    fun `Should fail to update an offer when the store does not exist`() = runTest {
        val storeId = Id()
        val categoryId = Id()
        val offer = mockOffer()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(false)

        val result = updateOfferInputPort.execute(storeId, categoryId, offer)

        result.isErr.shouldBeTrue()
        result.error shouldBe StoreNotFoundException(storeId)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
        }
    }

    @Test
    fun `Should fail to update an offer when the offer does not exist`() = runTest {
        val storeId = Id()
        val categoryId = Id()
        val offer = mockOffer()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { offerDatastoreOutputPort.findById(storeId, offer.id) } returns Ok(null)

        val result = updateOfferInputPort.execute(storeId, categoryId, offer)

        result.isErr.shouldBeTrue()
        result.error shouldBe OfferNotFoundException(storeId, offer.id)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            offerDatastoreOutputPort.findById(storeId, offer.id)
        }
    }

    @Test
    fun `Should fail to update an offer when the product does not exist`() = runTest {
        val storeId = Id()
        val categoryId = Id()
        val offer = mockOffer()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { offerDatastoreOutputPort.findById(storeId, offer.id) } returns Ok(offer)
        coEvery {
            productDatastoreOutputPort.getIfNotExists(storeId, match { ids -> ids.containsAll(offer.getAllProducts().map { product -> product.id }) })
        } returns Ok(offer.getAllProducts().map { product -> product.id })
        coEvery { offerDatastoreOutputPort.update(storeId, categoryId, offer) } returns Ok(Unit)

        val result = updateOfferInputPort.execute(storeId, categoryId, offer)

        result.isErr.shouldBeTrue()
        result.error shouldBe ProductsNotFoundException(offer.getAllProducts().map { product -> product.id })

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
