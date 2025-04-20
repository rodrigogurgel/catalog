package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.model

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey
import java.util.UUID

@DynamoDbBean
data class ProductItemRelationModel(
    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("product_id")
    var productId: UUID? = null,

    @get:DynamoDbSortKey
    @get:DynamoDbAttribute("item_id")
    var storeId: UUID? = null,
)
