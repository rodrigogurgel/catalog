package br.com.rodrigogurgel.catalog.application.exception

import br.com.rodrigogurgel.catalog.domain.vo.Id

class ProductNotFoundException(storeId: Id, productId: Id) :
    IllegalStateException(
        "Product with ID '$productId' and Store with ID '$storeId' not found. " +
            "Please verify the provided IDs and try again."
    )
