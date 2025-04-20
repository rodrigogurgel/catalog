package br.com.rodrigogurgel.catalog.application.input.product

import br.com.rodrigogurgel.catalog.application.exception.ProductAlreadyExistsException
import br.com.rodrigogurgel.catalog.application.exception.StoreNotFoundException
import br.com.rodrigogurgel.catalog.application.port.input.product.CreateProductInputPort
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

class CreateProductInputPortTest {
    private val storeDatastoreOutputPort: StoreDatastoreOutputPort = mockk()
    private val productDatastoreOutputPort: ProductDatastoreOutputPort = mockk()
    private val createProductInputPort = CreateProductInputPort(
        storeDatastoreOutputPort,
        productDatastoreOutputPort,
    )

    @Test
    fun `Should successfully create a product`() = runTest {
        val storeId = Id()
        val product = mockProduct()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { productDatastoreOutputPort.exists(storeId, product.id) } returns Ok(false)
        coEvery { productDatastoreOutputPort.create(storeId, product) } returns Ok(Unit)

        createProductInputPort.execute(storeId, product)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            productDatastoreOutputPort.exists(storeId, product.id)
            productDatastoreOutputPort.create(storeId, product)
        }
    }

    @Test
    fun `Should fail to create a product when the store does not exist`() = runTest {
        val storeId = Id()
        val product = mockProduct()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(false)

        val result = createProductInputPort.execute(storeId, product)

        result.isErr.shouldBeTrue()
        result.error shouldBe StoreNotFoundException(storeId)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
        }
    }

    @Test
    fun `Should fail to create a product when a product with the same ID already exists`() = runTest {
        val storeId = Id()
        val product = mockProduct()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { productDatastoreOutputPort.exists(storeId, product.id) } returns Ok(true)

        val result = createProductInputPort.execute(storeId, product)

        result.isErr.shouldBeTrue()
        result.error shouldBe ProductAlreadyExistsException(product.id)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            productDatastoreOutputPort.exists(storeId, product.id)
        }
    }
}
