package br.com.rodrigogurgel.catalog.application.exception

import br.com.rodrigogurgel.catalog.domain.vo.Id

class StoreNotFoundException(storeId: Id) :
    IllegalStateException("Store with ID '$storeId' not found. Please verify the provided ID and try again.")
