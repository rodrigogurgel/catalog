package br.com.rodrigogurgel.catalog.domain.exception

import br.com.rodrigogurgel.catalog.domain.vo.Description

data class DescriptionLengthException(private val description: String) :
    IllegalArgumentException(
        "The description must be between ${Description.MIN_LENGTH} and ${Description.MAX_LENGTH} characters. \n" +
            "Please adjust the description length accordingly.",
    )
