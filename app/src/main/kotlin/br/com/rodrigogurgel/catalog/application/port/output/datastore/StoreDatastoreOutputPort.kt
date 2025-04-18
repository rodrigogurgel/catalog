package br.com.rodrigogurgel.catalog.application.port.output.datastore

import br.com.rodrigogurgel.catalog.domain.vo.Id
import com.github.michaelbull.result.Result

interface StoreDatastoreOutputPort {
    suspend fun exists(id: Id): Result<Boolean, Throwable>
}
