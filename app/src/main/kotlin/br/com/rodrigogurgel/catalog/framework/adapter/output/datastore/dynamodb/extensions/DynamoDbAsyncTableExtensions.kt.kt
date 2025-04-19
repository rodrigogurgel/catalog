package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.extensions

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.TableMetadata
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.EnhancedGlobalSecondaryIndex
import software.amazon.awssdk.enhanced.dynamodb.model.EnhancedLocalSecondaryIndex
import software.amazon.awssdk.services.dynamodb.model.Projection
import software.amazon.awssdk.services.dynamodb.model.ProjectionType
import java.util.concurrent.CompletableFuture

fun DynamoDbAsyncTable<*>.createTableWithIndices(
    globalIndicesProjection: ProjectionType,
    localIndicesProjection: ProjectionType,
): CompletableFuture<*> {
    val metadata = tableSchema().tableMetadata()

    val globalIndices = metadata.indices()
        .filter { it.partitionKey().get().name() != metadata.primaryPartitionKey() }
        .map { index ->
            EnhancedGlobalSecondaryIndex.builder()
                .indexName(index.name())
                .projection(Projection.builder().projectionType(globalIndicesProjection).build())
                .build()
        }

    val localIndices = metadata.indices()
        .filter {
            it.partitionKey().get()
                .name() == metadata.primaryPartitionKey() && it.name() != TableMetadata.primaryIndexName()
        }
        .map { index ->
            EnhancedLocalSecondaryIndex.builder()
                .indexName(index.name())
                .projection(Projection.builder().projectionType(localIndicesProjection).build())
                .build()
        }

    val request = CreateTableEnhancedRequest.builder()
        .apply {
            if (localIndices.isNotEmpty()) localSecondaryIndices(localIndices)
            if (globalIndices.isNotEmpty()) globalSecondaryIndices(globalIndices)
        }
        .build()

    return createTable(request)
}
