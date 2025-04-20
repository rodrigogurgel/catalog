package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.repository.impl

import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.model.ProductItemRelationModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.model.ProductModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.repository.ProductRepository
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.utils.CursorUtils
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.exception.DatastoreIntegrationException
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactive.asFlow
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.model.BatchGetItemEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.ReadBatch
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.util.UUID

@Repository
class ProductRepositoryImpl(
    private val dynamoDbAsyncTable: DynamoDbAsyncTable<ProductModel>,
    private val productItemRelationAsyncTable: DynamoDbAsyncTable<ProductItemRelationModel>,
    private val enhancedAsyncClient: DynamoDbEnhancedAsyncClient,
) : ProductRepository {
    companion object {
        private val NOT_EXISTS_EXPRESSION = Expression.builder().expression("attribute_not_exists(product_id)").build()
        private val EXISTS_EXPRESSION = Expression.builder().expression("attribute_exists(product_id)").build()
    }

    override suspend fun countProducts(storeId: UUID): Result<Long, Throwable> = runSuspendCatching {
        val query = QueryConditional.keyEqualTo(
            Key.builder().partitionValue(storeId.toString()).build()
        )
        dynamoDbAsyncTable.query(query).items()
            .asFlow()
            .count()
            .toLong()
    }.mapError { DatastoreIntegrationException(it) }

    override suspend fun create(
        productModel: ProductModel
    ): Result<Unit, Throwable> = runSuspendCatching<Unit> {
        val request = PutItemEnhancedRequest
            .builder(ProductModel::class.java)
            .item(productModel)
            .conditionExpression(NOT_EXISTS_EXPRESSION)
            .build()

        dynamoDbAsyncTable.putItem(request).await()
    }.mapError { DatastoreIntegrationException(it) }

    override suspend fun delete(
        storeId: UUID,
        productId: UUID
    ): Result<Unit, Throwable> = runSuspendCatching<Unit> {
        val key = Key.builder()
            .partitionValue(storeId.toString())
            .sortValue(productId.toString())
            .build()

        val request = DeleteItemEnhancedRequest
            .builder()
            .key(key)
            .build()

        dynamoDbAsyncTable.deleteItem(request).await()
    }.mapError { DatastoreIntegrationException(it) }

    override suspend fun exists(storeId: UUID, productId: UUID): Result<Boolean, Throwable> = runSuspendCatching {
        val key = Key.builder()
            .partitionValue(storeId.toString())
            .sortValue(productId.toString())
            .build()

        val request = GetItemEnhancedRequest
            .builder()
            .consistentRead(true)
            .key(key)
            .build()

        dynamoDbAsyncTable.getItem(request).await() != null
    }.mapError { DatastoreIntegrationException(it) }

    override suspend fun findById(
        storeId: UUID,
        productId: UUID
    ): Result<ProductModel?, Throwable> = runSuspendCatching {
        val key = Key.builder()
            .partitionValue(storeId.toString())
            .sortValue(productId.toString())
            .build()

        val request = GetItemEnhancedRequest
            .builder()
            .consistentRead(true)
            .key(key)
            .build()

        dynamoDbAsyncTable.getItem(request).await()
    }.mapError { DatastoreIntegrationException(it) }

    override suspend fun getIfNotExists(storeId: UUID, productIds: List<UUID>): Result<List<UUID>, Throwable> =
        runSuspendCatching {
            val keys = productIds.map { productId ->
                Key.builder()
                    .partitionValue(storeId.toString())
                    .sortValue(productId.toString())
                    .build()
            }

            keys.ifEmpty { return Ok(emptyList()) }

            val readBatch = keys.map { key ->
                ReadBatch.builder(ProductModel::class.java)
                    .addGetItem(key)
                    .mappedTableResource(dynamoDbAsyncTable)
                    .build()
            }

            val batchGetItem = BatchGetItemEnhancedRequest.builder().readBatches(readBatch).build()

            val result = enhancedAsyncClient
                .batchGetItem(batchGetItem)
                .resultsForTable(dynamoDbAsyncTable)
                .asFlow()
                .toList()
                .map { productModel -> productModel.productId }

            productIds.filterNot { it in result }
        }.mapError { DatastoreIntegrationException(it) }

    override suspend fun getProducts(
        storeId: UUID,
        limit: Int,
        cursor: String?
    ): Result<Pair<String?, List<ProductModel>>, Throwable> = runSuspendCatching {
        val lastEvaluatedKey: Map<String, AttributeValue>? = cursor?.let { CursorUtils.decode(it) }

        val queryConditional = QueryConditional.keyEqualTo(
            Key.builder().partitionValue(storeId.toString()).build()
        )

        val request = QueryEnhancedRequest.builder()
            .limit(limit)
            .exclusiveStartKey(lastEvaluatedKey)
            .scanIndexForward(false)
            .queryConditional(queryConditional)
            .build()

        val page =
            dynamoDbAsyncTable.index(ProductModel.STORE_ID_CREATED_AT_INDEX).query(request).asFlow().firstOrNull()
        val categories = page?.items().orEmpty()
        val nextCursor = page?.lastEvaluatedKey()?.takeIf { it.isNotEmpty() }?.let { CursorUtils.encode(it) }

        nextCursor to categories
    }.mapError { DatastoreIntegrationException(it) }

    override suspend fun productIsInUse(productId: UUID): Result<Boolean, Throwable> = runSuspendCatching {
        val queryConditional = QueryConditional.keyEqualTo(
            Key.builder().partitionValue(productId.toString()).build()
        )

        val request = QueryEnhancedRequest.builder()
            .queryConditional(queryConditional)
            .limit(1)
            .build()

        val page = productItemRelationAsyncTable.query(request).asFlow().firstOrNull()

        page?.items()?.isNotEmpty() ?: false
    }

    override suspend fun update(
        productModel: ProductModel
    ): Result<Unit, Throwable> = runSuspendCatching<Unit> {
        val request = UpdateItemEnhancedRequest
            .builder(ProductModel::class.java)
            .item(productModel)
            .conditionExpression(EXISTS_EXPRESSION)
            .build()

        dynamoDbAsyncTable.updateItem(request).await()
    }.mapError { DatastoreIntegrationException(it) }
}
