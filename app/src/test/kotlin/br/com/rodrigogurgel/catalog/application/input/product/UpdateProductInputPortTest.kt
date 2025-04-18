package br.com.rodrigogurgel.catalog.application.input.product

import br.com.rodrigogurgel.catalog.application.exception.ProductNotFoundException
import br.com.rodrigogurgel.catalog.application.exception.StoreNotFoundException
import br.com.rodrigogurgel.catalog.application.port.input.product.UpdateProductInputPort
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

class UpdateProductInputPortTest {
    private val storeDatastoreOutputPort: StoreDatastoreOutputPort = mockk()
    private val productDatastoreOutputPort: ProductDatastoreOutputPort = mockk()
    private val updateProductInputPort = UpdateProductInputPort(
        storeDatastoreOutputPort,
        productDatastoreOutputPort,
    )

    @Test
    fun `Should successfully update a product`() = runTest {
        val storeId = Id()
        val product = mockProduct()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { productDatastoreOutputPort.exists(storeId, product.id) } returns Ok(true)
        coEvery { productDatastoreOutputPort.update(storeId, product) } returns Ok(Unit)

        val result = updateProductInputPort.execute(storeId, product)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            productDatastoreOutputPort.exists(storeId, product.id)
            productDatastoreOutputPort.update(storeId, product)
        }
    }

    @Test
    fun `Should fail to update a product when the store does not exist`() = runTest {
        val storeId = Id()
        val product = mockProduct()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(false)

        val result = updateProductInputPort.execute(storeId, product)

        result.isErr.shouldBeTrue()
        result.error shouldBe StoreNotFoundException(storeId)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
        }
    }

    @Test
    fun `Should fail to update a product when the product does not exist`() = runTest {
        val storeId = Id()
        val product = mockProduct()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { productDatastoreOutputPort.exists(storeId, product.id) } returns Ok(false)

        val result = updateProductInputPort.execute(storeId, product)

        result.isErr.shouldBeTrue()
        result.error shouldBe ProductNotFoundException(storeId, product.id)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            productDatastoreOutputPort.exists(storeId, product.id)
        }
    }
}
