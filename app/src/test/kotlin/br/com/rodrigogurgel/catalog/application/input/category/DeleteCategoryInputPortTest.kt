package br.com.rodrigogurgel.catalog.application.input.category

import br.com.rodrigogurgel.catalog.application.exception.StoreNotFoundException
import br.com.rodrigogurgel.catalog.application.port.input.category.DeleteCategoryInputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.CategoryDatastoreOutputPort
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

class DeleteCategoryInputPortTest {
    private val storeDatastoreOutputPort: StoreDatastoreOutputPort = mockk()
    private val categoryDatastoreOutputPort: CategoryDatastoreOutputPort = mockk()

    private val deleteCategoryInputPort =
        DeleteCategoryInputPort(
            storeDatastoreOutputPort,
            categoryDatastoreOutputPort,
        )

    @Test
    fun `Should successfully delete a category`() = runTest {
        val storeId = Id()
        val categoryId = Id()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { categoryDatastoreOutputPort.delete(storeId, categoryId) } returns Ok(Unit)

        val result = deleteCategoryInputPort.execute(storeId, categoryId)

        result.isOk.shouldBeTrue()

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            categoryDatastoreOutputPort.delete(storeId, categoryId)
        }
    }

    @Test
    fun `Should fail when trying to delete a category if the store doesn't exist`() = runTest {
        val storeId = Id()
        val categoryId = Id()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(false)

        val result = deleteCategoryInputPort.execute(storeId, categoryId)

        result.isErr.shouldBeTrue()
        result.error shouldBe StoreNotFoundException(storeId)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
        }
    }
}
