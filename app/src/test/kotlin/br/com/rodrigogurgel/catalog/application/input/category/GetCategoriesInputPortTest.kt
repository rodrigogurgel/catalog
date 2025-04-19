package br.com.rodrigogurgel.catalog.application.input.category

import br.com.rodrigogurgel.catalog.application.exception.StoreNotFoundException
import br.com.rodrigogurgel.catalog.application.port.input.category.GetCategoriesInputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.CategoryDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.StoreDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.utils.normalizeLimit
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.fixture.mock.mockCategory
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.utils.CursorUtils
import com.github.michaelbull.result.Ok
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.time.Instant

class GetCategoriesInputPortTest {
    private val storeDatastoreOutputPort: StoreDatastoreOutputPort = mockk()
    private val categoryDatastoreOutputPort: CategoryDatastoreOutputPort = mockk()

    private val getCategoriesInputPort =
        GetCategoriesInputPort(
            storeDatastoreOutputPort,
            categoryDatastoreOutputPort,
        )

    @Test
    fun `Should successfully get categories`() = runTest {
        val storeId = Id()
        val categories = listOf(mockCategory(), mockCategory())
        val limit = 20
        val cursor: String? = null

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { categoryDatastoreOutputPort.getCategories(storeId, limit, cursor) } returns Ok(null to categories)

        val result = getCategoriesInputPort.execute(storeId, limit, cursor)

        result.isOk.shouldBeTrue()
        result.value shouldBe (null to categories)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            categoryDatastoreOutputPort.getCategories(storeId, limit, cursor)
        }
    }

    @Test
    fun `Should successfully get categories when the limit parameter is negative`() = runTest {
        val storeId = Id()
        val categories = listOf(mockCategory(), mockCategory())
        val limit = -1
        val cursor: String? = null

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery {
            categoryDatastoreOutputPort.getCategories(storeId, normalizeLimit(limit), cursor)
        } returns Ok(null to categories)

        val result = getCategoriesInputPort.execute(storeId, limit, cursor)

        result.isOk.shouldBeTrue()
        result.value shouldBe (null to categories)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            categoryDatastoreOutputPort.getCategories(storeId, normalizeLimit(limit), cursor)
        }
    }

    @Test
    fun `Should successfully get categories when the limit parameter is greater than 20`() = runTest {
        val storeId = Id()
        val categories = listOf(mockCategory(), mockCategory())
        val limit = 21
        val cursor: String? = null

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery {
            categoryDatastoreOutputPort.getCategories(storeId, normalizeLimit(limit), cursor)
        } returns Ok(cursor to categories)

        val result = getCategoriesInputPort.execute(storeId, limit, cursor)

        result.isOk.shouldBeTrue()
        result.value shouldBe (null to categories)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            categoryDatastoreOutputPort.getCategories(storeId, normalizeLimit(limit), cursor)
        }
    }

    @Test
    fun `Should successfully get categories when the offset parameter is not null`() = runTest {
        val storeId = Id()
        val categories = listOf(mockCategory(), mockCategory())
        val limit = 20
        val cursor = CursorUtils.encode(
            mapOf(
                "store_id" to AttributeValue.builder().s(storeId.value.toString()).build(),
                "created_at" to AttributeValue.builder().s(Instant.now().toString()).build(),
                "category_id" to AttributeValue.builder().s(categories.last().id.value.toString()).build(),
            )
        )

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { categoryDatastoreOutputPort.getCategories(storeId, limit, cursor) } returns Ok(cursor to categories)

        val result = getCategoriesInputPort.execute(storeId, limit, cursor)

        result.isOk.shouldBeTrue()
        result.value shouldBe (cursor to categories)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            categoryDatastoreOutputPort.getCategories(storeId, limit, cursor)
        }
    }

    @Test
    fun `Should fail to get categories when the store does not exist`() = runTest {
        val storeId = Id()
        val categories = listOf(mockCategory(), mockCategory())
        val limit = 20
        val cursor: String? = null

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(false)
        coEvery { categoryDatastoreOutputPort.getCategories(storeId, limit, cursor) } returns Ok(cursor to categories)

        val result = getCategoriesInputPort.execute(storeId, limit, cursor)

        result.isErr.shouldBeTrue()
        result.error shouldBe StoreNotFoundException(storeId)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
        }
    }
}
