package br.com.rodrigogurgel.catalog.domain.exception

import br.com.rodrigogurgel.catalog.domain.vo.Id

data class OfferPriceZeroException(private val offerId: Id) :
    IllegalArgumentException(
        "Offer with ID '$offerId' has a price of zero. Please set a valid price before proceeding."
    )
