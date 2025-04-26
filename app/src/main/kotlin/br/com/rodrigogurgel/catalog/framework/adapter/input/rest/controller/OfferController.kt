package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.controller

import br.com.rodrigogurgel.catalog.common.logger.extensions.RESULT
import br.com.rodrigogurgel.catalog.common.logger.extensions.STORE_ID
import br.com.rodrigogurgel.catalog.common.logger.extensions.responseProduced
import br.com.rodrigogurgel.catalog.domain.usecase.offer.AddCustomizationOnChildrenUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.offer.AddCustomizationUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.offer.AddOptionOnChildrenUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.offer.CreateOfferUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.offer.DeleteOfferUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.offer.GetOfferUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.offer.RemoveCustomizationOnChildrenUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.offer.RemoveCustomizationUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.offer.RemoveOptionOnChildrenUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.offer.UpdateCustomizationOnChildrenUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.offer.UpdateCustomizationUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.offer.UpdateOfferUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.offer.UpdateOptionOnChildrenUseCase
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.offer.CustomizationRequestMapper.asEntity
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.offer.OfferRequestMapper.asEntity
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.offer.OfferResponseMapper.asResponse
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.offer.OptionRequestMapper.asEntity
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.request.offer.CustomizationRequestDTO
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.request.offer.OfferRequestDTO
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.request.offer.OptionRequestDTO
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.common.GenericResponseIdDTO
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.offer.OfferResponseDTO
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.extensions.failure
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.extensions.success
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapBoth
import com.github.michaelbull.result.mapCatching
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
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
@RequestMapping("offers")
class OfferController(
    private val createOfferUseCase: CreateOfferUseCase,
    private val getOfferUseCase: GetOfferUseCase,
    private val deleteOfferUseCase: DeleteOfferUseCase,
    private val updateOfferUseCase: UpdateOfferUseCase,
    private val addCustomizationUseCase: AddCustomizationUseCase,
    private val updateCustomizationUseCase: UpdateCustomizationUseCase,
    private val removeCustomizationUseCase: RemoveCustomizationUseCase,
    private val addCustomizationOnChildrenUseCase: AddCustomizationOnChildrenUseCase,
    private val updateCustomizationOnChildrenUseCase: UpdateCustomizationOnChildrenUseCase,
    private val removeCustomizationOnChildrenUseCase: RemoveCustomizationOnChildrenUseCase,
    private val addOptionOnChildrenUseCase: AddOptionOnChildrenUseCase,
    private val updateOptionOnChildrenUseCase: UpdateOptionOnChildrenUseCase,
    private val removeOptionOnChildrenUseCase: RemoveOptionOnChildrenUseCase,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Operation(summary = "Create an Offer in the Store", description = "Returns 201 if successful")
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
        consumes = [APPLICATION_JSON_VALUE],
        produces = [APPLICATION_JSON_VALUE]
    )
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createOffer(
        @RequestParam storeId: UUID,
        @RequestParam categoryId: UUID,
        @RequestBody offerRequestDTO: OfferRequestDTO,
    ): ResponseEntity<GenericResponseIdDTO> = withContext(MDCContext()) {
        runSuspendCatching {
            offerRequestDTO.asEntity()
        }.andThen { offer ->
            createOfferUseCase.execute(Id(storeId), Id(categoryId), offer)
                .map { GenericResponseIdDTO(offer.id.value.toString()) }
        }.mapBoth({
            logger.responseProduced(STORE_ID to storeId, RESULT to it)
            success(it, HttpStatus.CREATED)
        }, {
            logger.responseProduced(it, STORE_ID to storeId)
            failure(it)
        })
    }

    @Operation(summary = "Get an Offer from the Store", description = "Returns 200 if successful")
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
        "/{offerId}",
        produces = [APPLICATION_JSON_VALUE]
    )
    suspend fun getOfferById(
        @RequestParam storeId: UUID,
        @PathVariable offerId: UUID,
    ): ResponseEntity<OfferResponseDTO> = withContext(MDCContext()) {
        getOfferUseCase.execute(Id(storeId), Id(offerId))
            .mapCatching { it.asResponse() }
            .mapBoth({
                logger.responseProduced(STORE_ID to storeId, RESULT to it)
                success(it)
            }, {
                logger.responseProduced(it, STORE_ID to storeId)
                failure(it)
            })
    }

    @Operation(summary = "Delete an Offer from the Store", description = "Returns 204 if successful")
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
        "/{offerId}",
        produces = [APPLICATION_JSON_VALUE]
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteOffer(
        @RequestParam storeId: UUID,
        @PathVariable offerId: UUID,
    ): ResponseEntity<Unit> = withContext(MDCContext()) {
        deleteOfferUseCase.execute(Id(storeId), Id(offerId))
            .mapBoth({
                logger.responseProduced(STORE_ID to storeId, RESULT to it)
                success(it, HttpStatus.NO_CONTENT)
            }, {
                logger.responseProduced(it, STORE_ID to storeId)
                failure(it)
            })
    }

    @Operation(summary = "Update an Offer in the Store", description = "Returns 200 if successful")
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
        "/{offerId}",
        consumes = [APPLICATION_JSON_VALUE],
        produces = [APPLICATION_JSON_VALUE]
    )
    suspend fun updateOffer(
        @RequestParam storeId: UUID,
        @RequestParam categoryId: UUID,
        @PathVariable offerId: UUID,
        @RequestBody offerRequestDTO: OfferRequestDTO,
    ): ResponseEntity<Unit> = withContext(MDCContext()) {
        runSuspendCatching {
            offerRequestDTO.asEntity(offerId)
        }.andThen { offer ->
            updateOfferUseCase.execute(Id(storeId), Id(categoryId), offer)
        }.mapBoth({
            logger.responseProduced(STORE_ID to storeId, RESULT to it)
            success(it)
        }, {
            logger.responseProduced(it, STORE_ID to storeId)
            failure(it)
        })
    }

    @Operation(
        summary = "Update an Offer in the Store adding a new Customization",
        description = "Returns 201 if successful"
    )
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
        "/{offerId}/customizations",
        consumes = [APPLICATION_JSON_VALUE],
        produces = [APPLICATION_JSON_VALUE]
    )
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun addCustomization(
        @RequestParam storeId: UUID,
        @RequestParam categoryId: UUID,
        @PathVariable offerId: UUID,
        @RequestBody customizationRequestDTO: CustomizationRequestDTO,
    ): ResponseEntity<GenericResponseIdDTO> = withContext(MDCContext()) {
        runSuspendCatching {
            customizationRequestDTO.asEntity()
        }.andThen { customization ->
            addCustomizationUseCase.execute(Id(storeId), Id(categoryId), Id(offerId), customization)
                .map { GenericResponseIdDTO(customization.id.value.toString()) }
        }.mapBoth({
            logger.responseProduced(STORE_ID to storeId, RESULT to it)
            success(it, HttpStatus.CREATED)
        }, {
            logger.responseProduced(it, STORE_ID to storeId)
            failure(it)
        })
    }

    @Operation(
        summary = "Update a Customization Offer",
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
    @PutMapping(
        "/{offerId}/customizations/{customizationId}",
        consumes = [APPLICATION_JSON_VALUE],
        produces = [APPLICATION_JSON_VALUE]
    )
    suspend fun updateCustomization(
        @RequestParam storeId: UUID,
        @RequestParam categoryId: UUID,
        @PathVariable offerId: UUID,
        @PathVariable customizationId: UUID,
        @RequestBody updateCustomizationRequestDTO: CustomizationRequestDTO,
    ): ResponseEntity<Unit> = withContext(MDCContext()) {
        runSuspendCatching {
            updateCustomizationRequestDTO.copy(id = customizationId).asEntity()
        }.andThen { customization ->
            updateCustomizationUseCase.execute(
                Id(storeId),
                Id(categoryId),
                Id(offerId),
                customization
            )
        }.mapBoth({
            logger.responseProduced(STORE_ID to storeId, RESULT to it)
            success(it)
        }, {
            logger.responseProduced(it, STORE_ID to storeId)
            failure(it)
        })
    }

    @Operation(
        summary = "Remove a Customization from the Offer",
        description = "Returns 204 if successful"
    )
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
        "/{offerId}/customizations/{customizationId}",
        produces = [APPLICATION_JSON_VALUE]
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun removeCustomization(
        @RequestParam storeId: UUID,
        @RequestParam categoryId: UUID,
        @PathVariable offerId: UUID,
        @PathVariable customizationId: UUID,
    ): ResponseEntity<Unit> = withContext(MDCContext()) {
        removeCustomizationUseCase.execute(
            Id(storeId),
            Id(categoryId),
            Id(offerId),
            Id(customizationId)
        ).mapBoth({
            logger.responseProduced(STORE_ID to storeId, RESULT to it)
            success(it, HttpStatus.NO_CONTENT)
        }, {
            logger.responseProduced(it, STORE_ID to storeId)
            failure(it)
        })
    }

    @Operation(
        summary = "Create a Customization in a child of the Offer",
        description = "Returns 201 if successful"
    )
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
        "/{offerId}/options/{optionId}/customizations",
        consumes = [APPLICATION_JSON_VALUE],
        produces = [APPLICATION_JSON_VALUE]
    )
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun addCustomizationOnChildren(
        @RequestParam storeId: UUID,
        @RequestParam categoryId: UUID,
        @PathVariable offerId: UUID,
        @PathVariable optionId: UUID,
        @RequestBody customizationRequestDTO: CustomizationRequestDTO,
    ): ResponseEntity<GenericResponseIdDTO> = withContext(MDCContext()) {
        runSuspendCatching {
            customizationRequestDTO.asEntity()
        }.andThen { customization ->
            addCustomizationOnChildrenUseCase.execute(
                Id(storeId),
                Id(categoryId),
                Id(offerId),
                Id(optionId),
                customization
            ).map {
                GenericResponseIdDTO(customization.id.value.toString())
            }
        }.mapBoth({
            logger.responseProduced(STORE_ID to storeId, RESULT to it)
            success(it, HttpStatus.CREATED)
        }, {
            logger.responseProduced(it, STORE_ID to storeId)
            failure(it)
        })
    }

    @Operation(
        summary = "Update a Customization in a child of the Offer",
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
    @PutMapping(
        "/{offerId}/options/{optionId}/customizations/{customizationId}",
        consumes = [APPLICATION_JSON_VALUE],
        produces = [APPLICATION_JSON_VALUE]
    )
    suspend fun updateCustomizationOnChildren(
        @RequestParam storeId: UUID,
        @RequestParam categoryId: UUID,
        @PathVariable offerId: UUID,
        @PathVariable optionId: UUID,
        @PathVariable customizationId: UUID,
        @RequestBody updateCustomizationRequestDTO: CustomizationRequestDTO,
    ): ResponseEntity<GenericResponseIdDTO> = withContext(MDCContext()) {
        runSuspendCatching {
            updateCustomizationRequestDTO.copy(customizationId).asEntity()
        }.andThen { customization ->
            updateCustomizationOnChildrenUseCase.execute(
                Id(storeId),
                Id(categoryId),
                Id(offerId),
                Id(optionId),
                customization
            ).map { GenericResponseIdDTO(customization.id.value.toString()) }
        }.mapBoth({
            logger.responseProduced(STORE_ID to storeId, RESULT to it)
            success(it)
        }, {
            logger.responseProduced(it, STORE_ID to storeId)
            failure(it)
        })
    }

    @Operation(
        summary = "Delete a Customization in a child of the Offer",
        description = "Returns 204 if successful"
    )
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
        "/{offerId}/options/{optionId}/customizations/{customizationId}",
        produces = [APPLICATION_JSON_VALUE]
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun removeCustomizationOnChildren(
        @RequestParam storeId: UUID,
        @RequestParam categoryId: UUID,
        @PathVariable offerId: UUID,
        @PathVariable optionId: UUID,
        @PathVariable customizationId: UUID,
    ): ResponseEntity<Unit> = withContext(MDCContext()) {
        removeCustomizationOnChildrenUseCase.execute(
            Id(storeId),
            Id(categoryId),
            Id(offerId),
            Id(optionId),
            Id(customizationId),
        ).mapBoth({
            logger.responseProduced(STORE_ID to storeId, RESULT to it)
            success(it, HttpStatus.NO_CONTENT)
        }, {
            logger.responseProduced(it, STORE_ID to storeId)
            failure(it)
        })
    }

    @Operation(
        summary = "Create an Option in a child of the Offer",
        description = "Returns 201 if successful"
    )
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
        "/{offerId}/customizations/{customizationId}/options",
        consumes = [APPLICATION_JSON_VALUE],
        produces = [APPLICATION_JSON_VALUE]
    )
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun addOptionOnChildren(
        @RequestParam storeId: UUID,
        @RequestParam categoryId: UUID,
        @PathVariable offerId: UUID,
        @PathVariable customizationId: UUID,
        @RequestBody optionRequestDTO: OptionRequestDTO,
    ): ResponseEntity<GenericResponseIdDTO> = withContext(MDCContext()) {
        runSuspendCatching {
            optionRequestDTO.asEntity()
        }.andThen { option ->
            addOptionOnChildrenUseCase.execute(
                Id(storeId),
                Id(categoryId),
                Id(offerId),
                Id(customizationId),
                option
            ).map { GenericResponseIdDTO(option.id.value.toString()) }
        }.mapBoth({
            logger.responseProduced(STORE_ID to storeId, RESULT to it)
            success(it, HttpStatus.CREATED)
        }, {
            logger.responseProduced(it, STORE_ID to storeId)
            failure(it)
        })
    }

    @Operation(
        summary = "Update an Option in a child of the Offer",
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
    @PutMapping(
        "/{offerId}/customizations/{customizationId}/options/{optionId}",
        consumes = [APPLICATION_JSON_VALUE],
        produces = [APPLICATION_JSON_VALUE]
    )
    suspend fun updateOptionOnChildren(
        @RequestParam storeId: UUID,
        @RequestParam categoryId: UUID,
        @PathVariable offerId: UUID,
        @PathVariable customizationId: UUID,
        @PathVariable optionId: UUID,
        @RequestBody updateOptionRequestDTO: OptionRequestDTO,
    ): ResponseEntity<GenericResponseIdDTO> = withContext(MDCContext()) {
        runSuspendCatching {
            updateOptionRequestDTO.copy(id = optionId).asEntity()
        }.andThen { option ->
            updateOptionOnChildrenUseCase.execute(
                Id(storeId),
                Id(categoryId),
                Id(offerId),
                Id(customizationId),
                option
            ).map { GenericResponseIdDTO(option.id.value.toString()) }
        }.mapBoth({
            logger.responseProduced(STORE_ID to storeId, RESULT to it)
            success(it)
        }, {
            logger.responseProduced(it, STORE_ID to storeId)
            failure(it)
        })
    }

    @Operation(
        summary = "Remove an Option in a child of the Offer",
        description = "Returns 204 if successful"
    )
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
        "/{offerId}/customizations/{customizationId}/options/{optionId}",
        produces = [APPLICATION_JSON_VALUE]
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun removeOptionOnChildren(
        @RequestParam storeId: UUID,
        @RequestParam categoryId: UUID,
        @PathVariable offerId: UUID,
        @PathVariable customizationId: UUID,
        @PathVariable optionId: UUID,
    ): ResponseEntity<Unit> = withContext(MDCContext()) {
        removeOptionOnChildrenUseCase.execute(
            Id(storeId),
            Id(categoryId),
            Id(offerId),
            Id(customizationId),
            Id(optionId)
        ).mapBoth({
            logger.responseProduced(STORE_ID to storeId, RESULT to it)
            success(it, HttpStatus.NO_CONTENT)
        }, {
            logger.responseProduced(it, STORE_ID to storeId)
            failure(it)
        })
    }
}
