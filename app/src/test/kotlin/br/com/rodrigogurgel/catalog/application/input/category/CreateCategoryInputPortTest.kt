package br.com.rodrigogurgel.catalog.application.input.category

import br.com.rodrigogurgel.catalog.application.exception.CategoryAlreadyExistsException
import br.com.rodrigogurgel.catalog.application.exception.StoreNotFoundException
import br.com.rodrigogurgel.catalog.application.port.input.category.CreateCategoryInputPort
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

class CreateCategoryInputPortTest {
    private val storeDatastoreOutputPort: StoreDatastoreOutputPort = mockk()
    private val categoryDatastoreOutputPort: CategoryDatastoreOutputPort = mockk()

    private val createCategoryInputPort =
        CreateCategoryInputPort(
            storeDatastoreOutputPort,
            categoryDatastoreOutputPort,
        )

    @Test
    fun `Should successfully create a category`() = runTest {
        val storeId = Id()
        val category = mockCategory()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { categoryDatastoreOutputPort.exists(storeId, category.id) } returns Ok(false)
        coEvery { categoryDatastoreOutputPort.create(storeId, category) } returns Ok(Unit)

        val result = createCategoryInputPort.execute(storeId, category)

        result.isOk.shouldBeTrue()

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            categoryDatastoreOutputPort.exists(storeId, category.id)
            categoryDatastoreOutputPort.create(storeId, category)
        }
    }

    @Test
    fun `Should fail when trying to create a category if the store doesn't exist`() = runTest {
        val storeId = Id()
        val category = mockCategory()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(false)

        val result = createCategoryInputPort.execute(storeId, category)

        result.isErr.shouldBeTrue()
        result.error shouldBe StoreNotFoundException(storeId)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
        }
    }

    @Test
    fun `Should fail when trying to create a category if a category with the same ID already exists`() = runTest {
        val storeId = Id()
        val category = mockCategory()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { categoryDatastoreOutputPort.exists(storeId, category.id) } returns Ok(true)

        val result = createCategoryInputPort.execute(storeId, category)

        result.isErr.shouldBeTrue()
        result.error shouldBe CategoryAlreadyExistsException(category.id)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            categoryDatastoreOutputPort.exists(storeId, category.id)
        }
    }
}
