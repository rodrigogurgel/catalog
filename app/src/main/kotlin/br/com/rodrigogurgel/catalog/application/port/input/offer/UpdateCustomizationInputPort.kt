package br.com.rodrigogurgel.catalog.application.port.input.offer

import br.com.rodrigogurgel.catalog.application.exception.OfferNotFoundException
import br.com.rodrigogurgel.catalog.application.exception.ProductsNotFoundException
import br.com.rodrigogurgel.catalog.application.exception.StoreNotFoundException
import br.com.rodrigogurgel.catalog.application.port.output.datastore.OfferDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.ProductDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.StoreDatastoreOutputPort
import br.com.rodrigogurgel.catalog.common.logger.extensions.CATEGORY_ID
import br.com.rodrigogurgel.catalog.common.logger.extensions.CUSTOMIZATION
import br.com.rodrigogurgel.catalog.common.logger.extensions.OFFER_ID
import br.com.rodrigogurgel.catalog.common.logger.extensions.STORE_ID
import br.com.rodrigogurgel.catalog.common.logger.extensions.failure
import br.com.rodrigogurgel.catalog.common.logger.extensions.success
import br.com.rodrigogurgel.catalog.common.opentelemetry.extensions.coroutines.suspendSpan
import br.com.rodrigogurgel.catalog.domain.entity.Customization
import br.com.rodrigogurgel.catalog.domain.entity.Offer
import br.com.rodrigogurgel.catalog.domain.usecase.offer.UpdateCustomizationUseCase
import br.com.rodrigogurgel.catalog.domain.vo.Id
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.map
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.github.michaelbull.result.toErrorIf
import com.github.michaelbull.result.toErrorIfNull
import org.slf4j.LoggerFactory

class UpdateCustomizationInputPort(
    private val storeDatastoreOutputPort: StoreDatastoreOutputPort,
    private val offerDatastoreOutputPort: OfferDatastoreOutputPort,
    private val productDatastoreOutputPort: ProductDatastoreOutputPort,
) : UpdateCustomizationUseCase {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun execute(
        storeId: Id,
        categoryId: Id,
        offerId: Id,
        customization: Customization
    ) = suspendSpan(action()) {
        storeDatastoreOutputPort.exists(storeId)
            .toErrorIf({ !it }) { StoreNotFoundException(storeId) }
            .andThen { offerDatastoreOutputPort.findById(storeId, offerId) }
            .toErrorIfNull { OfferNotFoundException(storeId, offerId) }
            .andThen { updateCustomization(storeId, it, customization) }
            .andThen { offerDatastoreOutputPort.update(storeId, categoryId, it) }
            .onSuccess {
                logger.success(
                    action(),
                    STORE_ID to storeId,
                    CATEGORY_ID to categoryId,
                    OFFER_ID to offerId,
                    CUSTOMIZATION to customization,
                )
            }
            .onFailure {
                logger.failure(
                    action(),
                    it,
                    STORE_ID to storeId,
                    CATEGORY_ID to categoryId,
                    OFFER_ID to offerId,
                    CUSTOMIZATION to customization,
                )
            }
    }

    private suspend fun updateCustomization(
        storeId: Id,
        offer: Offer,
        customization: Customization
    ) = runSuspendCatching {
        offer.updateCustomization(customization)
        offer.validate()
    }.andThen {
        val productIds = offer.getAllProducts().map { it.id }
        productDatastoreOutputPort.getIfNotExists(storeId, productIds)
            .toErrorIf({ it.isNotEmpty() }) { ProductsNotFoundException(it) }
    }.map { offer }

    override fun action() = "update-customization"
}
