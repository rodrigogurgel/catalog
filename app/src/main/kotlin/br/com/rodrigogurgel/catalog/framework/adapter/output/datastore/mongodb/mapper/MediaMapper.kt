package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.mapper

import br.com.rodrigogurgel.catalog.domain.vo.Media
import br.com.rodrigogurgel.catalog.domain.vo.MediaType
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.MediaModel

object MediaMapper {
    fun Media.asModel(): MediaModel = MediaModel(
        url = url,
        type = type.name
    )

    fun MediaModel.asEntity(): Media = Media(
        url = url,
        type = MediaType.valueOf(type)
    )
}
