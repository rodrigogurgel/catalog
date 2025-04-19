package br.com.rodrigogurgel.catalog.framework.config.datastore

import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.extensions.createTableWithIndices
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.model.CategoryModel
import org.springframework.beans.factory.annotation.Value
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

@Configuration
class DynamoDBConfig {

    @Bean
    @Profile("!test")
    fun dynamoDbAsyncClient(
        @Value("\${aws.region}")
        region: String,
        @Value("\${aws.accessKeyId}")
        accessKeyId: String,
        @Value("\${aws.secretAccessKey}")
        secretAccessKey: String,
    ): DynamoDbAsyncClient {
        return DynamoDbAsyncClient
            .builder()
            .region(Region.of(region))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKeyId, secretAccessKey)
                )
            )
            .build()
    }

    @Bean
    @Profile("!test")
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
}
