package br.com.rodrigogurgel.catalog.domain.usecase.offer

import br.com.rodrigogurgel.catalog.domain.usecase.UseCase
import br.com.rodrigogurgel.catalog.domain.vo.Id
import com.github.michaelbull.result.Result

interface CountOffersUseCase : UseCase {
    suspend fun execute(
        storeId: Id,
        categoryId: Id,
    ): Result<Long, Throwable>
}
