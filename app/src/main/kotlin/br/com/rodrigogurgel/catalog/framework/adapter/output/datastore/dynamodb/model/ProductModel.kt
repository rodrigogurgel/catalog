package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.model

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey
import java.time.Instant
import java.util.UUID

@DynamoDbBean
data class ProductModel(
    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("store_id")
    @get:DynamoDbSecondaryPartitionKey(indexNames = [STORE_ID_CREATED_AT_INDEX])
    var storeId: UUID? = null,

    @get:DynamoDbSortKey
    @get:DynamoDbAttribute("product_id")
    var productId: UUID? = null,

    @get:DynamoDbAttribute("name")
    var name: String? = null,

    @get:DynamoDbAttribute("description")
    var description: String? = null,

    @get:DynamoDbAttribute("medias")
    var medias: List<MediaModel>? = null,

    @get:DynamoDbAttribute("created_at")
    @get:DynamoDbSecondarySortKey(indexNames = [STORE_ID_CREATED_AT_INDEX])
    var createdAt: Instant? = null,

    @get:DynamoDbAttribute("updated_at")
    var updatedAt: Instant? = null,
) {
    companion object {
        const val STORE_ID_CREATED_AT_INDEX = "store_id_created_at_index"
    }
}
