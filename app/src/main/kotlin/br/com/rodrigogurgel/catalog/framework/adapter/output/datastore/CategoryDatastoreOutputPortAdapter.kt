package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore

import br.com.rodrigogurgel.catalog.application.port.output.datastore.CategoryDatastoreOutputPort
import br.com.rodrigogurgel.catalog.domain.entity.Category
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.mapper.asEntity
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.mapper.asModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.repository.CategoryRepository
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import org.springframework.stereotype.Component

@Component
class CategoryDatastoreOutputPortAdapter(
    private val categoryRepository: CategoryRepository
) : CategoryDatastoreOutputPort {
    override suspend fun countCategories(
        storeId: Id
    ): Result<Long, Throwable> = categoryRepository.countCategories(storeId.value)

    override suspend fun create(
        storeId: Id,
        category: Category
    ): Result<Unit, Throwable> = categoryRepository.create(category.asModel(storeId))

    override suspend fun delete(
        storeId: Id,
        categoryId: Id
    ): Result<Unit, Throwable> = categoryRepository.delete(storeId.value, categoryId.value)

    override suspend fun exists(
        storeId: Id,
        categoryId: Id
    ): Result<Boolean, Throwable> = categoryRepository.exists(storeId.value, categoryId.value)

    override suspend fun findById(
        storeId: Id,
        categoryId: Id
    ): Result<Category?, Throwable> = categoryRepository.findById(storeId.value, categoryId.value)
        .map { it?.asEntity() }

    override suspend fun getCategories(
        storeId: Id,
        limit: Int,
        cursor: String?
    ): Result<Pair<String?, List<Category>>, Throwable> = categoryRepository.getCategories(storeId.value, limit, cursor)
        .map { nextCursorToCategories ->
            val nextCursor = nextCursorToCategories.first
            val categories = nextCursorToCategories.second.map { categoryModel -> categoryModel.asEntity() }

            nextCursor to categories
        }

    override suspend fun update(
        storeId: Id,
        category: Category
    ): Result<Unit, Throwable> = categoryRepository.update(category.asModel(storeId))
}
