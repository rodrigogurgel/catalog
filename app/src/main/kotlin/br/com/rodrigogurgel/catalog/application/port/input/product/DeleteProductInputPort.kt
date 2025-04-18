package br.com.rodrigogurgel.catalog.application.port.input.product

import br.com.rodrigogurgel.catalog.application.exception.ProductIsInUseException
import br.com.rodrigogurgel.catalog.application.exception.StoreNotFoundException
import br.com.rodrigogurgel.catalog.application.port.output.datastore.ProductDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.StoreDatastoreOutputPort
import br.com.rodrigogurgel.catalog.common.logger.extensions.PRODUCT_ID
import br.com.rodrigogurgel.catalog.common.logger.extensions.STORE_ID
import br.com.rodrigogurgel.catalog.common.logger.extensions.failure
import br.com.rodrigogurgel.catalog.common.logger.extensions.success
import br.com.rodrigogurgel.catalog.common.opentelemetry.extensions.coroutines.suspendSpan
import br.com.rodrigogurgel.catalog.domain.usecase.product.DeleteProductUseCase
import br.com.rodrigogurgel.catalog.domain.vo.Id
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.github.michaelbull.result.toErrorIf
import org.slf4j.LoggerFactory

class DeleteProductInputPort(
    private val storeDatastoreOutputPort: StoreDatastoreOutputPort,
    private val productDatastoreOutputPort: ProductDatastoreOutputPort,
) : DeleteProductUseCase {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun execute(
        storeId: Id,
        productId: Id,
    ) = suspendSpan(action()) {
        storeDatastoreOutputPort.exists(storeId)
            .toErrorIf({ !it }) { StoreNotFoundException(storeId) }
            .andThen { productDatastoreOutputPort.productIsInUse(productId) }
            .toErrorIf({ it }) { ProductIsInUseException(productId) }
            .andThen { productDatastoreOutputPort.delete(storeId, productId) }
            .onSuccess { logger.success(action(), STORE_ID to storeId, PRODUCT_ID to productId) }
            .onFailure { logger.failure(action(), it, STORE_ID to storeId, PRODUCT_ID to productId) }
    }

    override fun action() = "delete-product"
}
