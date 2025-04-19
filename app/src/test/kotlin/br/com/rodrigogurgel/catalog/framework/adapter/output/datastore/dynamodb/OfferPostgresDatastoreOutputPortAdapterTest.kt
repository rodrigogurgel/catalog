// package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore
//
// import br.com.rodrigogurgel.catalog.domain.vo.Id
// import br.com.rodrigogurgel.catalog.fixture.mock.mockCategory
// import br.com.rodrigogurgel.catalog.fixture.mock.mockCustomization
// import br.com.rodrigogurgel.catalog.fixture.mock.mockCustomizationWith
// import br.com.rodrigogurgel.catalog.fixture.mock.mockOffer
// import br.com.rodrigogurgel.catalog.fixture.mock.mockOfferWith
// import br.com.rodrigogurgel.catalog.fixture.mock.mockOptionWith
// import br.com.rodrigogurgel.catalog.fixture.mock.mockProduct
// import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.exception.DatastoreIntegrationException
// import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.postgres.OfferPostgresRepository
// import br.com.rodrigogurgel.catalog.framework.config.datastore.DynamoDBTestConfig
// import com.ninjasquad.springmockk.SpykBean
// import io.kotest.matchers.booleans.shouldBeFalse
// import io.kotest.matchers.booleans.shouldBeTrue
// import io.kotest.matchers.equals.shouldBeEqual
// import io.kotest.matchers.nulls.shouldBeNull
// import io.kotest.matchers.nulls.shouldNotBeNull
// import io.kotest.matchers.shouldBe
// import io.kotest.matchers.shouldNotBe
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
// class OfferPostgresDatastoreOutputPortAdapterTest {
//    @Autowired
//    private lateinit var categoryDatastoreOutputPortAdapter: CategoryDatastoreOutputPortAdapter
//
//    @Autowired
//    private lateinit var productDatastoreOutputPortAdapter: ProductDatastoreOutputPortAdapter
//
//    @Autowired
//    private lateinit var offerDatastoreOutputPortAdapter: OfferDatastoreOutputPortAdapter
//
//    @SpykBean
//    private lateinit var offerPostgresRepository: OfferPostgresRepository
//
//    private val genericTestException = RuntimeException("Something went wrong")
//
//    @AfterEach
//    fun afterEach() {
//        clearAllMocks()
//    }
//
//    @Test
//    fun `Should create an Offer with Customizations and Options successfully`() = runTest {
//        val storeId = Id(UUID.randomUUID())
//        val category = mockCategory()
//        val offer = mockOfferWith {
//            this.customizations = mutableListOf(mockCustomization())
//        }
//
//        offer.getAllProducts().forEach { product ->
//            productDatastoreOutputPortAdapter.create(storeId, product)
//        }
//
//        categoryDatastoreOutputPortAdapter.create(storeId, category)
//        offerDatastoreOutputPortAdapter.create(storeId, category.id, offer)
//
//        val result = offerDatastoreOutputPortAdapter.findById(storeId, offer.id)
//
//        result.isOk.shouldBeTrue()
//        result.shouldNotBeNull()
//        result.value.shouldNotBeNull()
//        result.value!! shouldBeEqual offer
//    }
//
//    @Test
//    fun `Should return null when offer not exists`() = runTest {
//        val result = offerDatastoreOutputPortAdapter.findById(
//            Id(UUID.randomUUID()),
//            Id(UUID.randomUUID())
//        )
//
//        result.isOk.shouldBeTrue()
//        result.value.shouldBeNull()
//    }
//
//    @Test
//    fun `Should return true when the Offer exists by Offer Id`() = runTest {
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
//        val result = offerDatastoreOutputPortAdapter.exists(offer.id)
//
//        result.isOk.shouldBeTrue()
//        result.value shouldBe true
//    }
//
//    @Test
//    fun `Should return false when the Offer doesn't exists by Offer Id`() = runTest {
//        val result = offerDatastoreOutputPortAdapter.exists(Id(UUID.randomUUID()))
//
//        result.isOk.shouldBeTrue()
//        result.value.shouldBeFalse()
//    }
//
//    @Test
//    fun `Should return true when the Offer exists by Offer Id and Store Id`() = runTest {
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
//        val result = offerDatastoreOutputPortAdapter.exists(storeId, offer.id)
//
//        result.isOk.shouldBeTrue()
//        result.value shouldBe true
//    }
//
//    @Test
//    fun `Should return false when the Offer doesn't exists by Offer Id and Store Id`() = runTest {
//        val result = offerDatastoreOutputPortAdapter.exists(
//            Id(UUID.randomUUID()),
//            Id(UUID.randomUUID())
//        )
//
//        result.isOk.shouldBeTrue()
//        result.value.shouldBeFalse()
//    }
//
//    @Test
//    fun `Should delete Offer successfully`() = runTest {
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
//        offerDatastoreOutputPortAdapter.delete(storeId, offer.id)
//
//        val result = offerDatastoreOutputPortAdapter.exists(storeId, offer.id)
//
//        result.isOk.shouldBeTrue()
//        result.value.shouldBeFalse()
//    }
//
//    @Test
//    fun `Should update an Offer successfully`() = runTest {
//        val storeId = Id(UUID.randomUUID())
//        val category = mockCategory()
//        val offer = mockOffer()
//
//        offer.getAllProducts().forEach { product ->
//            productDatastoreOutputPortAdapter.create(storeId, product)
//        }
//
//        categoryDatastoreOutputPortAdapter.create(storeId, category)
//        offerDatastoreOutputPortAdapter.create(storeId, category.id, offer)
//
//        val updateOffer = mockOfferWith {
//            id = offer.id
//        }
//
//        updateOffer.getAllProducts().forEach { product ->
//            productDatastoreOutputPortAdapter.create(storeId, product)
//        }
//
//        val update = offerDatastoreOutputPortAdapter.update(storeId, updateOffer)
//
//        update.isOk.shouldBeTrue()
//
//        val result = offerDatastoreOutputPortAdapter.findById(storeId, offer.id)
//
//        result.isOk.shouldBeTrue()
//        result.value.shouldNotBeNull()
//        result.value!! shouldNotBe offer
//        result.value!! shouldBeEqual updateOffer
//    }
//
//    @Test
//    fun `Should update an Offer with Customizations and Options successfully`() = runTest {
//        val storeId = Id(UUID.randomUUID())
//        val category = mockCategory()
//        val offer = mockOffer()
//
//        offer.getAllProducts().forEach { product ->
//            productDatastoreOutputPortAdapter.create(storeId, product)
//        }
//
//        categoryDatastoreOutputPortAdapter.create(storeId, category)
//        offerDatastoreOutputPortAdapter.create(storeId, category.id, offer)
//
//        val option = mockOptionWith {
//            customizations = mutableListOf(mockCustomizationWith { description = null })
//        }
//
//        val customizations = mockCustomizationWith {
//            options = mutableListOf(option)
//        }
//
//        val updateOffer = mockOfferWith {
//            id = offer.id
//            this.customizations = mutableListOf(customizations)
//        }
//
//        updateOffer.getAllProducts().forEach { product ->
//            productDatastoreOutputPortAdapter.create(storeId, product)
//        }
//
//        val update = offerDatastoreOutputPortAdapter.update(storeId, updateOffer)
//
//        update.isOk.shouldBeTrue()
//
//        val result = offerDatastoreOutputPortAdapter.findById(storeId, offer.id)
//
//        result.isOk.shouldBeTrue()
//        result.value.shouldNotBeNull()
//        result.value!! shouldBeEqual updateOffer
//    }
//
//    @Test
//    fun `Should count 10 Offers successfully`() = runTest {
//        val storeId = Id(UUID.randomUUID())
//        val category = mockCategory()
//        val offers = List(10) {
//            mockOffer()
//        }
//
//        offers.flatMap { offer -> offer.getAllProducts() }.forEach { product ->
//            productDatastoreOutputPortAdapter.create(storeId, product)
//        }
//
//        categoryDatastoreOutputPortAdapter.create(storeId, category)
//
//        offers.forEach { offer ->
//            offerDatastoreOutputPortAdapter.create(storeId, category.id, offer)
//        }
//
//        val result = offerDatastoreOutputPortAdapter.countOffers(storeId, category.id)
//
//        result.isOk.shouldBeTrue()
//        result.value shouldBe 10
//    }
//
//    @Test
//    fun `Should get 10 offers successfully`() = runTest {
//        val storeId = Id(UUID.randomUUID())
//        val category = mockCategory()
//        val offers = List(10) {
//            mockOffer()
//        }
//
//        offers.flatMap { offer -> offer.getAllProducts() }.forEach { product ->
//            productDatastoreOutputPortAdapter.create(storeId, product)
//        }
//
//        categoryDatastoreOutputPortAdapter.create(storeId, category)
//
//        offers.forEach { offer ->
//            offerDatastoreOutputPortAdapter.create(storeId, category.id, offer)
//        }
//
//        val result = offerDatastoreOutputPortAdapter.getOffers(storeId, category.id, 20, 0)
//
//        result.isOk.shouldBeTrue()
//        result.value.toSet() shouldBeEqual offers.toSet()
//    }
//
//    @Test
//    fun `Should get 10 Offers with Customizations and Options successfully`() = runTest {
//        val storeId = Id(UUID.randomUUID())
//        val category = mockCategory()
//        val offers = List(10) {
//            mockOfferWith {
//                customizations = mutableListOf(mockCustomization())
//            }
//        }
//
//        offers.flatMap { offer -> offer.getAllProducts() }.forEach { product ->
//            productDatastoreOutputPortAdapter.create(storeId, product)
//        }
//
//        categoryDatastoreOutputPortAdapter.create(storeId, category)
//
//        offers.forEach { offer ->
//            offerDatastoreOutputPortAdapter.create(storeId, category.id, offer)
//        }
//
//        val result = offerDatastoreOutputPortAdapter.getOffers(storeId, category.id, 20, 0)
//
//        result.isOk.shouldBeTrue()
//        result.value.toSet() shouldBeEqual offers.toSet()
//    }
//
//    @Test
//    fun `Should throw DatastoreIntegrationException when creating an offer and the repository throws an exception`() = runTest {
//        val storeId = Id(UUID.randomUUID())
//        val categoryId = Id(UUID.randomUUID())
//        val offer = mockOffer()
//
//        every { offerPostgresRepository.create(any()) } throws genericTestException
//
//        val result = offerDatastoreOutputPortAdapter.create(storeId, categoryId, offer)
//
//        result.isErr.shouldBeTrue()
//        result.error shouldBe DatastoreIntegrationException(genericTestException)
//    }
//
//    @Test
//    fun `Should throw DatastoreIntegrationException when finding an offer and the repository throws an exception`() = runTest {
//        val storeId = Id(UUID.randomUUID())
//        val offerId = Id(UUID.randomUUID())
//
//        every { offerPostgresRepository.findById(any(), any()) } throws genericTestException
//
//        val result = offerDatastoreOutputPortAdapter.findById(storeId, offerId)
//
//        result.isErr.shouldBeTrue()
//        result.error shouldBe DatastoreIntegrationException(genericTestException)
//    }
//
//    @Test
//    fun `Should throw DatastoreIntegrationException when checking offer existence by ID and the repository throws an exception`() = runTest {
//        val offerId = Id(UUID.randomUUID())
//
//        every { offerPostgresRepository.exists(any()) } throws genericTestException
//
//        val result = offerDatastoreOutputPortAdapter.exists(offerId)
//
//        result.isErr.shouldBeTrue()
//        result.error shouldBe DatastoreIntegrationException(genericTestException)
//    }
//
//    @Test
//    fun `Should throw DatastoreIntegrationException when checking offer existence by store and ID and the repository throws an exception`() = runTest {
//        val storeId = Id(UUID.randomUUID())
//        val offerId = Id(UUID.randomUUID())
//
//        every { offerPostgresRepository.exists(any(), any()) } throws genericTestException
//
//        val result = offerDatastoreOutputPortAdapter.exists(storeId, offerId)
//
//        result.isErr.shouldBeTrue()
//        result.error shouldBe DatastoreIntegrationException(genericTestException)
//    }
//
//    @Test
//    fun `Should throw DatastoreIntegrationException when deleting an offer and the repository throws an exception`() = runTest {
//        val storeId = Id(UUID.randomUUID())
//        val offerId = Id(UUID.randomUUID())
//
//        every { offerPostgresRepository.delete(any(), any()) } throws genericTestException
//
//        val result = offerDatastoreOutputPortAdapter.delete(storeId, offerId)
//
//        result.isErr.shouldBeTrue()
//        result.error shouldBe DatastoreIntegrationException(genericTestException)
//    }
//
//    @Test
//    fun `Should throw DatastoreIntegrationException when updating an offer and the repository throws an exception`() = runTest {
//        val storeId = Id(UUID.randomUUID())
//        val offer = mockOffer()
//
//        every { offerPostgresRepository.update(any()) } throws genericTestException
//
//        val result = offerDatastoreOutputPortAdapter.update(storeId, offer)
//
//        result.isErr.shouldBeTrue()
//        result.error shouldBe DatastoreIntegrationException(genericTestException)
//    }
//
//    @Test
//    fun `Should throw DatastoreIntegrationException when counting offers and the repository throws an exception`() = runTest {
//        val storeId = Id(UUID.randomUUID())
//        val categoryId = Id(UUID.randomUUID())
//
//        every { offerPostgresRepository.countOffers(any(), any()) } throws genericTestException
//
//        val result = offerDatastoreOutputPortAdapter.countOffers(storeId, categoryId)
//
//        result.isErr.shouldBeTrue()
//        result.error shouldBe DatastoreIntegrationException(genericTestException)
//    }
//
//    @Test
//    fun `Should throw DatastoreIntegrationException when getting offers and the repository throws an exception`() = runTest {
//        val storeId = Id(UUID.randomUUID())
//        val categoryId = Id(UUID.randomUUID())
//
//        every { offerPostgresRepository.getOffers(any(), any(), any(), any()) } throws genericTestException
//
//        val result = offerDatastoreOutputPortAdapter.getOffers(storeId, categoryId, 20, 0)
//
//        result.isErr.shouldBeTrue()
//        result.error shouldBe DatastoreIntegrationException(genericTestException)
//    }
// }
