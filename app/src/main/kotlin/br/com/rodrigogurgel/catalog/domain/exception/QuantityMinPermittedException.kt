package br.com.rodrigogurgel.catalog.domain.exception

data class QuantityMinPermittedException(private val minPermitted: Int) :
    IllegalArgumentException(
        "The minimum permitted value must be at least zero. Please provide a valid non-negative value.",
    )
