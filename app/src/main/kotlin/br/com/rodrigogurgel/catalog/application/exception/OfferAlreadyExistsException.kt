package br.com.rodrigogurgel.catalog.application.exception

import br.com.rodrigogurgel.catalog.domain.vo.Id

class OfferAlreadyExistsException(offerId: Id) :
    IllegalArgumentException(
        "Offer with ID '${offerId.value}' already exists. " +
            "Consider using a different ID, updating the existing offer, " +
            "or verifying if the offer should be duplicated."
    )
