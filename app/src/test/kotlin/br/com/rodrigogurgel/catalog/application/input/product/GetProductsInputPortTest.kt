package br.com.rodrigogurgel.catalog.application.input.product

import br.com.rodrigogurgel.catalog.application.exception.StoreNotFoundException
import br.com.rodrigogurgel.catalog.application.port.input.product.GetProductsInputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.ProductDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.StoreDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.utils.normalizeLimit
import br.com.rodrigogurgel.catalog.application.utils.normalizeOffset
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.fixture.mock.mockProduct
import com.github.michaelbull.result.Ok
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class GetProductsInputPortTest {
    private val storeDatastoreOutputPort: StoreDatastoreOutputPort = mockk()
    private val productDatastoreOutputPort: ProductDatastoreOutputPort = mockk()
    private val getProductsInputPort = GetProductsInputPort(
        storeDatastoreOutputPort,
        productDatastoreOutputPort
    )

    @Test
    fun `Should successfully get products`() = runTest {
        val storeId = Id()
        val products = List(20) { mockProduct() }
        val offset = 0
        val cursor: String? = null

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { productDatastoreOutputPort.getProducts(storeId, offset, cursor) } returns Ok(null to products)

        val result = getProductsInputPort.execute(storeId, offset, cursor)

        result.isOk.shouldBeTrue()
        result.value.first.shouldBeNull()
        result.value.second shouldBe products

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            productDatastoreOutputPort.getProducts(storeId, offset, cursor)
        }
    }

    @Test
    fun `Should successfully get products when the limit parameter is negative`() = runTest {
        val storeId = Id()
        val products = List(20) { mockProduct() }
        val offset = 0
        val cursor: String? = null

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { productDatastoreOutputPort.getProducts(storeId, offset, cursor) } returns Ok(null to products)

        val result = getProductsInputPort.execute(storeId, offset, cursor)

        result.isOk.shouldBeTrue()
        result.value.first.shouldBeNull()
        result.value.second shouldBe products

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            productDatastoreOutputPort.getProducts(storeId, offset, cursor)
        }
    }

    @Test
    fun `Should successfully get products when the limit parameter is greater than 20`() = runTest {
        val storeId = Id()
        val products = List(20) { mockProduct() }
        val offset = -1
        val cursor: String? = null

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery {
            productDatastoreOutputPort.getProducts(storeId, normalizeOffset(offset), cursor)
        } returns Ok(null to products)

        val result = getProductsInputPort.execute(storeId, offset, cursor)

        result.isOk.shouldBeTrue()
        result.value.first.shouldBeNull()
        result.value.second shouldBe products

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            productDatastoreOutputPort.getProducts(storeId, normalizeOffset(offset), cursor)
        }
    }

    @Test
    fun `Should successfully get products when the offset parameter is not null`() = runTest {
        val storeId = Id()
        val products = List(20) { mockProduct() }
        val offset = -1
        val cursor = "test"

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery {
            productDatastoreOutputPort.getProducts(storeId, normalizeLimit(offset), cursor)
        } returns Ok(cursor to products)

        val result = getProductsInputPort.execute(storeId, offset, cursor)

        result.isOk.shouldBeTrue()
        result.value.first shouldBe cursor
        result.value.second shouldBe products

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            productDatastoreOutputPort.getProducts(storeId, normalizeLimit(offset), cursor)
        }
    }

    @Test
    fun `Should fail to get products when the store does not exist`() = runTest {
        val storeId = Id()
        val offset = 0
        val cursor: String? = null

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(false)

        val result = getProductsInputPort.execute(storeId, offset, cursor)

        result.isErr.shouldBeTrue()
        result.error shouldBe StoreNotFoundException(storeId)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
        }
    }
}
