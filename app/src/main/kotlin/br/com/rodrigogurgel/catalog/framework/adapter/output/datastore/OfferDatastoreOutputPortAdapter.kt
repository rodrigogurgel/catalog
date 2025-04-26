package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore

import br.com.rodrigogurgel.catalog.application.port.output.datastore.OfferDatastoreOutputPort
import br.com.rodrigogurgel.catalog.common.logger.extensions.CATEGORY_ID
import br.com.rodrigogurgel.catalog.common.logger.extensions.CURSOR
import br.com.rodrigogurgel.catalog.common.logger.extensions.LIMIT
import br.com.rodrigogurgel.catalog.common.logger.extensions.OFFER
import br.com.rodrigogurgel.catalog.common.logger.extensions.OFFER_ID
import br.com.rodrigogurgel.catalog.common.logger.extensions.RESULT
import br.com.rodrigogurgel.catalog.common.logger.extensions.STORE_ID
import br.com.rodrigogurgel.catalog.common.logger.extensions.failure
import br.com.rodrigogurgel.catalog.common.logger.extensions.success
import br.com.rodrigogurgel.catalog.common.opentelemetry.extensions.coroutines.suspendSpan
import br.com.rodrigogurgel.catalog.domain.entity.Offer
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.exception.DatastoreIntegrationException
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.mapper.OfferMapper.asEntity
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.mapper.OfferMapper.asModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.OfferModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.ProductModelId
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.ProductOfferRelationModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.ProductOfferRelationModelId
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.repository.OfferRepository
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.repository.ProductOfferRelationRepository
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.repository.ProductRepository
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.utils.CursorUtils
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.github.michaelbull.result.runCatching
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@Component
class OfferDatastoreOutputPortAdapter(
    private val offerRepository: OfferRepository,
    private val productRepository: ProductRepository,
    private val productOfferRelationRepository: ProductOfferRelationRepository
) : OfferDatastoreOutputPort {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)

        private const val COUNT = "offer-datastore-output-port-count-offers"
        private const val CREATE = "offer-datastore-output-port-create"
        private const val DELETE = "offer-datastore-output-port-delete"
        private const val EXISTS = "offer-datastore-output-port-exists"
        private const val FIND_BY_ID = "offer-datastore-output-port-find-by-id"
        private const val GET_OFFERS = "offer-datastore-output-port-get-offers"
        private const val UPDATE = "offer-datastore-output-port-update"
    }

    override suspend fun countOffers(
        storeId: Id,
        categoryId: Id
    ): Result<Int, Throwable> = suspendSpan(COUNT) {
        runCatching {
            offerRepository.countByOfferModelId_StoreIdAndOfferModelId_CategoryId(
                storeId.value,
                categoryId.value
            ).awaitSingle()
        }
            .mapError { DatastoreIntegrationException(it) }
            .onSuccess {
                logger.success(
                    COUNT,
                    STORE_ID to storeId,
                    CATEGORY_ID to categoryId,
                    RESULT to it
                )
            }
            .onFailure {
                logger.failure(
                    COUNT,
                    it,
                    STORE_ID to storeId,
                    CATEGORY_ID to categoryId,
                )
            }
    }

    override suspend fun create(
        storeId: Id,
        categoryId: Id,
        offer: Offer
    ): Result<Unit, Throwable> = suspendSpan(CREATE) {
        runCatching<Unit> {
            offerRepository.insert(offer.asModel(storeId, categoryId)).awaitSingle()

            val productItemRelation =

                offer.getAllProducts()
                    .map { product ->
                        val productOfferRelationModelId =
                            ProductOfferRelationModelId(storeId.value, product.id.value, offer.id.value)
                        ProductOfferRelationModel(productOfferRelationModelId)
                    }
            productOfferRelationRepository.saveAll(productItemRelation).awaitSingle()
        }
            .mapError { DatastoreIntegrationException(it) }
            .onSuccess {
                logger.success(
                    CREATE,
                    STORE_ID to storeId,
                    CATEGORY_ID to categoryId,
                    OFFER to offer,
                    RESULT to it
                )
            }
            .onFailure {
                logger.failure(
                    CREATE,
                    it,
                    STORE_ID to storeId,
                    CATEGORY_ID to categoryId,
                    OFFER to offer,
                )
            }
    }

    override suspend fun delete(
        storeId: Id,
        offerId: Id
    ): Result<Unit, Throwable> = suspendSpan(DELETE) {
        runCatching<Unit> {
            offerRepository.deleteByOfferModelId_StoreIdAndOfferModelId_OfferId(
                storeId.value,
                offerId.value
            ).awaitSingle()
        }
            .mapError { DatastoreIntegrationException(it) }
            .onSuccess {
                logger.success(
                    DELETE,
                    STORE_ID to storeId,
                    OFFER_ID to offerId,
                )
            }
            .onFailure {
                logger.failure(
                    DELETE,
                    it,
                    STORE_ID to storeId,
                    OFFER_ID to offerId,
                )
            }
    }

    override suspend fun exists(
        storeId: Id,
        offerId: Id
    ): Result<Boolean, Throwable> = suspendSpan(EXISTS) {
        runCatching {
            offerRepository.existsByOfferModelId_StoreIdAndOfferModelId_OfferId(
                storeId.value,
                offerId.value
            ).awaitSingle()
        }
            .mapError { DatastoreIntegrationException(it) }
            .onSuccess {
                logger.success(
                    EXISTS,
                    STORE_ID to storeId,
                    OFFER_ID to offerId,
                    RESULT to it
                )
            }
            .onFailure {
                logger.failure(
                    EXISTS,
                    it,
                    STORE_ID to storeId,
                    OFFER_ID to offerId,
                )
            }
    }

    override suspend fun findById(
        storeId: Id,
        offerId: Id
    ): Result<Offer?, Throwable> = suspendSpan(FIND_BY_ID) {
        runCatching {
            val offerModel = offerRepository.findByOfferModelId_StoreIdAndOfferModelId_OfferId(
                storeId.value,
                offerId.value
            ).awaitSingleOrNull()

            offerModel?.let {
                val productModelIds =
                    offerModel.getProductIds().map { productId -> ProductModelId(productId, storeId.value) }

                val products = productRepository
                    .findAllByProductModelIdIn(productModelIds)
                    .collectList().awaitSingle()

                val productsById = products.associateBy { productModel -> productModel.productModelId.productId }

                offerModel.asEntity(productsById)
            }
        }
            .mapError { DatastoreIntegrationException(it) }
            .onSuccess {
                logger.success(
                    FIND_BY_ID,
                    STORE_ID to storeId,
                    OFFER_ID to offerId,
                    OFFER to it
                )
            }
            .onFailure {
                logger.failure(
                    FIND_BY_ID,
                    it,
                    STORE_ID to storeId,
                    OFFER_ID to offerId
                )
            }
    }

    override suspend fun getOffers(
        storeId: Id,
        categoryId: Id,
        limit: Int,
        cursor: String?
    ): Result<List<Offer>, Throwable> = suspendSpan(GET_OFFERS) {
        runCatching {
            val pageNumber = cursor?.let { CursorUtils.decode(cursor) } ?: 0
            val sort = Sort.by(Sort.Direction.ASC, OfferModel::createdAt.name)
            val pageRequest = PageRequest.of(pageNumber, limit, sort)

            val offers = offerRepository.findAllByOfferModelId_StoreIdAndOfferModelId_CategoryId(
                storeId.value,
                categoryId.value,
                pageRequest
            ).collectList().awaitSingle()

            val productIds = offers.flatMap { offerModel -> offerModel.getProductIds() }

            val productModelIds = productIds.map { productId -> ProductModelId(productId, storeId.value) }

            val products = productRepository.findAllByProductModelIdIn(productModelIds).collectList().awaitSingle()

            val productsById = products.associateBy { productModel -> productModel.productModelId.productId }

            offers.map { offerModel -> offerModel.asEntity(productsById) }.toList()
        }
            .mapError { DatastoreIntegrationException(it) }
            .onSuccess {
                logger.success(
                    GET_OFFERS,
                    STORE_ID to storeId,
                    CATEGORY_ID to categoryId,
                    LIMIT to limit,
                    CURSOR to cursor,
                    RESULT to it
                )
            }
            .onFailure {
                logger.failure(
                    GET_OFFERS,
                    it,
                    STORE_ID to storeId,
                    CATEGORY_ID to categoryId,
                    LIMIT to limit,
                    CURSOR to cursor
                )
            }
    }

    override suspend fun update(
        storeId: Id,
        categoryId: Id,
        offer: Offer
    ): Result<Unit, Throwable> = suspendSpan(UPDATE) {
        runCatching<Unit> {
            offerRepository.save(offer.asModel(storeId, categoryId)).awaitSingle()
            val productItemRelation =
                offer.getAllProducts()
                    .map { product ->
                        val productOfferRelationModelId =
                            ProductOfferRelationModelId(storeId.value, product.id.value, offer.id.value)
                        ProductOfferRelationModel(productOfferRelationModelId)
                    }
            productOfferRelationRepository.saveAll(productItemRelation).awaitLast()
        }
            .mapError { DatastoreIntegrationException(it) }
            .onSuccess {
                logger.success(
                    UPDATE,
                    STORE_ID to storeId,
                    CATEGORY_ID to categoryId,
                    OFFER to offer,
                    RESULT to it
                )
            }
            .onFailure {
                logger.failure(
                    UPDATE,
                    it,
                    STORE_ID to storeId,
                    CATEGORY_ID to categoryId,
                    OFFER to offer
                )
            }
    }
}
