package br.com.rodrigogurgel.catalog.domain.exception

import br.com.rodrigogurgel.catalog.domain.vo.Id

data class CustomizationMinPermittedException(private val customizationId: Id) :
    IllegalArgumentException(
        "Customization with ID '$customizationId' has a minimum permitted value greater than the number of available" +
            " options. Please adjust the configuration or add more available options.",
    )
