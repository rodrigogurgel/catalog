package br.com.rodrigogurgel.catalog.application.input.offer

import br.com.rodrigogurgel.catalog.application.exception.StoreNotFoundException
import br.com.rodrigogurgel.catalog.application.port.input.offer.DeleteOfferInputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.OfferDatastoreOutputPort
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

class DeleteOfferInputPortTest {
    private val storeDatastoreOutputPort: StoreDatastoreOutputPort = mockk()
    private val offerDatastoreOutputPort: OfferDatastoreOutputPort = mockk()

    private val deleteOfferInputPort = DeleteOfferInputPort(
        storeDatastoreOutputPort,
        offerDatastoreOutputPort
    )

    @Test
    fun `Should successfully delete an offer`() = runTest {
        val storeId = Id()
        val offerId = Id()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { offerDatastoreOutputPort.delete(storeId, offerId) } returns Ok(Unit)

        val result = deleteOfferInputPort.execute(storeId, offerId)

        result.isOk.shouldBeTrue()

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            offerDatastoreOutputPort.delete(storeId, offerId)
        }
    }

    @Test
    fun `Should fail to delete an offer when the store does not exist`() = runTest {
        val storeId = Id()
        val offerId = Id()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(false)

        val result = deleteOfferInputPort.execute(storeId, offerId)

        result.isErr.shouldBeTrue()
        result.error shouldBe StoreNotFoundException(storeId)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
        }
    }
}
