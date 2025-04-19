package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.repository

import br.com.rodrigogurgel.catalog.domain.entity.Offer
import br.com.rodrigogurgel.catalog.domain.vo.Id
import com.github.michaelbull.result.Result

interface OfferRepository {
    suspend fun countOffers(storeId: Id, categoryId: Id): com.github.michaelbull.result.Result<Long, Throwable>
    suspend fun create(storeId: Id, categoryId: Id, offer: Offer): com.github.michaelbull.result.Result<Unit, Throwable>
    suspend fun delete(storeId: Id, offerId: Id): com.github.michaelbull.result.Result<Unit, Throwable>
    suspend fun exists(offerId: Id): com.github.michaelbull.result.Result<Boolean, Throwable>
    suspend fun exists(storeId: Id, offerId: Id): com.github.michaelbull.result.Result<Boolean, Throwable>
    suspend fun findById(storeId: Id, offerId: Id): com.github.michaelbull.result.Result<Offer?, Throwable>
    suspend fun getOffers(
        storeId: Id,
        categoryId: Id,
        limit: Int,
        offset: Int
    ): com.github.michaelbull.result.Result<List<Offer>, Throwable>
    suspend fun search(
        storeId: Id,
        term: String,
        offset: Int,
        limit: Int
    ): com.github.michaelbull.result.Result<Pair<List<Offer>, Long>, Throwable>
    suspend fun update(storeId: Id, offer: Offer): Result<Unit, Throwable>
}
