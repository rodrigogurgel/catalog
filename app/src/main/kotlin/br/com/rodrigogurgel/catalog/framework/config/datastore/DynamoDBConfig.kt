package br.com.rodrigogurgel.catalog.framework.config.datastore

import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.extensions.createTableWithIndices
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.model.CategoryModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.model.ProductItemRelationModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.model.ProductModel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.ProjectionType
import java.net.URI

@Configuration
class DynamoDBConfig {
    @Bean
    @Profile("!test")
    fun dynamoDbAsyncClient(
        dynamoDBProperties: DynamoDBProperties
    ): DynamoDbAsyncClient {
        return DynamoDbAsyncClient
            .builder()
            .region(Region.of(dynamoDBProperties.region))
            .endpointOverride(dynamoDBProperties.endpoint?.let { URI.create(it) })
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        dynamoDBProperties.accessKeyId,
                        dynamoDBProperties.secretAccessKey
                    )
                )
            )
            .build()
    }

    @Bean
    fun enhancedAsyncClient(dynamoDbAsyncClient: DynamoDbAsyncClient): DynamoDbEnhancedAsyncClient =
        DynamoDbEnhancedAsyncClient.builder()
            .dynamoDbClient(dynamoDbAsyncClient)
            .build()

    @Bean
    fun categoryDynamoDbAsyncTable(
        enhancedAsyncClient: DynamoDbEnhancedAsyncClient,
    ): DynamoDbAsyncTable<CategoryModel> =
        enhancedAsyncClient.table("category", TableSchema.fromBean(CategoryModel::class.java))
            .apply { createTableWithIndices(ProjectionType.KEYS_ONLY, ProjectionType.ALL) }

    @Bean
    fun productDynamoDbAsyncTable(
        enhancedAsyncClient: DynamoDbEnhancedAsyncClient,
    ): DynamoDbAsyncTable<ProductModel> =
        enhancedAsyncClient.table("product", TableSchema.fromBean(ProductModel::class.java))
            .apply { createTableWithIndices(ProjectionType.KEYS_ONLY, ProjectionType.ALL) }

    @Bean
    fun productItemRelationDynamoDbAsyncTable(
        enhancedAsyncClient: DynamoDbEnhancedAsyncClient,
    ): DynamoDbAsyncTable<ProductItemRelationModel> =
        enhancedAsyncClient.table("product_item_relation", TableSchema.fromBean(ProductItemRelationModel::class.java))
            .apply { createTableWithIndices(ProjectionType.KEYS_ONLY, ProjectionType.ALL) }
}
