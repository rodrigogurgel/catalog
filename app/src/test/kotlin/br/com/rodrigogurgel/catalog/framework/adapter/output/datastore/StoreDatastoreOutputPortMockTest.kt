package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore

import br.com.rodrigogurgel.catalog.domain.vo.Id
import io.kotest.matchers.booleans.shouldBeTrue
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.util.UUID

class StoreDatastoreOutputPortMockTest {
    @Test
    fun `Should return true when is mock`() = runTest {
        val mock = StoreDatastoreOutputPortMock()

        mock.exists(Id(UUID.randomUUID())).value.shouldBeTrue()
    }
}
