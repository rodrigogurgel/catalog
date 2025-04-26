package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.mapper

import br.com.rodrigogurgel.catalog.domain.entity.Offer
import br.com.rodrigogurgel.catalog.domain.vo.Description
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.domain.vo.Name
import br.com.rodrigogurgel.catalog.domain.vo.Price
import br.com.rodrigogurgel.catalog.domain.vo.Status
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.mapper.CustomizationMapper.asEntity
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.mapper.CustomizationMapper.asModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.mapper.MediaMapper.asEntity
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.mapper.MediaMapper.asModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.mapper.ProductMapper.asEntity
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.OfferModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.OfferModelId
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.ProductModel
import java.time.Instant
import java.util.UUID

object OfferMapper {
    fun Offer.asModel(storeId: Id, categoryId: Id): OfferModel = OfferModel(
        offerModelId = OfferModelId(
            offerId = id.value,
            categoryId = categoryId.value,
            storeId = storeId.value,
        ),
        productId = product?.id?.value,
        name = name.value,
        description = description?.value,
        price = price.value,
        status = status.name,
        customizations = customizations.map { customization -> customization.asModel() },
        medias = medias.map { media -> media.asModel() },
        createdAt = Instant.now(),
        updatedAt = Instant.now(),
    )

    fun OfferModel.asEntity(productsById: Map<UUID, ProductModel>): Offer = Offer(
        id = Id(offerModelId.offerId),
        name = Name(name),
        description = description?.let { Description(it) },
        product = productId?.let { productsById[it]!!.asEntity() },
        price = Price(price!!),
        status = Status.valueOf(status),
        customizations = customizations.map { customizationModel -> customizationModel.asEntity(productsById) }
            .toMutableList(),
        medias = medias.map { mediasModel -> mediasModel.asEntity() }.orEmpty()
    )
}
