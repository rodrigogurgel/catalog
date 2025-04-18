package br.com.rodrigogurgel.catalog.domain.exception

data class QuantityMaxPermittedException(private val minPermitted: Int, private val maxPermitted: Int) :
    IllegalArgumentException(
        "The maximum permitted value must be at least equal to the minimum permitted value. \n" +
            "Please adjust the values accordingly.",
    )
