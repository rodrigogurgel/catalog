package br.com.rodrigogurgel.catalog.application.exception

import br.com.rodrigogurgel.catalog.domain.vo.Id

class ProductIsInUseException(productId: Id) :
    IllegalStateException(
        "The product with ID '$productId' cannot be deleted because it is in use. " +
            "Ensure that no active dependencies or references exist before attempting deletion.",
    )
