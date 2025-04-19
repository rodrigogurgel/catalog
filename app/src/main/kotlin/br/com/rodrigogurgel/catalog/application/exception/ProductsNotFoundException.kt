package br.com.rodrigogurgel.catalog.application.exception

import br.com.rodrigogurgel.catalog.domain.vo.Id

class ProductsNotFoundException(productIds: List<Id>) : IllegalStateException(
    "Products with IDs ${productIds.joinToString(", ") { productId -> productId.value.toString() }} not found. " +
        "Ensure that the provided product IDs are correct and exist in the system. " +
        "You may need to verify the IDs or create the missing products before proceeding."
)
