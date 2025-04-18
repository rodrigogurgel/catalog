package br.com.rodrigogurgel.catalog.application.input.category

import br.com.rodrigogurgel.catalog.application.exception.StoreNotFoundException
import br.com.rodrigogurgel.catalog.application.port.input.category.CountCategoriesInputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.CategoryDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.StoreDatastoreOutputPort
import br.com.rodrigogurgel.catalog.domain.vo.Id
import com.github.michaelbull.result.Ok
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class CountCategoriesInputPortTest {
    private val storeDatastoreOutputPort: StoreDatastoreOutputPort = mockk()
    private val categoryDatastoreOutputPort: CategoryDatastoreOutputPort = mockk()

    private val countCategoriesInputPort =
        CountCategoriesInputPort(
            storeDatastoreOutputPort,
            categoryDatastoreOutputPort,
        )

    @Test
    fun `Should successfully count categories`() = runTest {
        val storeId = Id()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { categoryDatastoreOutputPort.countCategories(storeId) } returns Ok(1)

        val count = countCategoriesInputPort.execute(storeId)

        count.isOk.shouldBeTrue()
        count.value shouldBe 1

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            categoryDatastoreOutputPort.countCategories(storeId)
        }
    }

    @Test
    fun `Should fail when trying to get a category if the store does not exist`() = runTest {
        val storeId = Id()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(false)

        val result = countCategoriesInputPort.execute(storeId)

        result.isErr.shouldBeTrue()
        result.error shouldBe StoreNotFoundException(storeId)

        coVerify { storeDatastoreOutputPort.exists(storeId) }
    }
}
