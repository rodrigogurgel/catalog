package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore

import br.com.rodrigogurgel.catalog.application.port.output.datastore.CategoryDatastoreOutputPort
import br.com.rodrigogurgel.catalog.common.logger.extensions.CATEGORY
import br.com.rodrigogurgel.catalog.common.logger.extensions.CATEGORY_ID
import br.com.rodrigogurgel.catalog.common.logger.extensions.CURSOR
import br.com.rodrigogurgel.catalog.common.logger.extensions.LIMIT
import br.com.rodrigogurgel.catalog.common.logger.extensions.RESULT
import br.com.rodrigogurgel.catalog.common.logger.extensions.STORE_ID
import br.com.rodrigogurgel.catalog.common.logger.extensions.failure
import br.com.rodrigogurgel.catalog.common.logger.extensions.success
import br.com.rodrigogurgel.catalog.common.opentelemetry.extensions.coroutines.suspendSpan
import br.com.rodrigogurgel.catalog.domain.entity.Category
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.exception.DatastoreIntegrationException
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.mapper.CategoryMapper.asEntity
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.mapper.CategoryMapper.asModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.CategoryModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.repository.CategoryRepository
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.utils.CursorUtils
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapCatching
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.github.michaelbull.result.runCatching
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@Component
class CategoryDatastoreOutputPortAdapter(
    private val categoryRepository: CategoryRepository
) : CategoryDatastoreOutputPort {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)

        private const val COUNT_CATEGORIES = "category-datastore-output-port-count-categories"
        private const val CREATE = "category-datastore-output-port-create"
        private const val DELETE = "category-datastore-output-port-delete"
        private const val EXISTS = "category-datastore-output-port-exists"
        private const val FIND_BY_ID = "category-datastore-output-port-find-by-id"
        private const val GET_CATEGORIES = "category-datastore-output-port-get-categories"
        private const val UPDATE = "category-datastore-output-port-update"
    }

    override suspend fun countCategories(
        storeId: Id
    ): Result<Int, Throwable> = suspendSpan(COUNT_CATEGORIES) {
        runCatching { categoryRepository.countByCategoryModelId_StoreId(storeId.value).awaitSingle() }
    }
        .mapError { DatastoreIntegrationException(it) }
        .onSuccess {
            logger.success(
                COUNT_CATEGORIES,
                STORE_ID to storeId,
                RESULT to it
            )
        }
        .onFailure {
            logger.failure(
                COUNT_CATEGORIES,
                it,
                STORE_ID to storeId,
            )
        }

    override suspend fun create(
        storeId: Id,
        category: Category
    ): Result<Unit, Throwable> = suspendSpan(CREATE) {
        runCatching<Unit> { categoryRepository.insert(category.asModel(storeId)).awaitSingle() }
            .mapError { DatastoreIntegrationException(it) }
            .onSuccess {
                logger.success(
                    CREATE,
                    STORE_ID to storeId,
                    CATEGORY to category,
                )
            }
            .onFailure {
                logger.failure(
                    CREATE,
                    it,
                    STORE_ID to storeId,
                    CATEGORY to category,
                )
            }
    }

    override suspend fun delete(
        storeId: Id,
        categoryId: Id
    ): Result<Unit, Throwable> = suspendSpan(DELETE) {
        runCatching<Unit> {
            categoryRepository.deleteByCategoryModelId_StoreIdAndCategoryModelId_CategoryId(
                storeId.value,
                categoryId.value
            ).awaitSingle()
        }
            .mapError { DatastoreIntegrationException(it) }
            .onSuccess {
                logger.success(
                    DELETE,
                    STORE_ID to storeId,
                    CATEGORY_ID to categoryId,
                )
            }
            .onFailure {
                logger.failure(
                    DELETE,
                    it,
                    STORE_ID to storeId,
                    CATEGORY_ID to categoryId,
                )
            }
    }

    override suspend fun exists(
        storeId: Id,
        categoryId: Id
    ): Result<Boolean, Throwable> = suspendSpan(EXISTS) {
        runCatching {
            categoryRepository.existsByCategoryModelId_StoreIdAndCategoryModelId_CategoryId(
                storeId.value,
                categoryId.value
            ).awaitSingle()
        }
            .mapError { DatastoreIntegrationException(it) }
            .onSuccess {
                logger.success(
                    EXISTS,
                    STORE_ID to storeId,
                    CATEGORY_ID to categoryId,
                    RESULT to it
                )
            }
            .onFailure {
                logger.failure(
                    EXISTS,
                    it,
                    STORE_ID to storeId,
                    CATEGORY_ID to categoryId,
                )
            }
    }

    override suspend fun findById(
        storeId: Id,
        categoryId: Id
    ): Result<Category?, Throwable> = suspendSpan(FIND_BY_ID) {
        runCatching {
            categoryRepository.findByCategoryModelId_StoreIdAndCategoryModelId_CategoryId(
                storeId.value,
                categoryId.value
            ).awaitSingleOrNull()
        }
            .mapError { DatastoreIntegrationException(it) }
            .mapCatching { categoryModel -> categoryModel?.asEntity() }
            .onSuccess {
                logger.success(
                    FIND_BY_ID,
                    STORE_ID to storeId,
                    CATEGORY_ID to categoryId,
                    RESULT to it
                )
            }
            .onFailure {
                logger.failure(
                    FIND_BY_ID,
                    it,
                    STORE_ID to storeId,
                    CATEGORY_ID to categoryId,
                )
            }
    }

    override suspend fun getCategories(
        storeId: Id,
        limit: Int,
        cursor: String?
    ): Result<List<Category>, Throwable> = suspendSpan(GET_CATEGORIES) {
        runCatching {
            val pageNumber = cursor?.let { CursorUtils.decode(cursor) } ?: 0
            val sort = Sort.by(Sort.Direction.ASC, CategoryModel::createdAt.name)
            val pageRequest = PageRequest.of(pageNumber, limit, sort)

            categoryRepository.findAllByCategoryModelId_StoreId(
                storeId.value,
                pageRequest
            ).collectList().awaitSingle()
        }
            .mapError { DatastoreIntegrationException(it) }
            .mapCatching { categories ->
                categories.map { categoryModel -> categoryModel.asEntity() }
            }
            .onSuccess {
                logger.success(
                    GET_CATEGORIES,
                    STORE_ID to storeId,
                    LIMIT to limit,
                    CURSOR to cursor,
                    RESULT to it
                )
            }
            .onFailure {
                logger.failure(
                    GET_CATEGORIES,
                    it,
                    STORE_ID to storeId,
                    LIMIT to limit,
                    CURSOR to cursor
                )
            }
    }

    override suspend fun update(
        storeId: Id,
        category: Category
    ): Result<Unit, Throwable> = suspendSpan(UPDATE) {
        runCatching<Unit> { categoryRepository.save(category.asModel(storeId)).awaitSingle() }
            .mapError { DatastoreIntegrationException(it) }
            .onSuccess {
                logger.success(
                    UPDATE,
                    STORE_ID to storeId,
                    CURSOR to CURSOR
                )
            }
            .onFailure {
                logger.failure(
                    UPDATE,
                    it,
                    STORE_ID to storeId,
                    CATEGORY to category,
                )
            }
    }
}
