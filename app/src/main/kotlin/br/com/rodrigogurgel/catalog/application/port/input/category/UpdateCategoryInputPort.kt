package br.com.rodrigogurgel.catalog.application.port.input.category

import br.com.rodrigogurgel.catalog.application.exception.CategoryNotFoundException
import br.com.rodrigogurgel.catalog.application.exception.StoreNotFoundException
import br.com.rodrigogurgel.catalog.application.port.output.datastore.CategoryDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.StoreDatastoreOutputPort
import br.com.rodrigogurgel.catalog.common.logger.extensions.CATEGORY
import br.com.rodrigogurgel.catalog.common.logger.extensions.STORE_ID
import br.com.rodrigogurgel.catalog.common.logger.extensions.failure
import br.com.rodrigogurgel.catalog.common.logger.extensions.success
import br.com.rodrigogurgel.catalog.common.opentelemetry.extensions.coroutines.suspendSpan
import br.com.rodrigogurgel.catalog.domain.entity.Category
import br.com.rodrigogurgel.catalog.domain.usecase.category.UpdateCategoryUseCase
import br.com.rodrigogurgel.catalog.domain.vo.Id
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.github.michaelbull.result.toErrorIf
import org.slf4j.LoggerFactory

class UpdateCategoryInputPort(
    private val storeDatastoreOutputPort: StoreDatastoreOutputPort,
    private val categoryDatastoreOutputPort: CategoryDatastoreOutputPort,
) : UpdateCategoryUseCase {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun execute(
        storeId: Id,
        category: Category,
    ) = suspendSpan(action()) {
        storeDatastoreOutputPort.exists(storeId)
            .toErrorIf({ !it }) { StoreNotFoundException(storeId) }
            .andThen { categoryDatastoreOutputPort.exists(storeId, category.id) }
            .toErrorIf({ !it }) { CategoryNotFoundException(storeId, category.id) }
            .andThen { categoryDatastoreOutputPort.update(storeId, category) }
            .onSuccess { logger.success(action(), STORE_ID to storeId, CATEGORY to category) }
            .onFailure { logger.failure(action(), it, STORE_ID to storeId, CATEGORY to category) }
    }

    override fun action() = "update-category"
}
