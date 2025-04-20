package br.com.rodrigogurgel.catalog.application.port.input.offer

import br.com.rodrigogurgel.catalog.application.exception.OfferNotFoundException
import br.com.rodrigogurgel.catalog.application.exception.StoreNotFoundException
import br.com.rodrigogurgel.catalog.application.port.output.datastore.OfferDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.StoreDatastoreOutputPort
import br.com.rodrigogurgel.catalog.common.logger.extensions.CUSTOMIZATION_ID
import br.com.rodrigogurgel.catalog.common.logger.extensions.OFFER_ID
import br.com.rodrigogurgel.catalog.common.logger.extensions.OPTION
import br.com.rodrigogurgel.catalog.common.logger.extensions.STORE_ID
import br.com.rodrigogurgel.catalog.common.logger.extensions.failure
import br.com.rodrigogurgel.catalog.common.logger.extensions.success
import br.com.rodrigogurgel.catalog.common.opentelemetry.extensions.coroutines.suspendSpan
import br.com.rodrigogurgel.catalog.domain.entity.Offer
import br.com.rodrigogurgel.catalog.domain.exception.CustomizationNotFoundException
import br.com.rodrigogurgel.catalog.domain.usecase.offer.RemoveOptionOnChildrenUseCase
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

class RemoveOptionOnChildrenInputPort(
    private val storeDatastoreOutputPort: StoreDatastoreOutputPort,
    private val offerDatastoreOutputPort: OfferDatastoreOutputPort,
) : RemoveOptionOnChildrenUseCase {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun execute(
        storeId: Id,
        offerId: Id,
        customizationId: Id,
        optionId: Id
    ) = suspendSpan(action()) {
        storeDatastoreOutputPort.exists(storeId)
            .toErrorIf({ !it }) { StoreNotFoundException(storeId) }
            .andThen { offerDatastoreOutputPort.findById(storeId, offerId) }
            .toErrorIfNull { OfferNotFoundException(storeId, offerId) }
            .andThen { removeOptionOnCustomization(it, customizationId, optionId) }
            .andThen { offerDatastoreOutputPort.update(storeId, it) }
            .onSuccess {
                logger.success(
                    action(),
                    STORE_ID to storeId,
                    OFFER_ID to offerId,
                    CUSTOMIZATION_ID to customizationId,
                    OPTION to optionId,
                )
            }
            .onFailure {
                logger.failure(
                    action(),
                    it,
                    STORE_ID to storeId,
                    OFFER_ID to offerId,
                    CUSTOMIZATION_ID to customizationId,
                    OPTION to optionId,
                )
            }
    }

    private fun removeOptionOnCustomization(
        offer: Offer,
        customizationId: Id,
        optionId: Id
    ): Result<Offer, Throwable> = runSuspendCatching {
        val customization = offer.findCustomizationInChildrenById(customizationId)
        customization?.removeOption(optionId) ?: throw CustomizationNotFoundException(customizationId)
        offer.validate()
    }.map { offer }

    override fun action() = "remove-option-on-children"
}
