package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.media

import br.com.rodrigogurgel.catalog.domain.vo.Media
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.common.MediaDTO

object MediaRequestMapper {
    fun MediaDTO.asEntity(): Media = Media(url, type)
}
