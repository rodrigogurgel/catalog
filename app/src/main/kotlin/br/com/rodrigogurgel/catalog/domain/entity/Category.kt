package br.com.rodrigogurgel.catalog.domain.entity

import br.com.rodrigogurgel.catalog.domain.vo.Description
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.domain.vo.Name
import br.com.rodrigogurgel.catalog.domain.vo.Status

data class Category(
    val id: Id,
    var name: Name,
    var description: Description?,
    var status: Status,
)
