package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.repository.impl

import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.model.CategoryModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.repository.CategoryRepository
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.utils.CursorUtils
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.exception.DatastoreIntegrationException
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactive.asFlow
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.util.UUID

@Repository
class CategoryRepositoryImpl(
    private val dynamoDbAsyncTable: DynamoDbAsyncTable<CategoryModel>,
) : CategoryRepository {
    companion object {
        private val NOT_EXISTS_EXPRESSION = Expression.builder().expression("attribute_not_exists(category_id)").build()
        private val EXISTS_EXPRESSION = Expression.builder().expression("attribute_exists(category_id)").build()
    }

    override suspend fun countCategories(storeId: UUID): Result<Long, Throwable> {
        val query = QueryConditional.keyEqualTo(
            Key.builder().partitionValue(storeId.toString()).build()
        )
        return runCatching {
            dynamoDbAsyncTable.query(query).items()
                .asFlow()
                .count()
                .toLong()
        }.mapError { DatastoreIntegrationException(it) }
    }

    override suspend fun create(categoryModel: CategoryModel): Result<Unit, Throwable> = runSuspendCatching<Unit> {
        val request = PutItemEnhancedRequest
            .builder(CategoryModel::class.java)
            .item(categoryModel)
            .conditionExpression(NOT_EXISTS_EXPRESSION)
            .build()

        runSuspendCatching {
            dynamoDbAsyncTable.putItem(request).await()
        }.mapError { DatastoreIntegrationException(it) }
    }

    override suspend fun delete(
        storeId: UUID,
        categoryId: UUID
    ): Result<Unit, Throwable> {
        val key = Key.builder()
            .partitionValue(storeId.toString())
            .sortValue(categoryId.toString())
            .build()

        val request = DeleteItemEnhancedRequest
            .builder()
            .key(key)
            .build()

        return runSuspendCatching<Unit> {
            dynamoDbAsyncTable.deleteItem(request).await()
        }.mapError { DatastoreIntegrationException(it) }
    }

    override suspend fun exists(
        storeId: UUID,
        categoryId: UUID
    ): Result<Boolean, Throwable> {
        val key = Key.builder()
            .partitionValue(storeId.toString())
            .sortValue(categoryId.toString())
            .build()

        val request = GetItemEnhancedRequest
            .builder()
            .consistentRead(true)
            .key(key)
            .build()

        return runSuspendCatching {
            dynamoDbAsyncTable.getItem(request).await() != null
        }.mapError { DatastoreIntegrationException(it) }
    }

    override suspend fun findById(
        storeId: UUID,
        categoryId: UUID
    ): Result<CategoryModel?, Throwable> {
        val key = Key.builder()
            .partitionValue(storeId.toString())
            .sortValue(categoryId.toString())
            .build()

        val request = GetItemEnhancedRequest
            .builder()
            .consistentRead(true)
            .key(key)
            .build()

        return runSuspendCatching {
            dynamoDbAsyncTable.getItem(request).await()
        }.mapError { DatastoreIntegrationException(it) }
    }

    override suspend fun getCategories(
        storeId: UUID,
        limit: Int,
        cursor: String?
    ): Result<Pair<String?, List<CategoryModel>>, Throwable> {
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

        return runSuspendCatching {
            val page =
                dynamoDbAsyncTable.index(CategoryModel.STORE_ID_CREATED_AT_INDEX).query(request).asFlow().firstOrNull()
            val categories = page?.items().orEmpty()
            val nextCursor = page?.lastEvaluatedKey()?.takeIf { it.isNotEmpty() }?.let { CursorUtils.encode(it) }

            nextCursor to categories
        }.mapError { DatastoreIntegrationException(it) }
    }

    override suspend fun update(
        categoryModel: CategoryModel
    ): Result<Unit, Throwable> {
        val key = Key.builder()
            .partitionValue(categoryModel.storeId.toString())
            .sortValue(categoryModel.categoryId.toString())
            .build()

        val request = UpdateItemEnhancedRequest
            .builder(CategoryModel::class.java)
            .item(categoryModel)
            .conditionExpression(EXISTS_EXPRESSION)
            .build()

        return runSuspendCatching<Unit> {
            dynamoDbAsyncTable.updateItem(request).await()
        }.mapError { DatastoreIntegrationException(it) }
    }
}
