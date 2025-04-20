package br.com.rodrigogurgel.catalog.application.input.offer

import br.com.rodrigogurgel.catalog.application.exception.CategoryNotFoundException
import br.com.rodrigogurgel.catalog.application.exception.OfferAlreadyExistsException
import br.com.rodrigogurgel.catalog.application.exception.ProductsNotFoundException
import br.com.rodrigogurgel.catalog.application.exception.StoreNotFoundException
import br.com.rodrigogurgel.catalog.application.port.input.offer.CreateOfferInputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.CategoryDatastoreOutputPort
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

class CreateOfferInputPortTest {

    private val storeDatastoreOutputPort: StoreDatastoreOutputPort = mockk()
    private val categoryDatastoreOutputPort: CategoryDatastoreOutputPort = mockk()
    private val productDatastoreOutputPort: ProductDatastoreOutputPort = mockk()
    private val offerDatastoreOutputPort: OfferDatastoreOutputPort = mockk()

    private val createOfferInputPort = CreateOfferInputPort(
        storeDatastoreOutputPort,
        categoryDatastoreOutputPort,
        productDatastoreOutputPort,
        offerDatastoreOutputPort
    )

    @Test
    fun `Should successfully create an offer`() = runTest {
        val storeId = Id()
        val categoryId = Id()
        val offer = mockOffer()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { categoryDatastoreOutputPort.exists(storeId, categoryId) } returns Ok(true)
        coEvery { offerDatastoreOutputPort.exists(offer.id) } returns Ok(false)
        coEvery { offerDatastoreOutputPort.create(storeId, categoryId, offer) } returns Ok(Unit)
        coEvery {
            productDatastoreOutputPort.getIfNotExists(storeId, offer.getAllProducts().map { it.id })
        } returns Ok(emptyList())

        val result = createOfferInputPort.execute(storeId, categoryId, offer)

        result.isOk.shouldBeTrue()

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            categoryDatastoreOutputPort.exists(storeId, categoryId)
            offerDatastoreOutputPort.exists(offer.id)
            productDatastoreOutputPort.getIfNotExists(storeId, offer.getAllProducts().map { it.id })
            offerDatastoreOutputPort.create(storeId, categoryId, offer)
        }
    }

    @Test
    fun `Should fail to create an offer when the store does not exist`() = runTest {
        val storeId = Id()
        val categoryId = Id()
        val offer = mockOffer()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(false)

        val result = createOfferInputPort.execute(storeId, categoryId, offer)

        result.isErr.shouldBeTrue()
        result.error shouldBe StoreNotFoundException(storeId)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
        }
    }

    @Test
    fun `Should fail to create an offer when an offer with the same ID already exists`() = runTest {
        val storeId = Id()
        val categoryId = Id()
        val offer = mockOffer()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { categoryDatastoreOutputPort.exists(storeId, categoryId) } returns Ok(true)
        coEvery { offerDatastoreOutputPort.exists(offer.id) } returns Ok(true)

        val result = createOfferInputPort.execute(storeId, categoryId, offer)

        result.isErr.shouldBeTrue()
        result.error shouldBe OfferAlreadyExistsException(offer.id)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            categoryDatastoreOutputPort.exists(storeId, categoryId)
            offerDatastoreOutputPort.exists(offer.id)
        }
    }

    @Test
    fun `Should fail to create an offer when the category does not exist`() = runTest {
        val storeId = Id()
        val categoryId = Id()
        val offer = mockOffer()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { categoryDatastoreOutputPort.exists(storeId, categoryId) } returns Ok(false)

        val result = createOfferInputPort.execute(storeId, categoryId, offer)

        result.isErr.shouldBeTrue()
        result.error shouldBe CategoryNotFoundException(storeId, categoryId)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            categoryDatastoreOutputPort.exists(storeId, categoryId)
        }
    }

    @Test
    fun `Should fail to create an offer when the product does not exist`() = runTest {
        val storeId = Id()
        val categoryId = Id()
        val offer = mockOffer()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { categoryDatastoreOutputPort.exists(storeId, categoryId) } returns Ok(true)
        coEvery { offerDatastoreOutputPort.exists(offer.id) } returns Ok(false)
        coEvery { offerDatastoreOutputPort.create(storeId, categoryId, offer) } returns Ok(Unit)
        coEvery {
            productDatastoreOutputPort.getIfNotExists(
                storeId,
                offer.getAllProducts().map {
                    it.id
                }
            )
        } returns Ok(offer.getAllProducts().map { it.id })

        val result = createOfferInputPort.execute(storeId, categoryId, offer)

        result.isErr.shouldBeTrue()
        result.error shouldBe ProductsNotFoundException(offer.getAllProducts().map { it.id })

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            categoryDatastoreOutputPort.exists(storeId, categoryId)
            offerDatastoreOutputPort.exists(offer.id)
            productDatastoreOutputPort.getIfNotExists(storeId, offer.getAllProducts().map { it.id })
        }
    }
}
