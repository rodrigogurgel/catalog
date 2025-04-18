package br.com.rodrigogurgel.catalog.application.port.output.datastore

import br.com.rodrigogurgel.catalog.domain.entity.Offer
import br.com.rodrigogurgel.catalog.domain.vo.Id
import com.github.michaelbull.result.Result

interface OfferDatastoreOutputPort {
    suspend fun countOffers(storeId: Id, categoryId: Id): Result<Long, Throwable>
    suspend fun create(storeId: Id, categoryId: Id, offer: Offer): Result<Unit, Throwable>
    suspend fun delete(storeId: Id, offerId: Id): Result<Unit, Throwable>
    suspend fun exists(offerId: Id): Result<Boolean, Throwable>
    suspend fun exists(storeId: Id, offerId: Id): Result<Boolean, Throwable>
    suspend fun findById(storeId: Id, offerId: Id): Result<Offer?, Throwable>
    suspend fun getOffers(storeId: Id, categoryId: Id, limit: Int, offset: Int): Result<List<Offer>, Throwable>
    suspend fun search(storeId: Id, term: String, offset: Int, limit: Int): Result<Pair<List<Offer>, Long>, Throwable>
    suspend fun update(storeId: Id, offer: Offer): Result<Unit, Throwable>
}
