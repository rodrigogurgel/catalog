package br.com.rodrigogurgel.catalog.application.input.offer

import br.com.rodrigogurgel.catalog.application.exception.OfferNotFoundException
import br.com.rodrigogurgel.catalog.application.exception.ProductsNotFoundException
import br.com.rodrigogurgel.catalog.application.exception.StoreNotFoundException
import br.com.rodrigogurgel.catalog.application.port.input.offer.AddCustomizationOnChildrenInputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.OfferDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.ProductDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.StoreDatastoreOutputPort
import br.com.rodrigogurgel.catalog.domain.exception.OptionNotFoundException
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.fixture.mock.mockCustomization
import br.com.rodrigogurgel.catalog.fixture.mock.mockCustomizationWith
import br.com.rodrigogurgel.catalog.fixture.mock.mockOfferWith
import br.com.rodrigogurgel.catalog.fixture.mock.mockOption
import com.github.michaelbull.result.Ok
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class AddCustomizationOnChildrenInputPortTest {
    private val storeDatastoreOutputPort: StoreDatastoreOutputPort = mockk()
    private val offerDatastoreOutputPort: OfferDatastoreOutputPort = mockk()
    private val productDatastoreOutputPort: ProductDatastoreOutputPort = mockk()

    private val addCustomizationOnChildrenInputPort = AddCustomizationOnChildrenInputPort(
        storeDatastoreOutputPort,
        offerDatastoreOutputPort,
        productDatastoreOutputPort
    )

    @Test
    fun `Should successfully add a customization on children`() = runTest {
        val storeId = Id()
        val category = Id()
        val option = mockOption()
        val offer = mockOfferWith {
            customizations = mutableListOf(
                mockCustomizationWith {
                    options = mutableListOf(option)
                }
            )
        }
        val customization = mockCustomization()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { offerDatastoreOutputPort.findById(storeId, offer.id) } returns Ok(offer)
        coEvery { offerDatastoreOutputPort.update(storeId, category, offer) } returns Ok(Unit)
        coEvery {
            productDatastoreOutputPort.getIfNotExists(storeId, match { ids -> ids.containsAll(offer.getAllProducts().map { product -> product.id }) })
        } returns Ok(emptyList())

        val result = addCustomizationOnChildrenInputPort.execute(storeId, category, offer.id, option.id, customization)

        result.isOk.shouldBeTrue()

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            offerDatastoreOutputPort.findById(storeId, offer.id)
            productDatastoreOutputPort.getIfNotExists(
                storeId,
                match { ids -> ids.containsAll(offer.getAllProducts().map { product -> product.id }) }
            )
            offerDatastoreOutputPort.update(storeId, category, offer)
        }
    }

    @Test
    fun `Should fail to add a customization on children when the parent option does not exist`() = runTest {
        val storeId = Id()
        val category = Id()
        val option = mockOption()
        val offer = mockOfferWith {
        }
        val customization = mockCustomization()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { offerDatastoreOutputPort.findById(storeId, offer.id) } returns Ok(offer)

        val result = addCustomizationOnChildrenInputPort.execute(storeId, category, offer.id, option.id, customization)

        result.isErr.shouldBeTrue()
        result.error shouldBe OptionNotFoundException(option.id)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            offerDatastoreOutputPort.findById(storeId, offer.id)
        }
    }

    @Test
    fun `Should fail to add a customization on children when the store does not exist`() = runTest {
        val storeId = Id()
        val category = Id()
        val option = mockOption()
        val offer = mockOfferWith {
            customizations = mutableListOf(
                mockCustomizationWith {
                    options = mutableListOf(option)
                }
            )
        }
        val customization = mockCustomization()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(false)

        val result = addCustomizationOnChildrenInputPort.execute(storeId, category, offer.id, option.id, customization)

        result.isErr.shouldBeTrue()
        result.error shouldBe StoreNotFoundException(storeId)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
        }
    }

    @Test
    fun `Should fail to add a customization on children when the offer does not exist`() = runTest {
        val storeId = Id()
        val category = Id()
        val option = mockOption()
        val offer = mockOfferWith {
            customizations = mutableListOf(
                mockCustomizationWith {
                    options = mutableListOf(option)
                }
            )
        }
        val customization = mockCustomization()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { offerDatastoreOutputPort.findById(storeId, offer.id) } returns Ok(null)

        val result = addCustomizationOnChildrenInputPort.execute(storeId, category, offer.id, option.id, customization)

        result.isErr.shouldBeTrue()
        result.error shouldBe OfferNotFoundException(storeId, offer.id)

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            offerDatastoreOutputPort.findById(storeId, offer.id)
        }
    }

    @Test
    fun `Should fail to add a customization on children when the product does not exist`() = runTest {
        val storeId = Id()
        val category = Id()
        val option = mockOption()
        val offer = mockOfferWith {
            customizations = mutableListOf(
                mockCustomizationWith {
                    options = mutableListOf(option)
                }
            )
        }
        val customization = mockCustomization()

        coEvery { storeDatastoreOutputPort.exists(storeId) } returns Ok(true)
        coEvery { offerDatastoreOutputPort.findById(storeId, offer.id) } returns Ok(offer)
        coEvery { offerDatastoreOutputPort.update(storeId, category, offer) } returns Ok(Unit)
        coEvery {
            productDatastoreOutputPort.getIfNotExists(storeId, match { ids -> ids.containsAll(offer.getAllProducts().map { product -> product.id }) })
        } returns Ok(listOf(option.product!!.id))

        val result = addCustomizationOnChildrenInputPort.execute(storeId, category, offer.id, option.id, customization)

        result.isErr.shouldBeTrue()
        result.error shouldBe ProductsNotFoundException(listOf(option.product!!.id))

        coVerifySequence {
            storeDatastoreOutputPort.exists(storeId)
            offerDatastoreOutputPort.findById(storeId, offer.id)
            productDatastoreOutputPort.getIfNotExists(
                storeId,
                match { ids -> ids.containsAll(offer.getAllProducts().map { product -> product.id }) }
            )
        }
    }
}
