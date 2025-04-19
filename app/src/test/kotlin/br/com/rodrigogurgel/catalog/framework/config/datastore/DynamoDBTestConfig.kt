package br.com.rodrigogurgel.catalog.framework.config.datastore

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB
import org.testcontainers.utility.DockerImageName
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

@TestConfiguration
class DynamoDBTestConfig {
    companion object {
        private val LOCALSTACK_IMAGE_NAME: DockerImageName = DockerImageName.parse("localstack/localstack:latest")
    }

    @Bean
    fun localStackContainer(): LocalStackContainer {
        return LocalStackContainer(LOCALSTACK_IMAGE_NAME).withServices(DYNAMODB)
            .also { it.start() }
    }

    @Bean
    fun awsCredentialsProvider(localStack: LocalStackContainer): AwsCredentialsProvider {
        val credentials = AwsBasicCredentials.create(localStack.accessKey, localStack.secretKey)
        return StaticCredentialsProvider.create(credentials)
    }

    @Bean
    fun dynamoDbAsyncClient(
        localStack: LocalStackContainer,
        credentialsProvider: AwsCredentialsProvider
    ): DynamoDbAsyncClient {
        return DynamoDbAsyncClient.builder()
            .endpointOverride(localStack.getEndpointOverride(DYNAMODB))
            .region(Region.of(localStack.region))
            .credentialsProvider(credentialsProvider)
            .build()
    }

    @Bean
    fun enhancedAsyncClient(dynamoDbAsyncClient: DynamoDbAsyncClient): DynamoDbEnhancedAsyncClient {
        return DynamoDbEnhancedAsyncClient.builder()
            .dynamoDbClient(dynamoDbAsyncClient)
            .build()
    }
}
