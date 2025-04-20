package br.com.rodrigogurgel.catalog.application.port.input.offer

import br.com.rodrigogurgel.catalog.application.exception.OfferNotFoundException
import br.com.rodrigogurgel.catalog.application.exception.ProductsNotFoundException
import br.com.rodrigogurgel.catalog.application.exception.StoreNotFoundException
import br.com.rodrigogurgel.catalog.application.port.output.datastore.OfferDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.ProductDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.StoreDatastoreOutputPort
import br.com.rodrigogurgel.catalog.common.logger.extensions.CUSTOMIZATION_ID
import br.com.rodrigogurgel.catalog.common.logger.extensions.OFFER_ID
import br.com.rodrigogurgel.catalog.common.logger.extensions.OPTION
import br.com.rodrigogurgel.catalog.common.logger.extensions.STORE_ID
import br.com.rodrigogurgel.catalog.common.logger.extensions.failure
import br.com.rodrigogurgel.catalog.common.logger.extensions.success
import br.com.rodrigogurgel.catalog.common.opentelemetry.extensions.coroutines.suspendSpan
import br.com.rodrigogurgel.catalog.domain.entity.Offer
import br.com.rodrigogurgel.catalog.domain.entity.Option
import br.com.rodrigogurgel.catalog.domain.exception.CustomizationNotFoundException
import br.com.rodrigogurgel.catalog.domain.usecase.offer.UpdateOptionOnChildrenUseCase
import br.com.rodrigogurgel.catalog.domain.vo.Id
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.map
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.github.michaelbull.result.toErrorIf
import com.github.michaelbull.result.toErrorIfNull
import org.slf4j.LoggerFactory

class UpdateOptionOnChildrenInputPort(
    private val storeDatastoreOutputPort: StoreDatastoreOutputPort,
    private val offerDatastoreOutputPort: OfferDatastoreOutputPort,
    private val productDatastoreOutputPort: ProductDatastoreOutputPort,
) : UpdateOptionOnChildrenUseCase {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun execute(
        storeId: Id,
        offerId: Id,
        customizationId: Id,
        option: Option
    ) = suspendSpan(action()) {
        storeDatastoreOutputPort.exists(storeId)
            .toErrorIf({ !it }) { StoreNotFoundException(storeId) }
            .andThen { offerDatastoreOutputPort.findById(storeId, offerId) }
            .toErrorIfNull { OfferNotFoundException(storeId, offerId) }
            .andThen { updateOptionOnCustomization(storeId, it, customizationId, option) }
            .andThen { offerDatastoreOutputPort.update(storeId, it) }
            .onSuccess {
                logger.success(
                    action(),
                    STORE_ID to storeId,
                    OFFER_ID to offerId,
                    CUSTOMIZATION_ID to customizationId,
                    OPTION to OPTION
                )
            }
            .onFailure {
                logger.failure(
                    action(),
                    it,
                    STORE_ID to storeId,
                    OFFER_ID to offerId,
                    CUSTOMIZATION_ID to customizationId,
                    OPTION to OPTION
                )
            }
    }

    private suspend fun updateOptionOnCustomization(
        storeId: Id,
        offer: Offer,
        customizationId: Id,
        option: Option
    ): Result<Offer, Throwable> = runSuspendCatching {
        val customization =
            offer.findCustomizationInChildrenById(customizationId) ?: throw CustomizationNotFoundException(
                customizationId
            )
        customization.updateOption(option)
        offer.validate()
    }.andThen {
        val productIds = offer.getAllProducts().map { it.id }
        productDatastoreOutputPort.getIfNotExists(storeId, productIds)
            .toErrorIf({ it.isNotEmpty() }) { ProductsNotFoundException(it) }
    }.map { offer }

    override fun action() = "update-option-on-children"
}
