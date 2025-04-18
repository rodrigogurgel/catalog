package br.com.rodrigogurgel.catalog.application.input.offer

import br.com.rodrigogurgel.catalog.application.exception.CategoryNotFoundException
import br.com.rodrigogurgel.catalog.application.exception.StoreNotFoundException
import br.com.rodrigogurgel.catalog.application.port.input.offer.CountOffersInputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.CategoryDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.OfferDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.StoreDatastoreOutputPort
import br.com.rodrigogurgel.catalog.domain.vo.Id
import com.github.michaelbull.result.Ok
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class CountOffersInputPortTest {
    private val storeDatastoreOutputPort: StoreDatastoreOutputPort = mockk()
    private val categoryDatastoreOutputPort: CategoryDatastoreOutputPort = mockk()
    private val offerDatastoreOutputPort: OfferDatastoreOutputPort = mockk()

    private val countOffersInputPort = CountOffersInputPort(
        storeDatastoreOutputPort,
        categoryDatastoreOutputPort,
        offerDatastoreOutputPort
    )

    @Test
    fun `Should successfully count offers`() = runTest {
        val storeId = Id()
        val categoryId = Id()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { categoryDatastoreOutputPort.exists(storeId, categoryId) } returns Ok(true)
        coEvery { offerDatastoreOutputPort.countOffers(storeId, categoryId) } returns Ok(1)

        val result = countOffersInputPort.execute(storeId, categoryId)

        result.isOk.shouldBeTrue()
        result.value shouldBe 1

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            categoryDatastoreOutputPort.exists(storeId, categoryId)
            offerDatastoreOutputPort.countOffers(storeId, categoryId)
        }
    }

    @Test
    fun `Should fail to count an offer when the store does not exist`() = runTest {
        val storeId = Id()
        val categoryId = Id()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(false)

        val result = countOffersInputPort.execute(storeId, categoryId)

        result.isErr.shouldBeTrue()
        result.error shouldBe StoreNotFoundException(storeId)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
        }
    }

    @Test
    fun `Should fail to count an offer when the category does not exist`() = runTest {
        val storeId = Id()
        val categoryId = Id()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { categoryDatastoreOutputPort.exists(storeId, categoryId) } returns Ok(false)

        val result = countOffersInputPort.execute(storeId, categoryId)

        result.isErr.shouldBeTrue()
        result.error shouldBe CategoryNotFoundException(storeId, categoryId)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            categoryDatastoreOutputPort.exists(storeId, categoryId)
        }
    }
}
