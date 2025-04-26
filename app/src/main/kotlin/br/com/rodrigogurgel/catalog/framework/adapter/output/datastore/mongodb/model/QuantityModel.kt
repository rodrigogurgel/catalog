package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model

import org.springframework.data.mongodb.core.mapping.Field

data class QuantityModel(
    @Field("min_permitted")
    val minPermitted: Int,
    @Field("max_permitted")
    val maxPermitted: Int,
)
