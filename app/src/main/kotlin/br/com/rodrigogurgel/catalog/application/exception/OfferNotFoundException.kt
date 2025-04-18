package br.com.rodrigogurgel.catalog.application.exception

import br.com.rodrigogurgel.catalog.domain.vo.Id

class OfferNotFoundException(
    storeId: Id,
    offerId: Id,
) : IllegalStateException(
    "Offer with ID '${offerId.value}' not found in Store with ID '${storeId.value}'. " +
        "Ensure that the offer exists and belongs to the specified store. " +
        "You may need to verify the IDs or create a new offer if it does not exist."
)
