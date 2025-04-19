// package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore
//
// import br.com.rodrigogurgel.catalog.domain.vo.Description
// import br.com.rodrigogurgel.catalog.domain.vo.Id
// import br.com.rodrigogurgel.catalog.domain.vo.Name
// import br.com.rodrigogurgel.catalog.fixture.mock.mockCategory
// import br.com.rodrigogurgel.catalog.fixture.mock.mockOfferWith
// import br.com.rodrigogurgel.catalog.fixture.mock.mockProduct
// import br.com.rodrigogurgel.catalog.fixture.mock.mockProductWith
// import br.com.rodrigogurgel.catalog.fixture.utils.randomString
// import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.exception.DatastoreIntegrationException
// import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.postgres.ProductPostgresRepository
// import br.com.rodrigogurgel.catalog.framework.config.datastore.DynamoDBTestConfig
// import com.ninjasquad.springmockk.SpykBean
// import io.kotest.matchers.booleans.shouldBeFalse
// import io.kotest.matchers.booleans.shouldBeTrue
// import io.kotest.matchers.collections.shouldContain
// import io.kotest.matchers.collections.shouldContainAll
// import io.kotest.matchers.collections.shouldNotContain
// import io.kotest.matchers.nulls.shouldBeNull
// import io.kotest.matchers.nulls.shouldNotBeNull
// import io.kotest.matchers.shouldBe
// import io.mockk.clearAllMocks
// import io.mockk.every
// import kotlinx.coroutines.test.runTest
// import org.junit.jupiter.api.AfterEach
// import org.junit.jupiter.api.Test
// import org.springframework.beans.factory.annotation.Autowired
// import org.springframework.boot.test.context.SpringBootTest
// import org.springframework.test.context.ActiveProfiles
// import org.springframework.test.context.ContextConfiguration
// import org.testcontainers.junit.jupiter.Testcontainers
// import java.util.UUID
//
// @SpringBootTest
// @Testcontainers
// @ActiveProfiles("test")
// @ContextConfiguration(classes = [DynamoDBTestConfig::class])
// class ProductPostgresDatastoreOutputPortAdapterTest {
//
//    @Autowired
//    private lateinit var productDatastoreOutputPortAdapter: ProductDatastoreOutputPortAdapter
//
//    @Autowired
//    private lateinit var offerDatastoreOutputPortAdapter: OfferDatastoreOutputPortAdapter
//
//    @Autowired
//    private lateinit var categoryDatastoreOutputPortAdapter: CategoryDatastoreOutputPortAdapter
//
//    @SpykBean
//    private lateinit var productPostgresRepository: ProductPostgresRepository
//
//    private val genericTestException = RuntimeException("Something went wrong")
//
//    @AfterEach
//    fun afterEach() {
//        clearAllMocks()
//    }
//
//    @Test
//    fun `Should create a Product Successfully`() = runTest {
//        val storeId = Id(UUID.randomUUID())
//        val product = mockProductWith {
//            description = null
//        }
//
//        productDatastoreOutputPortAdapter.create(storeId, product)
//
//        val result = productDatastoreOutputPortAdapter.findById(storeId, product.id)
//
//        result.isOk.shouldBeTrue()
//        result.value.shouldNotBeNull()
//        result.value shouldBe product
//    }
//
//    @Test
//    fun `Should return null when Product does not exists`() = runTest {
//        val result = productDatastoreOutputPortAdapter.findById(
//            Id(UUID.randomUUID()),
//            Id(UUID.randomUUID())
//        )
//
//        result.isOk.shouldBeTrue()
//        result.value.shouldBeNull()
//    }
//
//    @Test
//    fun `Should update a Product successfully`() = runTest {
//        val storeId = Id(UUID.randomUUID())
//        val product = mockProduct()
//
//        productDatastoreOutputPortAdapter.create(storeId, product)
//
//        product.name = Name(randomString(30))
//        product.description = Description(randomString(1000))
//        product.image = null
//
//        val update = productDatastoreOutputPortAdapter.update(storeId, product)
//
//        update.isOk.shouldBeTrue()
//
//        val result = productDatastoreOutputPortAdapter.findById(storeId, product.id)
//
//        result.isOk.shouldBeTrue()
//        result.value.shouldNotBeNull()
//        result.value shouldBe product
//    }
//
//    @Test
//    fun `Should delete a Product successfully`() = runTest {
//        val storeId = Id(UUID.randomUUID())
//        val product = mockProduct()
//
//        productDatastoreOutputPortAdapter.create(storeId, product)
//
//        val deletedProduct = productDatastoreOutputPortAdapter.findById(storeId, product.id)
//
//        deletedProduct.shouldNotBeNull()
//
//        productDatastoreOutputPortAdapter.delete(storeId, product.id)
//
//        val deletedProductAfterDeletion = productDatastoreOutputPortAdapter.findById(storeId, product.id)
//
//        deletedProductAfterDeletion.isOk.shouldBeTrue()
//        deletedProductAfterDeletion.value shouldBe null
//    }
//
//    @Test
//    fun `Should return Product Id if not exists`() = runTest {
//        val storeId = Id(UUID.randomUUID())
//        val productNotExists = Id(UUID.randomUUID())
//        val product = mockProduct()
//
//        productDatastoreOutputPortAdapter.create(storeId, product)
//
//        val result = productDatastoreOutputPortAdapter.getIfNotExists(listOf(product.id, productNotExists))
//
//        result.isOk.shouldBeTrue()
//        result.value shouldContain productNotExists
//        result.value shouldNotContain product.id
//    }
//
//    @Test
//    fun `Should return true when the Product exists`() = runTest {
//        val storeId = Id(UUID.randomUUID())
//        val product = mockProduct()
//
//        productDatastoreOutputPortAdapter.create(storeId, product)
//
//        val result = productDatastoreOutputPortAdapter.exists(product.id)
//
//        result.isOk.shouldBeTrue()
//        result.value.shouldBeTrue()
//    }
//
//    @Test
//    fun `Should return false when the Product does not exists`() = runTest {
//        val result = productDatastoreOutputPortAdapter.exists(Id(UUID.randomUUID()))
//
//        result.isOk.shouldBeTrue()
//        result.value.shouldBeFalse()
//    }
//
//    @Test
//    fun `Should return true when the Product is in use`() = runTest {
//        val storeId = Id(UUID.randomUUID())
//        val category = mockCategory()
//        val product = mockProduct()
//        val offer = mockOfferWith {
//            this.product = product
//        }
//
//        categoryDatastoreOutputPortAdapter.create(storeId, category)
//        productDatastoreOutputPortAdapter.create(storeId, product)
//        offerDatastoreOutputPortAdapter.create(storeId, category.id, offer)
//
//        val result = productDatastoreOutputPortAdapter.productIsInUse(product.id)
//
//        result.isOk.shouldBeTrue()
//        result.value.shouldBeTrue()
//    }
//
//    @Test
//    fun `Should return false when the Product is in use`() = runTest {
//        val result = productDatastoreOutputPortAdapter.productIsInUse(Id(UUID.randomUUID()))
//
//        result.isOk.shouldBeTrue()
//        result.value.shouldBeFalse()
//    }
//
//    @Test
//    fun `Should return true when the Product does not exists by Product Id and Store Id`() = runTest {
//        val storeId = Id(UUID.randomUUID())
//        val product = mockProduct()
//
//        productDatastoreOutputPortAdapter.create(storeId, product)
//
//        val result = productDatastoreOutputPortAdapter.exists(storeId, product.id)
//
//        result.isOk.shouldBeTrue()
//        result.value.shouldBeTrue()
//    }
//
//    @Test
//    fun `Should return false when the Product does not exists by Product Id and Store Id`() = runTest {
//        val result = productDatastoreOutputPortAdapter.exists(
//            Id(UUID.randomUUID()),
//            Id(UUID.randomUUID())
//        )
//
//        result.isOk.shouldBeTrue()
//        result.value.shouldBeFalse()
//    }
//
//    @Test
//    fun `Should count 10 Products`() = runTest {
//        val storeId = Id(UUID.randomUUID())
//        val products = List(10) { mockProduct() }
//
//        products.forEach {
//            productDatastoreOutputPortAdapter.create(storeId, it)
//        }
//
//        val result = productDatastoreOutputPortAdapter.countProducts(storeId)
//
//        result.isOk.shouldBeTrue()
//        result.value shouldBe 10
//    }
//
//    @Test
//    fun `Should return a page of Products successfully`() = runTest {
//        val storeId = Id(UUID.randomUUID())
//        val products = List(10) { mockProduct() }
//
//        products.forEach {
//            productDatastoreOutputPortAdapter.create(storeId, it)
//        }
//
//        val result = productDatastoreOutputPortAdapter.getProducts(storeId, 20, 0)
//
//        result.isOk.shouldBeTrue()
//        result.value shouldContainAll products
//    }
//
//    @Test
//    fun `Should throw DatastoreIntegrationException when creating a product and the repository throws an exception`() = runTest {
//        val storeId = Id(UUID.randomUUID())
//        val product = mockProduct()
//
//        every { productPostgresRepository.create(any()) } throws genericTestException
//
//        val result = productDatastoreOutputPortAdapter.create(storeId, product)
//
//        result.isErr.shouldBeTrue()
//        result.error shouldBe DatastoreIntegrationException(genericTestException)
//    }
//
//    @Test
//    fun `Should throw DatastoreIntegrationException when finding a product and the repository throws an exception`() = runTest {
//        val storeId = Id(UUID.randomUUID())
//        val productId = Id(UUID.randomUUID())
//
//        every { productPostgresRepository.findById(any(), any()) } throws genericTestException
//
//        val result = productDatastoreOutputPortAdapter.findById(storeId, productId)
//
//        result.isErr.shouldBeTrue()
//        result.error shouldBe DatastoreIntegrationException(genericTestException)
//    }
//
//    @Test
//    fun `Should throw DatastoreIntegrationException when updating a product and the repository throws an exception`() = runTest {
//        val storeId = Id(UUID.randomUUID())
//        val product = mockProduct()
//
//        every { productPostgresRepository.update(any()) } throws genericTestException
//
//        val result = productDatastoreOutputPortAdapter.update(storeId, product)
//
//        result.isErr.shouldBeTrue()
//        result.error shouldBe DatastoreIntegrationException(genericTestException)
//    }
//
//    @Test
//    fun `Should throw DatastoreIntegrationException when deleting a product and the repository throws an exception`() = runTest {
//        val storeId = Id(UUID.randomUUID())
//        val productId = Id(UUID.randomUUID())
//
//        every { productPostgresRepository.delete(any(), any()) } throws genericTestException
//
//        val result = productDatastoreOutputPortAdapter.delete(storeId, productId)
//
//        result.isErr.shouldBeTrue()
//        result.error shouldBe DatastoreIntegrationException(genericTestException)
//    }
//
//    @Test
//    fun `Should throw DatastoreIntegrationException when checking if product exists and the repository throws an exception`() = runTest {
//        val productId = Id(UUID.randomUUID())
//
//        every { productPostgresRepository.exists(any()) } throws genericTestException
//
//        val result = productDatastoreOutputPortAdapter.exists(productId)
//
//        result.isErr.shouldBeTrue()
//        result.error shouldBe DatastoreIntegrationException(genericTestException)
//    }
//
//    @Test
//    fun `Should throw DatastoreIntegrationException when counting products and the repository throws an exception`() = runTest {
//        val storeId = Id(UUID.randomUUID())
//
//        every { productPostgresRepository.countProducts(any()) } throws genericTestException
//
//        val result = productDatastoreOutputPortAdapter.countProducts(storeId)
//
//        result.isErr.shouldBeTrue()
//        result.error shouldBe DatastoreIntegrationException(genericTestException)
//    }
//
//    @Test
//    fun `Should throw DatastoreIntegrationException when getting products and the repository throws an exception`() = runTest {
//        val storeId = Id(UUID.randomUUID())
//
//        every { productPostgresRepository.getProducts(any(), any(), any()) } throws genericTestException
//
//        val result = productDatastoreOutputPortAdapter.getProducts(storeId, 20, 0)
//
//        result.isErr.shouldBeTrue()
//        result.error shouldBe DatastoreIntegrationException(genericTestException)
//    }
// }
