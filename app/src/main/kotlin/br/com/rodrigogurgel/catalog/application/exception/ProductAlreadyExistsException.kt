package br.com.rodrigogurgel.catalog.application.exception

import br.com.rodrigogurgel.catalog.domain.vo.Id

class ProductAlreadyExistsException(productId: Id) :
    IllegalArgumentException(
        "Product with ID '$productId' already exists. Consider using a different ID or updating the existing product.\n"
    )
