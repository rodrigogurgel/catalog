package br.com.rodrigogurgel.catalog.domain.usecase.product

import br.com.rodrigogurgel.catalog.domain.usecase.UseCase
import br.com.rodrigogurgel.catalog.domain.vo.Id
import com.github.michaelbull.result.Result

interface CountProductsUseCase : UseCase {
    suspend fun execute(
        storeId: Id
    ): Result<Long, Throwable>
}
