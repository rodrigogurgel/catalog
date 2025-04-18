package br.com.rodrigogurgel.catalog.domain.entity

import br.com.rodrigogurgel.catalog.domain.vo.Description
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.domain.vo.Media
import br.com.rodrigogurgel.catalog.domain.vo.Name

data class Product(
    val id: Id,
    var name: Name,
    var description: Description?,
    var medias: List<Media>,
)
