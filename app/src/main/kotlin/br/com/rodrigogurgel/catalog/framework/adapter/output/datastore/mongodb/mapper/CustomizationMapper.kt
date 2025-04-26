package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.mapper

import br.com.rodrigogurgel.catalog.domain.entity.Customization
import br.com.rodrigogurgel.catalog.domain.vo.Description
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.domain.vo.Name
import br.com.rodrigogurgel.catalog.domain.vo.Status
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.mapper.OptionMapper.asEntity
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.mapper.OptionMapper.asModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.mapper.QuantityMapper.asEntity
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.mapper.QuantityMapper.asModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.CustomizationModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.ProductModel
import java.time.Instant
import java.util.UUID

object CustomizationMapper {
    fun Customization.asModel(): CustomizationModel = CustomizationModel(
        customizationId = id.value,
        name = name.value,
        description = description?.value,
        quantity = quantity.asModel(),
        status = status.name,
        options = options.map { option -> option.asModel() },
        createdAt = Instant.now(),
        updatedAt = Instant.now()
    )

    fun CustomizationModel.asEntity(productsById: Map<UUID, ProductModel>): Customization = Customization(
        id = Id(customizationId),
        name = Name(name),
        description = description?.let { Description(it) },
        quantity = quantity.asEntity(),
        status = Status.valueOf(status),
        options = options.map { optionModel -> optionModel.asEntity(productsById) }.toMutableList()
    )
}
