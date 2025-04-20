package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.mapper

import br.com.rodrigogurgel.catalog.domain.entity.Product
import br.com.rodrigogurgel.catalog.domain.vo.Description
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.domain.vo.Name
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.mapper.MediaMapper.asEntity
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.mapper.MediaMapper.asModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.model.ProductModel
import java.time.Instant

object ProductMapper {
    fun Product.asModel(storeId: Id): ProductModel = ProductModel(
        storeId = storeId.value,
        productId = id.value,
        name = name.value,
        description = description?.value,
        medias = medias.map { media -> media.asModel() },
        createdAt = Instant.now(),
        updatedAt = Instant.now(),
    )

    fun ProductModel.asEntity(): Product = Product(
        id = Id(productId!!),
        name = Name(name!!),
        description = description?.let { Description(it) },
        medias = medias?.map { mediaModel -> mediaModel.asEntity() }.orEmpty(),
    )
}
