package br.com.rodrigogurgel.catalog.domain.usecase.offer

import br.com.rodrigogurgel.catalog.domain.entity.Customization
import br.com.rodrigogurgel.catalog.domain.usecase.UseCase
import br.com.rodrigogurgel.catalog.domain.vo.Id
import com.github.michaelbull.result.Result

interface AddCustomizationOnChildrenUseCase : UseCase {
    suspend fun execute(
        storeId: Id,
        offerId: Id,
        optionId: Id,
        customization: Customization,
    ): Result<Unit, Throwable>
}
