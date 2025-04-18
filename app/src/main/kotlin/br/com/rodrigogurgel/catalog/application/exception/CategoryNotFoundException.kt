package br.com.rodrigogurgel.catalog.application.exception

import br.com.rodrigogurgel.catalog.domain.vo.Id

class CategoryNotFoundException(storeId: Id, categoryId: Id) :
    IllegalStateException(
        "Category with ID '$categoryId' and Store with ID '$storeId' not found. " +
            "Please verify the provided IDs and try again."
    )
