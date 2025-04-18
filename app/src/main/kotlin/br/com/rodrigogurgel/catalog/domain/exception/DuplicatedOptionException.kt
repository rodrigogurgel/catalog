package br.com.rodrigogurgel.catalog.domain.exception

import br.com.rodrigogurgel.catalog.domain.vo.Id

class DuplicatedOptionException(duplicatedOptionIds: List<Id>) :
    IllegalStateException(
        "Each option can only be used once. Please remove duplicate entries. \n" +
            "Duplicated Option IDs: [$duplicatedOptionIds].",
    )
