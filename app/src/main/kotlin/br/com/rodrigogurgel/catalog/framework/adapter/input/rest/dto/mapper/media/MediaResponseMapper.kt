package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.media

import br.com.rodrigogurgel.catalog.domain.vo.Media
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.common.MediaDTO

object MediaResponseMapper {
    fun Media.asResponse(): MediaDTO = MediaDTO(url, type)
}
