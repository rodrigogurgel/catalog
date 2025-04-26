package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.mapper

import br.com.rodrigogurgel.catalog.domain.entity.Product
import br.com.rodrigogurgel.catalog.domain.vo.Description
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.domain.vo.Name
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.mapper.MediaMapper.asEntity
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.mapper.MediaMapper.asModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.ProductModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.ProductModelId
import java.time.Instant

object ProductMapper {
    fun Product.asModel(storeId: Id): ProductModel = ProductModel(
        productModelId = ProductModelId(
            storeId = storeId.value,
            productId = id.value,
        ),
        name = name.value,
        description = description?.value,
        medias = medias.map { media -> media.asModel() },
        createdAt = Instant.now(),
        updatedAt = Instant.now(),
    )

    fun ProductModel.asEntity(): Product = Product(
        id = Id(productModelId.productId),
        name = Name(name),
        description = description?.let { Description(it) },
        medias = medias.map { mediaModel -> mediaModel.asEntity() },
    )
}
