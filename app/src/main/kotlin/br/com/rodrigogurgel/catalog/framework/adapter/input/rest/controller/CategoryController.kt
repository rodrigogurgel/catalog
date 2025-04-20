package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.controller

import br.com.rodrigogurgel.catalog.common.logger.extensions.CATEGORY_ID
import br.com.rodrigogurgel.catalog.common.logger.extensions.CURSOR
import br.com.rodrigogurgel.catalog.common.logger.extensions.LIMIT
import br.com.rodrigogurgel.catalog.common.logger.extensions.RESULT
import br.com.rodrigogurgel.catalog.common.logger.extensions.STORE_ID
import br.com.rodrigogurgel.catalog.common.logger.extensions.responseProduced
import br.com.rodrigogurgel.catalog.domain.usecase.category.CountCategoriesUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.category.CreateCategoryUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.category.DeleteCategoryUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.category.GetCategoriesUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.category.GetCategoryUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.category.UpdateCategoryUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.offer.CountOffersUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.offer.GetOffersUseCase
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.category.CategoryRequestMapper.asEntity
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.category.CategoryResponseMapper.asResponse
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.offer.OfferResponseMapper.asResponse
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.request.category.CategoryRequestDTO
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.category.CategoryPageResponseDTO
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.category.CategoryResponseDTO
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.common.GenericResponseIdDTO
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.offer.OfferPageResponseDTO
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.extensions.failure
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.extensions.success
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapBoth
import com.github.michaelbull.result.mapCatching
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
@RequestMapping("/categories")
class CategoryController(
    private val getCategoryUseCase: GetCategoryUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val createCategoryUseCase: CreateCategoryUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val countCategoriesUseCase: CountCategoriesUseCase,
    private val countOffersUseCase: CountOffersUseCase,
    private val getOffersUseCase: GetOffersUseCase,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Operation(summary = "Get a page of Categories of the Store", description = "Returns 200 if successful")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successful Operation",
                useReturnTypeSchema = true,
            ),
        ]
    )
    @GetMapping(
        produces = [APPLICATION_JSON_VALUE]
    )
    suspend fun getCategories(
        @RequestParam storeId: UUID,
        @RequestParam(defaultValue = "20", required = false) limit: Int,
        @RequestParam(required = false) cursor: String? = null,
    ): ResponseEntity<CategoryPageResponseDTO> = coroutineBinding {
        val total = countCategoriesUseCase.execute(Id(storeId)).bind()
        val nextCursorToCategories = getCategoriesUseCase.execute(
            Id(storeId),
            limit,
            cursor
        ).bind()

        val nextCursor = nextCursorToCategories.first
        val categories = nextCursorToCategories.second.map { it.asResponse() }

        CategoryPageResponseDTO(limit, nextCursor, total, categories)
    }.mapBoth({
        logger.responseProduced(STORE_ID to storeId, LIMIT to limit, CURSOR to cursor, RESULT to it)
        success<CategoryPageResponseDTO>(it)
    }, {
        logger.responseProduced(it, STORE_ID to storeId, LIMIT to limit, CURSOR to cursor)
        failure(it)
    })

    @Operation(summary = "Get a Category of the Store", description = "Returns 200 if successful")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successful Operation",
                useReturnTypeSchema = true,
            ),
        ]
    )
    @GetMapping(
        "/{categoryId}",
        produces = [APPLICATION_JSON_VALUE]
    )
    suspend fun getCategoryById(
        @RequestParam storeId: UUID,
        @PathVariable categoryId: UUID,
    ): ResponseEntity<CategoryResponseDTO> = getCategoryUseCase.execute(Id(storeId), Id(categoryId))
        .mapCatching { it.asResponse() }
        .mapBoth({
            logger.responseProduced(STORE_ID to storeId, CATEGORY_ID to categoryId, RESULT to it)
            success(it)
        }, {
            logger.responseProduced(it, STORE_ID to storeId, CATEGORY_ID to categoryId)
            failure(it)
        })

    @Operation(summary = "Create a Category in the Store", description = "Returns 201 if successful")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Successful Operation",
                useReturnTypeSchema = true,
            ),
        ]
    )
    @PostMapping(
        produces = [APPLICATION_JSON_VALUE],
        consumes = [APPLICATION_JSON_VALUE]
    )
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createCategory(
        @RequestParam storeId: UUID,
        @RequestBody categoryRequestDTO: CategoryRequestDTO,
    ): ResponseEntity<GenericResponseIdDTO> = runSuspendCatching {
        categoryRequestDTO.asEntity()
    }.andThen { category ->
        createCategoryUseCase.execute(Id(storeId), category)
            .map { GenericResponseIdDTO(category.id.value.toString()) }
    }.mapBoth({
        logger.responseProduced(STORE_ID to storeId, RESULT to it)
        success(it, HttpStatus.CREATED)
    }, {
        logger.responseProduced(it, STORE_ID to storeId)
        failure(it)
    })

    @Operation(summary = "Update a Category in the Store", description = "Returns 200 if successful")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successful Operation",
                useReturnTypeSchema = true,
            ),
        ]
    )
    @PutMapping(
        "/{categoryId}",
        produces = [APPLICATION_JSON_VALUE],
        consumes = [APPLICATION_JSON_VALUE]
    )
    suspend fun updateCategory(
        @RequestParam storeId: UUID,
        @PathVariable categoryId: UUID,
        @RequestBody categoryRequestDTO: CategoryRequestDTO,
    ): ResponseEntity<Unit> = runSuspendCatching {
        categoryRequestDTO.asEntity(categoryId)
    }.andThen { category ->
        updateCategoryUseCase.execute(Id(storeId), category)
    }.mapBoth({
        logger.responseProduced(STORE_ID to storeId, CATEGORY_ID to categoryId, RESULT to it)
        success(it)
    }, {
        logger.responseProduced(it, STORE_ID to storeId, CATEGORY_ID to categoryId)
        failure(it)
    })

    @Operation(summary = "Delete a Category in the Store", description = "Returns 204 if successful")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Successful Operation",
                useReturnTypeSchema = true,
            ),
        ]
    )
    @DeleteMapping(
        "/{categoryId}"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteCategoryById(
        @RequestParam storeId: UUID,
        @PathVariable categoryId: UUID,
    ): ResponseEntity<Unit> = deleteCategoryUseCase.execute(Id(storeId), Id(categoryId))
        .mapBoth({
            logger.responseProduced(STORE_ID to storeId, CATEGORY_ID to categoryId)
            success(it, HttpStatus.NO_CONTENT)
        }, {
            logger.responseProduced(it, STORE_ID to storeId, CATEGORY_ID to categoryId)
            failure(it)
        })

    @Operation(
        summary = "Get a page of Offers from the Category in the Store",
        description = "Returns 200 if successful"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successful Operation",
                useReturnTypeSchema = true,
            ),
        ]
    )
    @GetMapping(
        "/{categoryId}/offers",
        produces = [APPLICATION_JSON_VALUE]
    )
    suspend fun getOffers(
        @PathVariable categoryId: UUID,
        @RequestParam storeId: UUID,
        @RequestParam(defaultValue = "20", required = false) limit: Int,
        @RequestParam(defaultValue = "0", required = false) offset: Int,
    ): ResponseEntity<OfferPageResponseDTO> = coroutineBinding {
        val total = countOffersUseCase.execute(Id(storeId), Id(categoryId)).bind()
        val offers = getOffersUseCase.execute(Id(storeId), Id(categoryId), limit, offset).bind()
        OfferPageResponseDTO(limit, null, total, offers.map { offer -> offer.asResponse() })
    }.mapBoth({
        logger.responseProduced(STORE_ID to storeId, CATEGORY_ID to categoryId, RESULT to it)
        success(it)
    }, {
        logger.responseProduced(it, STORE_ID to storeId, CATEGORY_ID to categoryId)
        failure(it)
    })
}
