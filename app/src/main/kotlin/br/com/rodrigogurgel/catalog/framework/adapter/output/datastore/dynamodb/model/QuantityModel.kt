package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.model

data class QuantityModel(
    val minPermitted: Int,
    val maxPermitted: Int,
)
