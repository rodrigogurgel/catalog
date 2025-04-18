package br.com.rodrigogurgel.catalog.domain.exception

import br.com.rodrigogurgel.catalog.domain.vo.Id

data class OptionNotFoundException(private val optionId: Id) :
    IllegalStateException("Option with ID '$optionId' not found. Please verify the provided ID and try again.")
