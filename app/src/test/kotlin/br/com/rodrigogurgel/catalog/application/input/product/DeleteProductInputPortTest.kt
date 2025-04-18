package br.com.rodrigogurgel.catalog.application.input.product

import br.com.rodrigogurgel.catalog.application.exception.ProductIsInUseException
import br.com.rodrigogurgel.catalog.application.exception.StoreNotFoundException
import br.com.rodrigogurgel.catalog.application.port.input.product.DeleteProductInputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.ProductDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.StoreDatastoreOutputPort
import br.com.rodrigogurgel.catalog.domain.vo.Id
import com.github.michaelbull.result.Ok
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class DeleteProductInputPortTest {
    private val storeDatastoreOutputPort: StoreDatastoreOutputPort = mockk()
    private val productDatastoreOutputPort: ProductDatastoreOutputPort = mockk()
    private val deleteProductInputPort = DeleteProductInputPort(
        storeDatastoreOutputPort,
        productDatastoreOutputPort,
    )

    @Test
    fun `Should successfully delete a product`() = runTest {
        val storeId = Id()
        val productId = Id()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { productDatastoreOutputPort.productIsInUse(productId) } returns Ok(false)
        coEvery { productDatastoreOutputPort.delete(storeId, productId) } returns Ok(Unit)

        val result = deleteProductInputPort.execute(storeId, productId)

        result.isOk.shouldBeTrue()

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            productDatastoreOutputPort.productIsInUse(productId)
            productDatastoreOutputPort.delete(storeId, productId)
        }
    }

    @Test
    fun `Should fail to delete a product when the store does not exist`() = runTest {
        val storeId = Id()
        val productId = Id()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(false)

        val result = deleteProductInputPort.execute(storeId, productId)

        result.isErr.shouldBeTrue()
        result.error shouldBe StoreNotFoundException(storeId)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
        }
    }

    @Test
    fun `Should fail to delete a product when the product is in use`() = runTest {
        val storeId = Id()
        val productId = Id()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { productDatastoreOutputPort.productIsInUse(productId) } returns Ok(true)

        val result = deleteProductInputPort.execute(storeId, productId)

        result.isErr.shouldBeTrue()
        result.error shouldBe ProductIsInUseException(productId)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            productDatastoreOutputPort.productIsInUse(productId)
        }
    }
}
