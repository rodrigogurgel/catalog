package br.com.rodrigogurgel.catalog.domain.usecase.offer

import br.com.rodrigogurgel.catalog.domain.entity.Offer
import br.com.rodrigogurgel.catalog.domain.usecase.UseCase
import br.com.rodrigogurgel.catalog.domain.vo.Id
import com.github.michaelbull.result.Result

interface CreateOfferUseCase : UseCase {
    suspend fun execute(
        storeId: Id,
        categoryId: Id,
        offer: Offer,
    ): Result<Unit, Throwable>
}
