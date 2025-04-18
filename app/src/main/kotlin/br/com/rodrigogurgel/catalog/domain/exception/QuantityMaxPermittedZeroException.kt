package br.com.rodrigogurgel.catalog.domain.exception

data class QuantityMaxPermittedZeroException(private val maxPermitted: Int) :
    IllegalArgumentException(
        "The maximum permitted value must be greater than zero. Please provide a valid positive value.",
    )
