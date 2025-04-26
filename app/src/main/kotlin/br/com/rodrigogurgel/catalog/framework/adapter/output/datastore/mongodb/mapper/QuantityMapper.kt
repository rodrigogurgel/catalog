package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.mapper

import br.com.rodrigogurgel.catalog.domain.vo.Quantity
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.QuantityModel

object QuantityMapper {
    fun Quantity.asModel(): QuantityModel = QuantityModel(
        minPermitted = minPermitted,
        maxPermitted = maxPermitted
    )

    fun QuantityModel.asEntity(): Quantity = Quantity(
        minPermitted = minPermitted,
        maxPermitted = maxPermitted
    )
}
