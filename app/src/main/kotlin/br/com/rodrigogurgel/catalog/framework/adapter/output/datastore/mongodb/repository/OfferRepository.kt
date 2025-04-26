package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.repository

import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.OfferModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.OfferModelId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.UUID

@Suppress("FunctionNaming")
interface OfferRepository : MongoRepository<OfferModel, OfferModelId> {
    fun countByOfferModelId_StoreIdAndOfferModelId_CategoryId(storeId: UUID, categoryId: UUID): Int
    fun existsByOfferModelId_StoreIdAndOfferModelId_OfferId(storeId: UUID, offerId: UUID): Boolean
    fun findByOfferModelId_StoreIdAndOfferModelId_OfferId(storeId: UUID, offerId: UUID): OfferModel?
    fun findAllByOfferModelId_StoreIdAndOfferModelId_CategoryId(
        storeId: UUID,
        categoryId: UUID,
        pageable: Pageable
    ): Page<OfferModel>

    fun deleteByOfferModelId_StoreIdAndOfferModelId_OfferId(storeId: UUID, offerId: UUID)
}
