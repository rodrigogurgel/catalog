package br.com.rodrigogurgel.catalog.application.input.category

import br.com.rodrigogurgel.catalog.application.exception.CategoryNotFoundException
import br.com.rodrigogurgel.catalog.application.exception.StoreNotFoundException
import br.com.rodrigogurgel.catalog.application.port.input.category.UpdateCategoryInputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.CategoryDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.StoreDatastoreOutputPort
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.fixture.mock.mockCategory
import com.github.michaelbull.result.Ok
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class UpdateCategoryInputPortTest {
    private val storeDatastoreOutputPort: StoreDatastoreOutputPort = mockk()
    private val categoryDatastoreOutputPort: CategoryDatastoreOutputPort = mockk()

    private val updateCategoryInputPort =
        UpdateCategoryInputPort(
            storeDatastoreOutputPort,
            categoryDatastoreOutputPort,
        )

    @Test
    fun `Should successfully update a category`() = runTest {
        val storeId = Id()
        val category = mockCategory()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { categoryDatastoreOutputPort.exists(storeId, category.id) } returns Ok(true)
        coEvery { categoryDatastoreOutputPort.update(storeId, category) } returns Ok(Unit)

        val result = updateCategoryInputPort.execute(storeId, category)

        result.isOk.shouldBeTrue()

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            categoryDatastoreOutputPort.exists(storeId, category.id)
            categoryDatastoreOutputPort.update(storeId, category)
        }
    }

    @Test
    fun `Should fail to update a category when the store does not exist`() = runTest {
        val storeId = Id()
        val category = mockCategory()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(false)

        val result = updateCategoryInputPort.execute(storeId, category)

        result.isErr.shouldBeTrue()
        result.error shouldBe StoreNotFoundException(storeId)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
        }
    }

    @Test
    fun `Should fail to update a category when the category does not exist`() = runTest {
        val storeId = Id()
        val category = mockCategory()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { categoryDatastoreOutputPort.exists(storeId, category.id) } returns Ok(false)

        val result = updateCategoryInputPort.execute(storeId, category)

        result.isErr.shouldBeTrue()
        result.error shouldBe CategoryNotFoundException(storeId, category.id)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            categoryDatastoreOutputPort.exists(storeId, category.id)
        }
    }
}
