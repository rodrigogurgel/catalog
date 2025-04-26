package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.repository

import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.CategoryModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.CategoryModelId
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.UUID

@Suppress("FunctionNaming")
interface CategoryRepository : MongoRepository<CategoryModel, CategoryModelId> {
    fun countByCategoryModelId_StoreId(storeId: UUID): Int
    fun existsByCategoryModelId_StoreIdAndCategoryModelId_CategoryId(storeId: UUID, categoryId: UUID): Boolean
    fun deleteByCategoryModelId_StoreIdAndCategoryModelId_CategoryId(
        storeId: UUID,
        categoryId: UUID
    )

    fun findByCategoryModelId_StoreIdAndCategoryModelId_CategoryId(storeId: UUID, categoryId: UUID): CategoryModel?
    fun findAllByCategoryModelId_StoreId(
        storeId: UUID,
        pageable: Pageable
    ): List<CategoryModel>
}
