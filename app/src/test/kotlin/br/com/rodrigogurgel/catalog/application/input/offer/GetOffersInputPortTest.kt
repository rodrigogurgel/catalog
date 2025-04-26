package br.com.rodrigogurgel.catalog.application.input.offer

import br.com.rodrigogurgel.catalog.application.exception.CategoryNotFoundException
import br.com.rodrigogurgel.catalog.application.exception.StoreNotFoundException
import br.com.rodrigogurgel.catalog.application.port.input.offer.GetOffersInputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.CategoryDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.OfferDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.StoreDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.utils.normalizeLimit
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

class GetOffersInputPortTest {

    private val storeDatastoreOutputPort: StoreDatastoreOutputPort = mockk()
    private val categoryDatastoreOutputPort: CategoryDatastoreOutputPort = mockk()
    private val offersDatastoreOutputPort: OfferDatastoreOutputPort = mockk()

    private val getOffersInputPort = GetOffersInputPort(
        storeDatastoreOutputPort,
        categoryDatastoreOutputPort,
        offersDatastoreOutputPort
    )

    @Test
    fun `Should successfully get offers`() = runTest {
        val storeId = Id()
        val categoryId = Id()
        val limit = 20
        val cursor: String? = null
        val offers = List(10) {
            mockOffer()
        }

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { categoryDatastoreOutputPort.exists(storeId, categoryId) } returns Ok(true)
        coEvery { offersDatastoreOutputPort.getOffers(storeId, categoryId, limit, cursor) } returns Ok(offers)

        val result = getOffersInputPort.execute(storeId, categoryId, limit, cursor)

        result.isOk.shouldBeTrue()
        result.value shouldBe offers

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            categoryDatastoreOutputPort.exists(storeId, categoryId)
            offersDatastoreOutputPort.getOffers(storeId, categoryId, limit, cursor)
        }
    }

    @Test
    fun `Should successfully get offers when the limit parameter is negative`() = runTest {
        val storeId = Id()
        val categoryId = Id()
        val limit = -1
        val cursor: String? = null
        val offers = List(10) {
            mockOffer()
        }

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { categoryDatastoreOutputPort.exists(storeId, categoryId) } returns Ok(true)
        coEvery {
            offersDatastoreOutputPort.getOffers(storeId, categoryId, normalizeLimit(limit), cursor)
        } returns Ok(offers)

        val result = getOffersInputPort.execute(storeId, categoryId, limit, cursor)

        result.isOk.shouldBeTrue()

        result.value shouldBe offers

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            categoryDatastoreOutputPort.exists(storeId, categoryId)
            offersDatastoreOutputPort.getOffers(storeId, categoryId, normalizeLimit(limit), cursor)
        }
    }

    @Test
    fun `Should successfully get offers when the limit parameter is greater than 20`() = runTest {
        val storeId = Id()
        val categoryId = Id()
        val limit = 10000
        val cursor: String? = null
        val offers = List(10) {
            mockOffer()
        }

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { categoryDatastoreOutputPort.exists(storeId, categoryId) } returns Ok(true)
        coEvery {
            offersDatastoreOutputPort.getOffers(storeId, categoryId, normalizeLimit(limit), cursor)
        } returns Ok(offers)

        val result = getOffersInputPort.execute(storeId, categoryId, limit, cursor)

        result.isOk.shouldBeTrue()

        result.value shouldBe offers

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            categoryDatastoreOutputPort.exists(storeId, categoryId)
            offersDatastoreOutputPort.getOffers(storeId, categoryId, normalizeLimit(limit), cursor)
        }
    }

    @Test
    fun `Should successfully get offers when the offset parameter is negative`() = runTest {
        val storeId = Id()
        val categoryId = Id()
        val limit = 20
        val cursor: String? = null
        val offers = List(10) {
            mockOffer()
        }

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { categoryDatastoreOutputPort.exists(storeId, categoryId) } returns Ok(true)
        coEvery { offersDatastoreOutputPort.getOffers(storeId, categoryId, limit, cursor) } returns Ok(offers)

        val result = getOffersInputPort.execute(storeId, categoryId, limit, cursor)

        result.isOk.shouldBeTrue()

        result.value shouldBe offers

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            categoryDatastoreOutputPort.exists(storeId, categoryId)
            offersDatastoreOutputPort.getOffers(storeId, categoryId, limit, cursor)
        }
    }

    @Test
    fun `Should fail to get offers when the store does not exist`() = runTest {
        val storeId = Id()
        val categoryId = Id()
        val limit = 20
        val cursor: String? = null

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(false)

        val result = getOffersInputPort.execute(storeId, categoryId, limit, cursor)

        result.isErr.shouldBeTrue()
        result.error shouldBe StoreNotFoundException(storeId)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
        }
    }

    @Test
    fun `Should fail to get offers when the category does not exist`() = runTest {
        val storeId = Id()
        val categoryId = Id()
        val limit = 20
        val cursor: String? = null

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { categoryDatastoreOutputPort.exists(storeId, categoryId) } returns Ok(false)

        val result = getOffersInputPort.execute(storeId, categoryId, limit, cursor)

        result.isErr.shouldBeTrue()
        result.error shouldBe CategoryNotFoundException(storeId, categoryId)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            categoryDatastoreOutputPort.exists(storeId, categoryId)
        }
    }
}
