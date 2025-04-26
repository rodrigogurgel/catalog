package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.repository

import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.OfferModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.OfferModelId
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@Suppress("FunctionNaming")
interface OfferRepository : ReactiveMongoRepository<OfferModel, OfferModelId> {
    fun countByOfferModelId_StoreIdAndOfferModelId_CategoryId(storeId: UUID, categoryId: UUID): Mono<Int>
    fun existsByOfferModelId_StoreIdAndOfferModelId_OfferId(storeId: UUID, offerId: UUID): Mono<Boolean>
    fun findByOfferModelId_StoreIdAndOfferModelId_OfferId(storeId: UUID, offerId: UUID): Mono<OfferModel>
    fun findAllByOfferModelId_StoreIdAndOfferModelId_CategoryId(
        storeId: UUID,
        categoryId: UUID,
        pageable: Pageable
    ): Flux<OfferModel>

    fun deleteByOfferModelId_StoreIdAndOfferModelId_OfferId(storeId: UUID, offerId: UUID): Mono<Unit>
}
