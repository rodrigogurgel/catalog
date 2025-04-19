package br.com.rodrigogurgel.catalog.framework.config.port

import br.com.rodrigogurgel.catalog.application.port.input.offer.AddCustomizationInputPort
import br.com.rodrigogurgel.catalog.application.port.input.offer.AddCustomizationOnChildrenInputPort
import br.com.rodrigogurgel.catalog.application.port.input.offer.AddOptionOnChildrenInputPort
import br.com.rodrigogurgel.catalog.application.port.input.offer.CountOffersInputPort
import br.com.rodrigogurgel.catalog.application.port.input.offer.CreateOfferInputPort
import br.com.rodrigogurgel.catalog.application.port.input.offer.DeleteOfferInputPort
import br.com.rodrigogurgel.catalog.application.port.input.offer.GetOfferInputPort
import br.com.rodrigogurgel.catalog.application.port.input.offer.GetOffersInputPort
import br.com.rodrigogurgel.catalog.application.port.input.offer.RemoveCustomizationInputPort
import br.com.rodrigogurgel.catalog.application.port.input.offer.RemoveCustomizationOnChildrenInputPort
import br.com.rodrigogurgel.catalog.application.port.input.offer.RemoveOptionOnChildrenInputPort
import br.com.rodrigogurgel.catalog.application.port.input.offer.UpdateCustomizationInputPort
import br.com.rodrigogurgel.catalog.application.port.input.offer.UpdateCustomizationOnChildrenInputPort
import br.com.rodrigogurgel.catalog.application.port.input.offer.UpdateOfferInputPort
import br.com.rodrigogurgel.catalog.application.port.input.offer.UpdateOptionOnChildrenInputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.CategoryDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.OfferDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.ProductDatastoreOutputPort
import br.com.rodrigogurgel.catalog.application.port.output.datastore.StoreDatastoreOutputPort
import br.com.rodrigogurgel.catalog.domain.usecase.offer.AddCustomizationOnChildrenUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.offer.AddCustomizationUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.offer.AddOptionOnChildrenUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.offer.CountOffersUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.offer.CreateOfferUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.offer.DeleteOfferUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.offer.GetOfferUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.offer.GetOffersUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.offer.RemoveCustomizationOnChildrenUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.offer.RemoveCustomizationUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.offer.RemoveOptionOnChildrenUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.offer.UpdateCustomizationOnChildrenUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.offer.UpdateCustomizationUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.offer.UpdateOfferUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.offer.UpdateOptionOnChildrenUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OfferInputPortConfig {
    @Bean
    fun createOfferUseCase(
        storeDatastoreOutputPort: StoreDatastoreOutputPort,
        categoryDatastoreOutputPort: CategoryDatastoreOutputPort,
        productDatastoreOutputPort: ProductDatastoreOutputPort,
        offerDatastoreOutputPort: OfferDatastoreOutputPort,
    ): CreateOfferUseCase {
        return CreateOfferInputPort(
            storeDatastoreOutputPort,
            categoryDatastoreOutputPort,
            productDatastoreOutputPort,
            offerDatastoreOutputPort,
        )
    }

    @Bean
    fun getOfferUseCase(
        storeDatastoreOutputPort: StoreDatastoreOutputPort,
        offerDatastoreOutputPort: OfferDatastoreOutputPort,
    ): GetOfferUseCase {
        return GetOfferInputPort(
            storeDatastoreOutputPort,
            offerDatastoreOutputPort
        )
    }

    @Bean
    fun deleteOfferUseCase(
        storeDatastoreOutputPort: StoreDatastoreOutputPort,
        offerDatastoreOutputPort: OfferDatastoreOutputPort,
    ): DeleteOfferUseCase {
        return DeleteOfferInputPort(
            storeDatastoreOutputPort,
            offerDatastoreOutputPort
        )
    }

    @Bean
    fun updateOfferUseCase(
        storeDatastoreOutputPort: StoreDatastoreOutputPort,
        offerDatastoreOutputPort: OfferDatastoreOutputPort,
        productDatastoreOutputPort: ProductDatastoreOutputPort,
    ): UpdateOfferUseCase {
        return UpdateOfferInputPort(
            storeDatastoreOutputPort,
            productDatastoreOutputPort,
            offerDatastoreOutputPort
        )
    }

    @Bean
    fun countOffersUseCase(
        storeDatastoreOutputPort: StoreDatastoreOutputPort,
        categoryDatastoreOutputPort: CategoryDatastoreOutputPort,
        offerDatastoreOutputPort: OfferDatastoreOutputPort,
    ): CountOffersUseCase {
        return CountOffersInputPort(
            storeDatastoreOutputPort,
            categoryDatastoreOutputPort,
            offerDatastoreOutputPort
        )
    }

    @Bean
    fun getOffersUseCase(
        storeDatastoreOutputPort: StoreDatastoreOutputPort,
        categoryDatastoreOutputPort: CategoryDatastoreOutputPort,
        offerDatastoreOutputPort: OfferDatastoreOutputPort,
    ): GetOffersUseCase {
        return GetOffersInputPort(
            storeDatastoreOutputPort,
            categoryDatastoreOutputPort,
            offerDatastoreOutputPort
        )
    }

    @Bean
    fun addCustomizationUseCase(
        storeDatastoreOutputPort: StoreDatastoreOutputPort,
        offerDatastoreOutputPort: OfferDatastoreOutputPort,
        productDatastoreOutputPort: ProductDatastoreOutputPort,
    ): AddCustomizationUseCase {
        return AddCustomizationInputPort(
            storeDatastoreOutputPort,
            offerDatastoreOutputPort,
            productDatastoreOutputPort
        )
    }

    @Bean
    fun updateCustomizationUseCase(
        storeDatastoreOutputPort: StoreDatastoreOutputPort,
        offerDatastoreOutputPort: OfferDatastoreOutputPort,
        productDatastoreOutputPort: ProductDatastoreOutputPort,
    ): UpdateCustomizationUseCase {
        return UpdateCustomizationInputPort(
            storeDatastoreOutputPort,
            offerDatastoreOutputPort,
            productDatastoreOutputPort
        )
    }

    @Bean
    fun removeCustomizationUseCase(
        storeDatastoreOutputPort: StoreDatastoreOutputPort,
        offerDatastoreOutputPort: OfferDatastoreOutputPort,
    ): RemoveCustomizationUseCase {
        return RemoveCustomizationInputPort(
            storeDatastoreOutputPort,
            offerDatastoreOutputPort,
        )
    }

    @Bean
    fun addCustomizationOnChildrenUseCase(
        storeDatastoreOutputPort: StoreDatastoreOutputPort,
        offerDatastoreOutputPort: OfferDatastoreOutputPort,
        productDatastoreOutputPort: ProductDatastoreOutputPort,
    ): AddCustomizationOnChildrenUseCase {
        return AddCustomizationOnChildrenInputPort(
            storeDatastoreOutputPort,
            offerDatastoreOutputPort,
            productDatastoreOutputPort
        )
    }

    @Bean
    fun updateCustomizationOnChildrenUseCase(
        storeDatastoreOutputPort: StoreDatastoreOutputPort,
        offerDatastoreOutputPort: OfferDatastoreOutputPort,
        productDatastoreOutputPort: ProductDatastoreOutputPort,
    ): UpdateCustomizationOnChildrenUseCase {
        return UpdateCustomizationOnChildrenInputPort(
            storeDatastoreOutputPort,
            offerDatastoreOutputPort,
            productDatastoreOutputPort
        )
    }

    @Bean
    fun removeCustomizationOnChildrenUseCase(
        storeDatastoreOutputPort: StoreDatastoreOutputPort,
        offerDatastoreOutputPort: OfferDatastoreOutputPort,
    ): RemoveCustomizationOnChildrenUseCase {
        return RemoveCustomizationOnChildrenInputPort(
            storeDatastoreOutputPort,
            offerDatastoreOutputPort,
        )
    }

    @Bean
    fun addOptionOnChildrenUseCase(
        storeDatastoreOutputPort: StoreDatastoreOutputPort,
        offerDatastoreOutputPort: OfferDatastoreOutputPort,
        productDatastoreOutputPort: ProductDatastoreOutputPort,
    ): AddOptionOnChildrenUseCase {
        return AddOptionOnChildrenInputPort(
            storeDatastoreOutputPort,
            offerDatastoreOutputPort,
            productDatastoreOutputPort
        )
    }

    @Bean
    fun updateOptionOnChildrenUseCase(
        storeDatastoreOutputPort: StoreDatastoreOutputPort,
        offerDatastoreOutputPort: OfferDatastoreOutputPort,
        productDatastoreOutputPort: ProductDatastoreOutputPort,
    ): UpdateOptionOnChildrenUseCase {
        return UpdateOptionOnChildrenInputPort(
            storeDatastoreOutputPort,
            offerDatastoreOutputPort,
            productDatastoreOutputPort
        )
    }

    @Bean
    fun removeOptionOnChildrenUseCase(
        storeDatastoreOutputPort: StoreDatastoreOutputPort,
        offerDatastoreOutputPort: OfferDatastoreOutputPort,
    ): RemoveOptionOnChildrenUseCase {
        return RemoveOptionOnChildrenInputPort(
            storeDatastoreOutputPort,
            offerDatastoreOutputPort,
        )
    }
}
