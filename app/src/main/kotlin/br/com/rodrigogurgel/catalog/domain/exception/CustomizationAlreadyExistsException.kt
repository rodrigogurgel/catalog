package br.com.rodrigogurgel.catalog.domain.exception

import br.com.rodrigogurgel.catalog.domain.vo.Id

data class CustomizationAlreadyExistsException(private val customizationId: Id) :
    IllegalArgumentException(
        "Customization with ID '$customizationId' already exists. Consider using a different ID" +
            " or updating the existing customization."
    )
