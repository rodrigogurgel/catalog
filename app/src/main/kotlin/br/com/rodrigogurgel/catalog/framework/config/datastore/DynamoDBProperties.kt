package br.com.rodrigogurgel.catalog.framework.config.datastore

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "aws.dynamodb")
data class DynamoDBProperties(
    val endpoint: String? = null,
    val region: String,
    val accessKeyId: String,
    val secretAccessKey: String,
)
