package br.com.rodrigogurgel.catalog.domain.usecase.offer

import br.com.rodrigogurgel.catalog.domain.usecase.UseCase
import br.com.rodrigogurgel.catalog.domain.vo.Id
import com.github.michaelbull.result.Result

interface RemoveOptionOnChildrenUseCase : UseCase {
    suspend fun execute(
        storeId: Id,
        offerId: Id,
        customizationId: Id,
        optionId: Id,
    ): Result<Unit, Throwable>
}
