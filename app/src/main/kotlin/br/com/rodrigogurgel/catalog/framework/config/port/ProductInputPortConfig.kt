package br.com.rodrigogurgel.catalog.framework.config.port

import br.com.rodrigogurgel.catalog.application.port.input.product.CountProductsInputPort
import br.com.rodrigogurgel.catalog.application.port.input.product.CreateProductInputPort
import br.com.rodrigogurgel.catalog.application.port.input.product.DeleteProductInputPort
import br.com.rodrigogurgel.catalog.application.port.input.product.GetProductInputPort
import br.com.rodrigogurgel.catalog.application.port.input.product.GetProductsInputPort
import br.com.rodrigogurgel.catalog.application.port.input.product.UpdateProductInputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.ProductDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.StoreDatastoreOutputPort
import br.com.rodrigogurgel.catalog.domain.usecase.product.CountProductsUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.product.DeleteProductUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.product.GetProductUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.product.GetProductsUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ProductInputPortConfig {
    @Bean
    fun createProductInputPort(
        storeDatastoreOutputPort: StoreDatastoreOutputPort,
        productDatastoreOutputPort: ProductDatastoreOutputPort,
    ): CreateProductInputPort {
        return CreateProductInputPort(storeDatastoreOutputPort, productDatastoreOutputPort)
    }

    @Bean
    fun updateProductInputPort(
        storeDatastoreOutputPort: StoreDatastoreOutputPort,
        productDatastoreOutputPort: ProductDatastoreOutputPort,
    ): UpdateProductInputPort {
        return UpdateProductInputPort(storeDatastoreOutputPort, productDatastoreOutputPort)
    }

    @Bean
    fun getProductUseCase(
        storeDatastoreOutputPort: StoreDatastoreOutputPort,
        productDatastoreOutputPort: ProductDatastoreOutputPort,
    ): GetProductUseCase {
        return GetProductInputPort(storeDatastoreOutputPort, productDatastoreOutputPort)
    }

    @Bean
    fun deleteProductUseCase(
        storeDatastoreOutputPort: StoreDatastoreOutputPort,
        productDatastoreOutputPort: ProductDatastoreOutputPort,
    ): DeleteProductUseCase {
        return DeleteProductInputPort(storeDatastoreOutputPort, productDatastoreOutputPort)
    }

    @Bean
    fun getProductsUseCase(
        storeDatastoreOutputPort: StoreDatastoreOutputPort,
        productDatastoreOutputPort: ProductDatastoreOutputPort,
    ): GetProductsUseCase {
        return GetProductsInputPort(storeDatastoreOutputPort, productDatastoreOutputPort)
    }

    @Bean
    fun countProductsUseCase(
        storeDatastoreOutputPort: StoreDatastoreOutputPort,
        productDatastoreOutputPort: ProductDatastoreOutputPort,
    ): CountProductsUseCase {
        return CountProductsInputPort(storeDatastoreOutputPort, productDatastoreOutputPort)
    }
}
