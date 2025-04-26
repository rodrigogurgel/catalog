package br.com.rodrigogurgel.catalog.application.port.output.datastore

import br.com.rodrigogurgel.catalog.domain.entity.Offer
import br.com.rodrigogurgel.catalog.domain.vo.Id
import com.github.michaelbull.result.Result

interface OfferDatastoreOutputPort {
    suspend fun countOffers(storeId: Id, categoryId: Id): Result<Int, Throwable>
    suspend fun create(storeId: Id, categoryId: Id, offer: Offer): Result<Unit, Throwable>
    suspend fun delete(storeId: Id, offerId: Id): Result<Unit, Throwable>
    suspend fun exists(storeId: Id, offerId: Id): Result<Boolean, Throwable>
    suspend fun findById(storeId: Id, offerId: Id): Result<Offer?, Throwable>
    suspend fun getOffers(
        storeId: Id,
        categoryId: Id,
        limit: Int,
        cursor: String?
    ): Result<List<Offer>, Throwable>

    suspend fun update(storeId: Id, categoryId: Id, offer: Offer): Result<Unit, Throwable>
}
