package br.com.rodrigogurgel.catalog.domain.usecase.category

import br.com.rodrigogurgel.catalog.domain.entity.Category
import br.com.rodrigogurgel.catalog.domain.usecase.UseCase
import br.com.rodrigogurgel.catalog.domain.vo.Id
import com.github.michaelbull.result.Result

interface GetCategoriesUseCase : UseCase {
    suspend fun execute(
        storeId: Id,
        limit: Int,
        cursor: String?,
    ): Result<Pair<String?, List<Category>>, Throwable>
}
