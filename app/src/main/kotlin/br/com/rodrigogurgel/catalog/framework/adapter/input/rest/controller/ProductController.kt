package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.controller

import br.com.rodrigogurgel.catalog.common.logger.extensions.CURSOR
import br.com.rodrigogurgel.catalog.common.logger.extensions.LIMIT
import br.com.rodrigogurgel.catalog.common.logger.extensions.PRODUCT
import br.com.rodrigogurgel.catalog.common.logger.extensions.PRODUCT_ID
import br.com.rodrigogurgel.catalog.common.logger.extensions.STORE_ID
import br.com.rodrigogurgel.catalog.common.logger.extensions.responseProduced
import br.com.rodrigogurgel.catalog.domain.usecase.product.CountProductsUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.product.CreateProductUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.product.DeleteProductUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.product.GetProductUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.product.GetProductsUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.product.UpdateProductUseCase
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.product.ProductRequestMapper.asEntity
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.product.ProductResponseMapper.asResponse
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.request.product.ProductRequestDTO
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.common.GenericResponseIdDTO
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.product.ProductPageResponseDTO
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.product.ProductResponseDTO
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.extensions.failure
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.extensions.success
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapBoth
import com.github.michaelbull.result.mapCatching
import com.github.michaelbull.result.runCatching
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/products")
class ProductController(
    private val createProductUseCase: CreateProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val getProductUseCase: GetProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val getProductsUseCase: GetProductsUseCase,
    private val countProductsUseCase: CountProductsUseCase,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Operation(
        summary = "Retrieve a paginated list of products from a store",
        description = "Returns a paginated list of products associated with the given store ID. " +
            "Supports filtering by product name prefix. Returns HTTP 200 if successful."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Products retrieved successfully",
                useReturnTypeSchema = true,
            ),
        ]
    )
    @GetMapping(
        produces = [APPLICATION_JSON_VALUE]
    )
    suspend fun fetchPaginatedProducts(
        @RequestParam storeId: UUID,
        @RequestParam(defaultValue = "20", required = false) limit: Int,
        @RequestParam(defaultValue = "0", required = false) offset: Int,
    ): ResponseEntity<ProductPageResponseDTO> = coroutineBinding {
        val total = countProductsUseCase.execute(Id(storeId)).bind()
        val products = getProductsUseCase.execute(Id(storeId), limit, offset).bind()

        ProductPageResponseDTO(
            limit,
            null,
            total,
            products.map { product -> product.asResponse() }
        )
    }.mapBoth({
        logger.responseProduced(STORE_ID to storeId, LIMIT to limit, CURSOR to offset)
        success(it)
    }, {
        logger.responseProduced(it, STORE_ID to storeId, LIMIT to limit, CURSOR to offset)
        failure(it)
    })

    @Operation(
        summary = "Create a new product in the store",
        description = "Creates a new product under the specified store. Returns HTTP 201 upon successful creation."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Product created successfully",
                useReturnTypeSchema = true,
            ),
        ]
    )
    @PostMapping(
        consumes = [APPLICATION_JSON_VALUE],
        produces = [APPLICATION_JSON_VALUE]
    )
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createProduct(
        @RequestParam storeId: UUID,
        @RequestBody productRequestDTO: ProductRequestDTO,
    ): ResponseEntity<GenericResponseIdDTO> = runCatching {
        productRequestDTO.asEntity()
    }.andThen { product ->
        createProductUseCase.execute(Id(storeId), product)
            .map { GenericResponseIdDTO(product.id.value.toString()) }
    }.mapBoth({
        logger.responseProduced(STORE_ID to storeId, PRODUCT to it)
        success(it, HttpStatus.CREATED)
    }, {
        logger.responseProduced(it, STORE_ID to storeId)
        failure(it)
    })

    @Operation(
        summary = "Update an existing product in the store",
        description = "Updates the details of a specific product identified by its ID within the given store. " +
            "Returns HTTP 200 if the update is successful."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Product updated successfully",
                useReturnTypeSchema = true,
            ),
        ]
    )
    @PutMapping(
        "/{id}",
        consumes = [APPLICATION_JSON_VALUE],
        produces = [APPLICATION_JSON_VALUE]
    )
    suspend fun updateProduct(
        @RequestParam storeId: UUID,
        @PathVariable(value = "id") productId: UUID,
        @RequestBody productRequestDTO: ProductRequestDTO,
    ): ResponseEntity<Unit> =
        runCatching { productRequestDTO.asEntity(productId,) }
            .andThen { product ->
                updateProductUseCase.execute(Id(storeId), product)
            }
            .mapBoth({
                logger.responseProduced(STORE_ID to storeId, PRODUCT to it)
                success(it)
            }, {
                logger.responseProduced(it, STORE_ID to storeId)
                failure(it)
            })

    @Operation(
        summary = "Retrieve a specific product from the store",
        description = "Fetches details of a product by its ID within the specified store. " +
            "Returns HTTP 200 if the product is found."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Product found successfully",
                useReturnTypeSchema = true,
            ),
        ]
    )
    @GetMapping(
        "/{productId}",
        produces = [APPLICATION_JSON_VALUE]
    )
    suspend fun getProductById(
        @RequestParam storeId: UUID,
        @PathVariable productId: UUID,
    ): ResponseEntity<ProductResponseDTO> = getProductUseCase.execute(Id(storeId), Id(productId))
        .mapCatching { it.asResponse() }
        .mapBoth({
            logger.responseProduced(STORE_ID to storeId, PRODUCT to it)
            success(it)
        }, {
            logger.responseProduced(it, STORE_ID to storeId)
            failure(it)
        })

    @Operation(
        summary = "Delete a product from the store",
        description = "Removes a product identified by its ID from the specified store. " +
            "Returns HTTP 204 upon successful deletion."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Product deleted successfully",
                useReturnTypeSchema = true,
            ),
        ]
    )
    @DeleteMapping(
        "/{productId}",
        produces = [APPLICATION_JSON_VALUE]
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteProductById(
        @RequestParam storeId: UUID,
        @PathVariable productId: UUID,
    ): ResponseEntity<Unit> = deleteProductUseCase.execute(Id(storeId), Id(productId))
        .mapBoth({
            logger.responseProduced(STORE_ID to storeId, PRODUCT_ID to productId)
            success(Unit, HttpStatus.NO_CONTENT)
        }, {
            logger.responseProduced(it, STORE_ID to storeId, PRODUCT_ID to productId)
            failure(it)
        })
}
