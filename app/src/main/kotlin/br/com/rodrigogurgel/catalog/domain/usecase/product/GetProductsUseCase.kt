package br.com.rodrigogurgel.catalog.domain.usecase.product

import br.com.rodrigogurgel.catalog.domain.entity.Product
import br.com.rodrigogurgel.catalog.domain.usecase.UseCase
import br.com.rodrigogurgel.catalog.domain.vo.Id
import com.github.michaelbull.result.Result

interface GetProductsUseCase : UseCase {
    suspend fun execute(
        storeId: Id,
        limit: Int,
        cursor: String?,
    ): Result<List<Product>, Throwable>
}
