package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.repository

import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.model.CategoryModel
import com.github.michaelbull.result.Result
import java.util.UUID

interface CategoryRepository {
    suspend fun countCategories(storeId: UUID): Result<Long, Throwable>
    suspend fun create(category: CategoryModel): Result<Unit, Throwable>
    suspend fun delete(storeId: UUID, categoryId: UUID): Result<Unit, Throwable>
    suspend fun exists(storeId: UUID, categoryId: UUID): Result<Boolean, Throwable>
    suspend fun findById(storeId: UUID, categoryId: UUID): Result<CategoryModel?, Throwable>
    suspend fun getCategories(
        storeId: UUID,
        limit: Int,
        cursor: String?
    ): Result<Pair<String?, List<CategoryModel>>, Throwable>
    suspend fun update(category: CategoryModel): Result<Unit, Throwable>
}
