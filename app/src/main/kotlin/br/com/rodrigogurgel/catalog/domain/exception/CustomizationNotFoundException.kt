package br.com.rodrigogurgel.catalog.domain.exception

import br.com.rodrigogurgel.catalog.domain.vo.Id

data class CustomizationNotFoundException(private val customizationId: Id) :
    IllegalStateException(
        "Customization with ID '$customizationId' not found. Please verify the provided ID and try again."
    )
