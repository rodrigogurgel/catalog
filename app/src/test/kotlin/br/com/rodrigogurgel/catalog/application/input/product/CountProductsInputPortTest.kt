package br.com.rodrigogurgel.catalog.application.input.product

import br.com.rodrigogurgel.catalog.application.exception.StoreNotFoundException
import br.com.rodrigogurgel.catalog.application.port.input.product.CountProductsInputPort
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

class CountProductsInputPortTest {
    private val storeDatastoreOutputPort: StoreDatastoreOutputPort = mockk()
    private val productDatastoreOutputPort: ProductDatastoreOutputPort = mockk()
    private val countProductsInputPort = CountProductsInputPort(
        storeDatastoreOutputPort,
        productDatastoreOutputPort
    )

    @Test
    fun `Should successfully count products`() = runTest {
        val storeId = Id()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { productDatastoreOutputPort.countProducts(storeId) } returns Ok(1)

        val result = countProductsInputPort.execute(storeId)

        result.isOk.shouldBeTrue()
        result.value shouldBe 1

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            productDatastoreOutputPort.countProducts(storeId)
        }
    }

    @Test
    fun `Should fail to get products when the store does not exist`() = runTest {
        val storeId = Id()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(false)

        val result = countProductsInputPort.execute(storeId)

        result.isErr.shouldBeTrue()
        result.error shouldBe StoreNotFoundException(storeId)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
        }
    }
}
