package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore

import br.com.rodrigogurgel.catalog.application.port.output.datastore.StoreDatastoreOutputPort
import br.com.rodrigogurgel.catalog.domain.vo.Id
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.springframework.stereotype.Component

@Component
class StoreDatastoreOutputPortMock : StoreDatastoreOutputPort {
    override suspend fun exists(id: Id): Result<Boolean, Throwable> = Ok(true)
}
