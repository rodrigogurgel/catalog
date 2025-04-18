package br.com.rodrigogurgel.catalog.domain.exception

import br.com.rodrigogurgel.catalog.domain.vo.Id

data class OptionAlreadyExistsException(private val optionId: Id) :
    IllegalArgumentException(
        "Option with ID '$optionId' already exists. Consider using a different ID or updating the existing option."
    )
