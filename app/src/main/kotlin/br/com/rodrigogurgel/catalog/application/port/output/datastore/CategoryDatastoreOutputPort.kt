package br.com.rodrigogurgel.catalog.application.port.output.datastore

import br.com.rodrigogurgel.catalog.domain.entity.Category
import br.com.rodrigogurgel.catalog.domain.vo.Id
import com.github.michaelbull.result.Result

interface CategoryDatastoreOutputPort {
    suspend fun countCategories(storeId: Id): Result<Long, Throwable>
    suspend fun create(storeId: Id, category: Category): Result<Unit, Throwable>
    suspend fun delete(storeId: Id, categoryId: Id): Result<Unit, Throwable>
    suspend fun exists(storeId: Id, categoryId: Id): Result<Boolean, Throwable>
    suspend fun findById(storeId: Id, categoryId: Id): Result<Category?, Throwable>
    suspend fun getCategories(
        storeId: Id,
        limit: Int,
        cursor: String?
    ): Result<Pair<String?, List<Category>>, Throwable>

    suspend fun update(storeId: Id, category: Category): Result<Unit, Throwable>
}
