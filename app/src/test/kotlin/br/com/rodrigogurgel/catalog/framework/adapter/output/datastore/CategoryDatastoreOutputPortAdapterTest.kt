package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore

import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.domain.vo.Name
import br.com.rodrigogurgel.catalog.domain.vo.Status
import br.com.rodrigogurgel.catalog.fixture.mock.mockCategory
import br.com.rodrigogurgel.catalog.fixture.utils.randomString
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.exception.DatastoreIntegrationException
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.CategoryModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.repository.CategoryRepository
import com.github.michaelbull.result.getError
import com.ninjasquad.springmockk.SpykBean
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.coEvery
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class CategoryDatastoreOutputPortAdapterTest : AbstractMongoDBBaseTest() {
    @Autowired
    private lateinit var categoryDatastoreOutputPortAdapter: CategoryDatastoreOutputPortAdapter

    @SpykBean
    private lateinit var categoryRepository: CategoryRepository

    private val genericTestException = RuntimeException("Something went wrong")

    @AfterEach
    fun afterEach() {
        clearAllMocks()
    }

    @Test
    fun `Should create a Category successfully`() = runTest {
        val storeId = Id(UUID.randomUUID())
        val category = mockCategory()
        val created = categoryDatastoreOutputPortAdapter.create(storeId, category)

        created.isOk.shouldBeTrue()

        val result = categoryDatastoreOutputPortAdapter.findById(storeId, category.id)

        result.isOk.shouldBeTrue()
        result.value.shouldNotBeNull()
        result.value shouldBe category
    }

    @Test
    fun `Should update a Category successfully`() = runTest {
        val storeId = Id(UUID.randomUUID())
        val category = mockCategory()
        categoryDatastoreOutputPortAdapter.create(storeId, category)

        category.status = Status.UNAVAILABLE
        category.name = Name(randomString(30))
        category.description = null

        categoryDatastoreOutputPortAdapter.update(storeId, category)

        val result = categoryDatastoreOutputPortAdapter.findById(storeId, category.id)

        result.isOk.shouldBeTrue()
        result.value.shouldNotBeNull()
        result.value shouldBe category
    }

    @Test
    fun `Should return true when verify if the Category exists by Category Id`() = runTest {
        val storeId = Id(UUID.randomUUID())
        val category = mockCategory()
        categoryDatastoreOutputPortAdapter.create(storeId, category)

        val result = categoryDatastoreOutputPortAdapter.exists(storeId, category.id)

        result.isOk.shouldBeTrue()
        result.value.shouldBeTrue()
    }

    @Test
    fun `Should return true when verify if the Category exists by Category Id and Store Id`() = runTest {
        val storeId = Id(UUID.randomUUID())
        val category = mockCategory()
        categoryDatastoreOutputPortAdapter.create(storeId, category)

        val result = categoryDatastoreOutputPortAdapter.exists(storeId, category.id)

        result.isOk.shouldBeTrue()
        result.value.shouldBeTrue()
    }

    @Test
    fun `Should delete a Category successfully`() = runTest {
        val storeId = Id(UUID.randomUUID())
        val category = mockCategory()
        categoryDatastoreOutputPortAdapter.create(storeId, category)

        categoryDatastoreOutputPortAdapter.delete(storeId, category.id)

        val result = categoryDatastoreOutputPortAdapter.findById(storeId, category.id)

        result.isOk.shouldBeTrue()
        result.value.shouldBeNull()
    }

    @Test
    fun `Should return false when verify if the Category doesn't exist by Category Id`() = runTest {
        val storeId = Id(UUID.randomUUID())
        val result = categoryDatastoreOutputPortAdapter.exists(storeId, Id(UUID.randomUUID()))

        if (result.isErr) println(result.getError())
        result.isOk.shouldBeTrue()
        result.value.shouldBeFalse()
    }

    @Test
    fun `Should return false when verify if the Category doesn't exist by Category Id and Store Id`() = runTest {
        val result = categoryDatastoreOutputPortAdapter.exists(
            Id(UUID.randomUUID()),
            Id(UUID.randomUUID())
        )

        result.isOk.shouldBeTrue()
        result.value.shouldBeFalse()
    }

    @Test
    fun `Should count 10 categories successfully`() = runTest {
        val storeId = Id(UUID.randomUUID())
        val categories = List(10) {
            mockCategory()
        }

        categories.forEach { categoryDatastoreOutputPortAdapter.create(storeId, it) }

        val result = categoryDatastoreOutputPortAdapter.countCategories(storeId)

        result.isOk.shouldBeTrue()
        result.value shouldBe 10
    }

    @Test
    fun `Should get 10 categories successfully`() = runTest {
        val storeId = Id(UUID.randomUUID())
        val categories = List(10) {
            mockCategory()
        }

        categories.forEach { categoryDatastoreOutputPortAdapter.create(storeId, it) }

        val result = categoryDatastoreOutputPortAdapter.getCategories(storeId, 20, null)

        result.isOk.shouldBeTrue()
        result.value shouldContainAll categories
    }

    @Test
    fun `Should throw DatastoreIntegrationException when creating a category and the repository returns an exception`() = runTest {
        val storeId = Id(UUID.randomUUID())
        val category = mockCategory()

        coEvery { categoryRepository.insert(any<CategoryModel>()) } throws genericTestException

        val result = categoryDatastoreOutputPortAdapter.create(storeId, category)

        result.isErr.shouldBeTrue()
        result.error shouldBe DatastoreIntegrationException(genericTestException)
    }

    @Test
    fun `Should throw DatastoreIntegrationException when updating a category and the repository returns an exception`() = runTest {
        val storeId = Id(UUID.randomUUID())
        val category = mockCategory()

        coEvery { categoryRepository.save(any<CategoryModel>()) } throws genericTestException

        val result = categoryDatastoreOutputPortAdapter.update(storeId, category)

        result.isErr.shouldBeTrue()
        result.error shouldBe DatastoreIntegrationException(genericTestException)
    }

    @Test
    fun `Should throw DatastoreIntegrationException when verifying if the category exists by category ID and store ID and the repository returns an exception`() = runTest {
        val storeId = Id(UUID.randomUUID())
        val categoryId = Id(UUID.randomUUID())

        coEvery {
            categoryRepository.existsByCategoryModelId_StoreIdAndCategoryModelId_CategoryId(storeId.value, categoryId.value)
        } throws genericTestException

        val result = categoryDatastoreOutputPortAdapter.exists(storeId, categoryId)

        result.isErr.shouldBeTrue()
        result.error shouldBe DatastoreIntegrationException(genericTestException)
    }

    @Test
    fun `Should throw DatastoreIntegrationException when deleting a category and the repository returns an exception`() = runTest {
        val storeId = Id(UUID.randomUUID())
        val categoryId = Id(UUID.randomUUID())

        coEvery {
            categoryRepository.deleteByCategoryModelId_StoreIdAndCategoryModelId_CategoryId(storeId.value, categoryId.value)
        } throws genericTestException

        val result = categoryDatastoreOutputPortAdapter.delete(storeId, categoryId)

        result.isErr.shouldBeTrue()
        result.error shouldBe DatastoreIntegrationException(genericTestException)
    }

    @Test
    fun `Should throw DatastoreIntegrationException when counting categories and the repository returns an exception`() = runTest {
        val storeId = Id(UUID.randomUUID())

        coEvery { categoryRepository.countByCategoryModelId_StoreId(storeId.value) } throws genericTestException

        val result = categoryDatastoreOutputPortAdapter.countCategories(storeId)

        result.isErr.shouldBeTrue()
        result.error shouldBe DatastoreIntegrationException(genericTestException)
    }

    @Test
    fun `Should throw DatastoreIntegrationException when getting categories and the repository returns an exception`() = runTest {
        val storeId = Id(UUID.randomUUID())

        coEvery {
            categoryRepository.findAllByCategoryModelId_StoreId(storeId.value, any())
        } throws genericTestException

        val result = categoryDatastoreOutputPortAdapter.getCategories(storeId, 10, null)

        result.isErr.shouldBeTrue()
        result.error shouldBe DatastoreIntegrationException(genericTestException)
    }
}
