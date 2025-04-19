package br.com.rodrigogurgel.catalog.framework.config.port

import br.com.rodrigogurgel.catalog.application.port.input.category.CountCategoriesInputPort
import br.com.rodrigogurgel.catalog.application.port.input.category.CreateCategoryInputPort
import br.com.rodrigogurgel.catalog.application.port.input.category.DeleteCategoryInputPort
import br.com.rodrigogurgel.catalog.application.port.input.category.GetCategoriesInputPort
import br.com.rodrigogurgel.catalog.application.port.input.category.GetCategoryInputPort
import br.com.rodrigogurgel.catalog.application.port.input.category.UpdateCategoryInputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.CategoryDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.StoreDatastoreOutputPort
import br.com.rodrigogurgel.catalog.domain.usecase.category.CountCategoriesUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.category.CreateCategoryUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.category.DeleteCategoryUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.category.GetCategoriesUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.category.GetCategoryUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.category.UpdateCategoryUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CategoryInputPortConfig {
    @Bean
    fun getCategoryUseCase(
        storeDatastoreOutputPort: StoreDatastoreOutputPort,
        categoryDatastoreOutputPort: CategoryDatastoreOutputPort,
    ): GetCategoryUseCase {
        return GetCategoryInputPort(
            storeDatastoreOutputPort,
            categoryDatastoreOutputPort
        )
    }

    @Bean
    fun getCategoriesUseCase(
        storeDatastoreOutputPort: StoreDatastoreOutputPort,
        categoryDatastoreOutputPort: CategoryDatastoreOutputPort,
    ): GetCategoriesUseCase {
        return GetCategoriesInputPort(
            storeDatastoreOutputPort,
            categoryDatastoreOutputPort
        )
    }

    @Bean
    fun createCategoryUseCase(
        storeDatastoreOutputPort: StoreDatastoreOutputPort,
        categoryDatastoreOutputPort: CategoryDatastoreOutputPort,
    ): CreateCategoryUseCase {
        return CreateCategoryInputPort(
            storeDatastoreOutputPort,
            categoryDatastoreOutputPort
        )
    }

    @Bean
    fun updateCategoryUseCase(
        storeDatastoreOutputPort: StoreDatastoreOutputPort,
        categoryDatastoreOutputPort: CategoryDatastoreOutputPort,
    ): UpdateCategoryUseCase {
        return UpdateCategoryInputPort(
            storeDatastoreOutputPort,
            categoryDatastoreOutputPort
        )
    }

    @Bean
    fun deleteCategoryUseCase(
        storeDatastoreOutputPort: StoreDatastoreOutputPort,
        categoryDatastoreOutputPort: CategoryDatastoreOutputPort,
    ): DeleteCategoryUseCase {
        return DeleteCategoryInputPort(
            storeDatastoreOutputPort,
            categoryDatastoreOutputPort
        )
    }

    @Bean
    fun countCategoriesUseCase(
        storeDatastoreOutputPort: StoreDatastoreOutputPort,
        categoryDatastoreOutputPort: CategoryDatastoreOutputPort,
    ): CountCategoriesUseCase {
        return CountCategoriesInputPort(
            storeDatastoreOutputPort,
            categoryDatastoreOutputPort
        )
    }
}
