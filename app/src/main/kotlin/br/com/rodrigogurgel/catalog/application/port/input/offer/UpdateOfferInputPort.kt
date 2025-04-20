package br.com.rodrigogurgel.catalog.application.port.input.offer

import br.com.rodrigogurgel.catalog.application.exception.OfferNotFoundException
import br.com.rodrigogurgel.catalog.application.exception.ProductsNotFoundException
import br.com.rodrigogurgel.catalog.application.exception.StoreNotFoundException
import br.com.rodrigogurgel.catalog.application.port.output.datastore.OfferDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.ProductDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.StoreDatastoreOutputPort
import br.com.rodrigogurgel.catalog.common.logger.extensions.OFFER
import br.com.rodrigogurgel.catalog.common.logger.extensions.STORE_ID
import br.com.rodrigogurgel.catalog.common.logger.extensions.failure
import br.com.rodrigogurgel.catalog.common.logger.extensions.success
import br.com.rodrigogurgel.catalog.common.opentelemetry.extensions.coroutines.suspendSpan
import br.com.rodrigogurgel.catalog.domain.entity.Offer
import br.com.rodrigogurgel.catalog.domain.usecase.offer.UpdateOfferUseCase
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

class UpdateOfferInputPort(
    private val storeDatastoreOutputPort: StoreDatastoreOutputPort,
    private val productDatastoreOutputPort: ProductDatastoreOutputPort,
    private val offerDatastoreOutputPort: OfferDatastoreOutputPort,
) : UpdateOfferUseCase {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun execute(
        storeId: Id,
        offer: Offer
    ) = suspendSpan(action()) {
        storeDatastoreOutputPort.exists(storeId)
            .toErrorIf({ !it }) { StoreNotFoundException(storeId) }
            .andThen { offerDatastoreOutputPort.findById(storeId, offer.id) }
            .toErrorIfNull { OfferNotFoundException(storeId, offer.id) }
            .andThen { validateOffer(storeId, offer) }
            .andThen { offerDatastoreOutputPort.update(storeId, offer) }
            .onSuccess {
                logger.success(
                    action(),
                    STORE_ID to storeId,
                    OFFER to offer,
                )
            }
            .onFailure {
                logger.failure(
                    action(),
                    it,
                    STORE_ID to storeId,
                    OFFER to offer,
                )
            }
    }

    private suspend fun validateOffer(storeId: Id, offer: Offer): Result<Offer, Throwable> = runSuspendCatching {
        offer.validate()
    }.andThen {
        val productIds = offer.getAllProducts().map { it.id }
        productDatastoreOutputPort.getIfNotExists(storeId, productIds)
    }.toErrorIf({ it.isNotEmpty() }) { ProductsNotFoundException(it) }
        .map { offer }

    override fun action() = "update-offer"
}
