package br.com.rodrigogurgel.catalog.application.port.input.offer

import br.com.rodrigogurgel.catalog.application.exception.CategoryNotFoundException
import br.com.rodrigogurgel.catalog.application.exception.OfferAlreadyExistsException
import br.com.rodrigogurgel.catalog.application.exception.ProductsNotFoundException
import br.com.rodrigogurgel.catalog.application.exception.StoreNotFoundException
import br.com.rodrigogurgel.catalog.application.port.output.datastore.CategoryDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.OfferDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.ProductDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.StoreDatastoreOutputPort
import br.com.rodrigogurgel.catalog.common.logger.extensions.CATEGORY_ID
import br.com.rodrigogurgel.catalog.common.logger.extensions.OFFER
import br.com.rodrigogurgel.catalog.common.logger.extensions.STORE_ID
import br.com.rodrigogurgel.catalog.common.logger.extensions.failure
import br.com.rodrigogurgel.catalog.common.logger.extensions.success
import br.com.rodrigogurgel.catalog.common.opentelemetry.extensions.coroutines.suspendSpan
import br.com.rodrigogurgel.catalog.domain.entity.Offer
import br.com.rodrigogurgel.catalog.domain.usecase.offer.CreateOfferUseCase
import br.com.rodrigogurgel.catalog.domain.vo.Id
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.map
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.github.michaelbull.result.toErrorIf
import org.slf4j.LoggerFactory

class CreateOfferInputPort(
    private val storeDatastoreOutputPort: StoreDatastoreOutputPort,
    private val categoryDatastoreOutputPort: CategoryDatastoreOutputPort,
    private val productDatastoreOutputPort: ProductDatastoreOutputPort,
    private val offerDatastoreOutputPort: OfferDatastoreOutputPort,
) : CreateOfferUseCase {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun execute(
        storeId: Id,
        categoryId: Id,
        offer: Offer
    ) = suspendSpan(action()) {
        storeDatastoreOutputPort.exists(storeId)
            .toErrorIf({ !it }) { StoreNotFoundException(storeId) }
            .andThen { categoryDatastoreOutputPort.exists(storeId, categoryId) }
            .toErrorIf({ !it }) { CategoryNotFoundException(storeId, categoryId) }
            .andThen { offerDatastoreOutputPort.exists(storeId, offer.id) }
            .toErrorIf({ it }) { OfferAlreadyExistsException(offer.id) }
            .andThen { validateOffer(storeId, offer) }
            .andThen { offerDatastoreOutputPort.create(storeId, categoryId, offer) }
            .onSuccess {
                logger.success(
                    action(),
                    STORE_ID to storeId,
                    CATEGORY_ID to categoryId,
                    OFFER to offer,
                )
            }
            .onFailure {
                logger.failure(
                    action(),
                    it,
                    STORE_ID to storeId,
                    CATEGORY_ID to categoryId,
                    OFFER to offer,
                )
            }
    }

    private suspend fun validateOffer(storeId: Id, offer: Offer): Result<Offer, Throwable> = runSuspendCatching {
        offer.validate()
    }.andThen {
        val productIds = offer.getAllProducts().map { it.id }
        productDatastoreOutputPort.getIfNotExists(storeId, productIds)
            .toErrorIf({ it.isNotEmpty() }) { ProductsNotFoundException(it) }
    }.map { offer }

    override fun action() = "create-offer"
}
