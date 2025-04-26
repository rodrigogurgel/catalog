package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.repository

import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.CategoryModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.CategoryModelId
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@Suppress("FunctionNaming")
interface CategoryRepository : ReactiveMongoRepository<CategoryModel, CategoryModelId> {
    fun countByCategoryModelId_StoreId(storeId: UUID): Mono<Int>
    fun existsByCategoryModelId_StoreIdAndCategoryModelId_CategoryId(storeId: UUID, categoryId: UUID): Mono<Boolean>
    fun deleteByCategoryModelId_StoreIdAndCategoryModelId_CategoryId(
        storeId: UUID,
        categoryId: UUID
    ): Mono<Unit>

    fun findByCategoryModelId_StoreIdAndCategoryModelId_CategoryId(storeId: UUID, categoryId: UUID): Mono<CategoryModel>
    fun findAllByCategoryModelId_StoreId(
        storeId: UUID,
        pageable: Pageable
    ): Flux<CategoryModel>
}
