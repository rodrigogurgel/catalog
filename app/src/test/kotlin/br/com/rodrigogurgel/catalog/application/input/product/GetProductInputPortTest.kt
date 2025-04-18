package br.com.rodrigogurgel.catalog.application.input.product

import br.com.rodrigogurgel.catalog.application.exception.ProductNotFoundException
import br.com.rodrigogurgel.catalog.application.exception.StoreNotFoundException
import br.com.rodrigogurgel.catalog.application.port.output.datastore.ProductDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.StoreDatastoreOutputPort
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.fixture.mock.mockProduct
import com.github.michaelbull.result.Ok
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class GetProductInputPortTest {
    private val storeDatastoreOutputPort: StoreDatastoreOutputPort = mockk()
    private val productDatastoreOutputPort: ProductDatastoreOutputPort = mockk()
    private val getProductInputPort = br.com.rodrigogurgel.catalog.application.port.input.product.GetProductInputPort(
        storeDatastoreOutputPort,
        productDatastoreOutputPort,
    )

    @Test
    fun `Should successfully get a product`() = runTest {
        val storeId = Id()
        val product = mockProduct()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { productDatastoreOutputPort.findById(storeId, product.id) } returns Ok(product)

        val result = getProductInputPort.execute(storeId, product.id)

        result.isOk.shouldBeTrue()
        result.value shouldBe product

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            productDatastoreOutputPort.findById(storeId, product.id)
        }
    }

    @Test
    fun `Should fail to get a product when the store does not exist`() = runTest {
        val storeId = Id()
        val product = mockProduct()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(false)

        val result = getProductInputPort.execute(storeId, product.id)

        result.isErr.shouldBeTrue()
        result.error shouldBe StoreNotFoundException(storeId)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
        }
    }

    @Test
    fun `Should fail to get a product when the product does not exist`() = runTest {
        val storeId = Id()
        val product = mockProduct()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { productDatastoreOutputPort.findById(storeId, product.id) } returns Ok(null)

        val result = getProductInputPort.execute(storeId, product.id)

        result.isErr.shouldBeTrue()
        result.error shouldBe ProductNotFoundException(storeId, product.id)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            productDatastoreOutputPort.findById(storeId, product.id)
        }
    }
}
