package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.model

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean

@DynamoDbBean
data class MediaModel(
    @get:DynamoDbAttribute("url")
    var url: String? = null,

    @get:DynamoDbAttribute("type")
    var type: String? = null
)
