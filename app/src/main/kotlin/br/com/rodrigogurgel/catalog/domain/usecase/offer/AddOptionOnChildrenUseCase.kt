package br.com.rodrigogurgel.catalog.domain.usecase.offer

import br.com.rodrigogurgel.catalog.domain.entity.Option
import br.com.rodrigogurgel.catalog.domain.usecase.UseCase
import br.com.rodrigogurgel.catalog.domain.vo.Id
import com.github.michaelbull.result.Result

interface AddOptionOnChildrenUseCase : UseCase {
    suspend fun execute(
        storeId: Id,
        offerId: Id,
        customizationId: Id,
        option: Option,
    ): Result<Unit, Throwable>
}
