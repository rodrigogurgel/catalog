package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.mapper

import br.com.rodrigogurgel.catalog.domain.vo.Media
import br.com.rodrigogurgel.catalog.domain.vo.MediaType
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.model.MediaModel

object MediaMapper {
    fun Media.asModel(): MediaModel = MediaModel(
        url = url,
        type = type.name
    )

    fun MediaModel.asEntity(): Media = Media(
        url = url!!,
        type = MediaType.valueOf(type!!)
    )
}
