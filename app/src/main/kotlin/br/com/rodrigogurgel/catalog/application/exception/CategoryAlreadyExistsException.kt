package br.com.rodrigogurgel.catalog.application.exception

import br.com.rodrigogurgel.catalog.domain.vo.Id

class CategoryAlreadyExistsException(categoryId: Id) :
    IllegalArgumentException(
        "Category with ID '$categoryId' already exists. Consider using a different " +
            "ID or updating the existing category."
    )
