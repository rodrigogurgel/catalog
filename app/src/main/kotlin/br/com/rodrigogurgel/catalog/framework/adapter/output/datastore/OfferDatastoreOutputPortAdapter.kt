package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore

import br.com.rodrigogurgel.catalog.application.port.output.datastore.OfferDatastoreOutputPort
import br.com.rodrigogurgel.catalog.domain.entity.Offer
import br.com.rodrigogurgel.catalog.domain.vo.Id
import com.github.michaelbull.result.Result
import org.springframework.stereotype.Component

@Component
class OfferDatastoreOutputPortAdapter : OfferDatastoreOutputPort {
    override suspend fun countOffers(
        storeId: Id,
        categoryId: Id
    ): Result<Long, Throwable> {
        TODO("Not yet implemented")
    }

    override suspend fun create(
        storeId: Id,
        categoryId: Id,
        offer: Offer
    ): Result<Unit, Throwable> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(
        storeId: Id,
        offerId: Id
    ): Result<Unit, Throwable> {
        TODO("Not yet implemented")
    }

    override suspend fun exists(offerId: Id): Result<Boolean, Throwable> {
        TODO("Not yet implemented")
    }

    override suspend fun exists(
        storeId: Id,
        offerId: Id
    ): Result<Boolean, Throwable> {
        TODO("Not yet implemented")
    }

    override suspend fun findById(
        storeId: Id,
        offerId: Id
    ): Result<Offer?, Throwable> {
        TODO("Not yet implemented")
    }

    override suspend fun getOffers(
        storeId: Id,
        categoryId: Id,
        limit: Int,
        offset: Int
    ): Result<List<Offer>, Throwable> {
        TODO("Not yet implemented")
    }

    override suspend fun search(
        storeId: Id,
        term: String,
        offset: Int,
        limit: Int
    ): Result<Pair<List<Offer>, Long>, Throwable> {
        TODO("Not yet implemented")
    }

    override suspend fun update(
        storeId: Id,
        offer: Offer
    ): Result<Unit, Throwable> {
        TODO("Not yet implemented")
    }
}
