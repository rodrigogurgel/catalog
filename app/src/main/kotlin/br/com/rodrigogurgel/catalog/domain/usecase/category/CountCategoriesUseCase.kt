package br.com.rodrigogurgel.catalog.domain.usecase.category

import br.com.rodrigogurgel.catalog.domain.usecase.UseCase
import br.com.rodrigogurgel.catalog.domain.vo.Id
import com.github.michaelbull.result.Result

interface CountCategoriesUseCase : UseCase {
    suspend fun execute(storeId: Id): Result<Long, Throwable>
}
