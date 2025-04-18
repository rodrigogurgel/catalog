package br.com.rodrigogurgel.catalog.domain.exception

import br.com.rodrigogurgel.catalog.domain.vo.Id

class DuplicatedCustomizationException(duplicatedCustomizationIds: List<Id>) :
    IllegalStateException(
        "Each customization can only be used once. Please remove duplicate entries. \n" +
            "Duplicated Customization IDs: [$duplicatedCustomizationIds].",
    )
