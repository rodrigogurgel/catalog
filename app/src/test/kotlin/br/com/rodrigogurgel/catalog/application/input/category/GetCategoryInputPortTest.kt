package br.com.rodrigogurgel.catalog.application.input.category

import br.com.rodrigogurgel.catalog.application.exception.CategoryNotFoundException
import br.com.rodrigogurgel.catalog.application.exception.StoreNotFoundException
import br.com.rodrigogurgel.catalog.application.port.input.category.GetCategoryInputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.CategoryDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.StoreDatastoreOutputPort
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.fixture.mock.mockCategory
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class GetCategoryInputPortTest {
    private val storeDatastoreOutputPort: StoreDatastoreOutputPort = mockk()
    private val categoryDatastoreOutputPort: CategoryDatastoreOutputPort = mockk()

    private val getCategoryInputPort =
        GetCategoryInputPort(
            storeDatastoreOutputPort,
            categoryDatastoreOutputPort,
        )

    @Test
    fun `Should successfully get a category`() = runTest {
        val storeId = Id()
        val category = mockCategory()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { categoryDatastoreOutputPort.findById(storeId, category.id) } returns Ok(category)

        val result = getCategoryInputPort.execute(storeId, category.id)

        result.isOk.shouldBeTrue()
        result.value shouldBe category

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            categoryDatastoreOutputPort.findById(storeId, category.id)
        }
    }

    @Test
    fun `Should fail to get a category when the store does not exist`() = runTest {
        val storeId = Id()
        val category = mockCategory()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(false)

        val result = getCategoryInputPort.execute(storeId, category.id)

        result.isErr.shouldBeTrue()
        result.error shouldBe StoreNotFoundException(storeId)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
        }
    }

    @Test
    fun `Should fail to get a category when the category does not exist`() = runTest {
        val storeId = Id()
        val category = mockCategory()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery {
            categoryDatastoreOutputPort.findById(storeId, category.id)
        } returns Err(CategoryNotFoundException(storeId, category.id))

        val result = getCategoryInputPort.execute(storeId, category.id)

        result.isErr.shouldBeTrue()
        result.error shouldBe CategoryNotFoundException(storeId, category.id)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            categoryDatastoreOutputPort.findById(storeId, category.id)
        }
    }
}
