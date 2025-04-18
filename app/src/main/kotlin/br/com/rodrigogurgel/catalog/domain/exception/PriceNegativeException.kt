package br.com.rodrigogurgel.catalog.domain.exception

import java.math.BigDecimal

data class PriceNegativeException(private val price: BigDecimal) :
    IllegalArgumentException("The price must be a positive value. Please provide a valid price greater than zero.n")
