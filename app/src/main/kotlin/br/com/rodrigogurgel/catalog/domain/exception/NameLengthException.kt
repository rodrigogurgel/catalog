package br.com.rodrigogurgel.catalog.domain.exception

import br.com.rodrigogurgel.catalog.domain.vo.Name

data class NameLengthException(private val name: String) :
    IllegalArgumentException(
        "The name must be between ${Name.MIN_LENGTH} and ${Name.MAX_LENGTH} characters. \n" +
            "Please adjust the name length accordingly.",
    )
