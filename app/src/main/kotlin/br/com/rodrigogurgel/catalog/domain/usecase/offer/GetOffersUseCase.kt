package br.com.rodrigogurgel.catalog.domain.usecase.offer

import br.com.rodrigogurgel.catalog.domain.entity.Offer
import br.com.rodrigogurgel.catalog.domain.usecase.UseCase
import br.com.rodrigogurgel.catalog.domain.vo.Id
import com.github.michaelbull.result.Result

interface GetOffersUseCase : UseCase {
    suspend fun execute(
        storeId: Id,
        categoryId: Id,
        limit: Int,
        offset: Int,
    ): Result<List<Offer>, Throwable>
}
