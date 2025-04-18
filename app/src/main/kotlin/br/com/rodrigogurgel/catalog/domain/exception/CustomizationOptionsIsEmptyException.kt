package br.com.rodrigogurgel.catalog.domain.exception

import br.com.rodrigogurgel.catalog.domain.vo.Id

data class CustomizationOptionsIsEmptyException(private val customizationId: Id) :
    IllegalStateException(
        "Customization with ID '$customizationId' has no available options. Please add options before proceeding."
    )
