package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.mapper

import br.com.rodrigogurgel.catalog.domain.entity.Option
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
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.mapper.QuantityMapper.asEntity
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.mapper.QuantityMapper.asModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.OptionModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.ProductModel
import java.time.Instant
import java.util.UUID

object OptionMapper {
    fun Option.asModel(): OptionModel = OptionModel(
        optionId = id.value,
        productId = product?.id?.value,
        name = name.value,
        description = description?.value,
        quantity = quantity.asModel(),
        status = status.name,
        price = price.value,
        customizations = customizations.map { customization -> customization.asModel() },
        medias = medias.map { medias -> medias.asModel() },
        createdAt = Instant.now(),
        updatedAt = Instant.now(),
    )

    fun OptionModel.asEntity(productsById: Map<UUID, ProductModel>): Option = Option(
        id = Id(optionId),
        name = Name(name),
        description = description?.let { Description(it) },
        product = productsById[productId]?.asEntity(),
        quantity = quantity.asEntity(),
        status = Status.valueOf(status),
        price = Price(price),
        customizations = customizations.map { customizationModel -> customizationModel.asEntity(productsById) }
            .toMutableList(),
        medias = medias.map { mediaModel -> mediaModel.asEntity() }
    )
}
