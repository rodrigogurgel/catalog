package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.controller

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
import br.com.rodrigogurgel.catalog.domain.vo.Status
import br.com.rodrigogurgel.catalog.fixture.mock.mockMedia
import br.com.rodrigogurgel.catalog.fixture.mock.mockOffer
import br.com.rodrigogurgel.catalog.fixture.utils.randomString
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.media.MediaResponseMapper.asResponse
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.offer.OfferResponseMapper.asResponse
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.offer.OptionRequestMapper.asEntity
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.request.common.GenericRequestIdDTO
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.request.offer.CustomizationRequestDTO
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.request.offer.OfferRequestDTO
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.request.offer.OptionRequestDTO
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.michaelbull.result.Ok
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import org.hamcrest.CoreMatchers.anything
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.math.BigDecimal
import java.util.UUID
import kotlin.random.Random

@ActiveProfiles("test")
@WebMvcTest(controllers = [OfferController::class])
class OfferControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    private val objectMapper = jacksonObjectMapper()

    @MockkBean
    private lateinit var createOfferUseCase: CreateOfferUseCase

    @MockkBean
    private lateinit var getOfferUseCase: GetOfferUseCase

    @MockkBean
    private lateinit var deleteOfferUseCase: DeleteOfferUseCase

    @MockkBean
    private lateinit var updateOfferUseCase: UpdateOfferUseCase

    @MockkBean
    private lateinit var addCustomizationUseCase: AddCustomizationUseCase

    @MockkBean
    private lateinit var updateCustomizationUseCase: UpdateCustomizationUseCase

    @MockkBean
    private lateinit var removeCustomizationUseCase: RemoveCustomizationUseCase

    @MockkBean
    private lateinit var addCustomizationOnChildrenUseCase: AddCustomizationOnChildrenUseCase

    @MockkBean
    private lateinit var updateCustomizationOnChildrenUseCase: UpdateCustomizationOnChildrenUseCase

    @MockkBean
    private lateinit var removeCustomizationOnChildrenUseCase: RemoveCustomizationOnChildrenUseCase

    @MockkBean
    private lateinit var addOptionOnChildrenUseCase: AddOptionOnChildrenUseCase

    @MockkBean
    private lateinit var updateOptionOnChildrenUseCase: UpdateOptionOnChildrenUseCase

    @MockkBean
    private lateinit var removeOptionOnChildrenUseCase: RemoveOptionOnChildrenUseCase

    @Test
    fun `Get offer`() {
        // given
        val storeId = UUID.randomUUID()
        val offer = mockOffer()
        val body = offer.asResponse()

        coEvery { getOfferUseCase.execute(Id(storeId), offer.id) } returns Ok(offer)

        // when
        val result = mockMvc.get("/offers/{offerId}", offer.id.value.toString()) {
            queryParam("storeId", storeId.toString())
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }.asyncDispatch().andDo { print() }

        // then
        result.andExpect {
            status { isOk() }
        }.andExpectAll {
            jsonPath("$.id", `is`(body.id))
            jsonPath("$.name", `is`(body.name))
            jsonPath("$.product.id", `is`(body.product?.id))
            jsonPath("$.status", `is`(body.status.name))
            jsonPath("$.price", `is`(body.price.toDouble()))
            body.customizations.forEachIndexed { customizationIndex, customization ->
                jsonPath("$.customizations[$customizationIndex].id", `is`(customization.id))
                jsonPath("$.customizations[$customizationIndex].name", `is`(customization.name))
                jsonPath("$.customizations[$customizationIndex].description", `is`(customization.description))
                jsonPath("$.customizations[$customizationIndex].minPermitted", `is`(customization.minPermitted))
                jsonPath("$.customizations[$customizationIndex].maxPermitted", `is`(customization.maxPermitted))
                jsonPath("$.customizations[$customizationIndex].status", `is`(customization.status.name))
                customization.options.forEachIndexed { optionIndex, option ->
                    jsonPath(
                        "$.customizations[$customizationIndex].options[$optionIndex].id",
                        `is`(option.id)
                    )
                    jsonPath(
                        "$.customizations[$customizationIndex].options[$optionIndex].product.id",
                        `is`(option.product?.id)
                    )
                    jsonPath(
                        "$.customizations[$customizationIndex].options[$optionIndex].minPermitted",
                        `is`(option.minPermitted)
                    )
                    jsonPath(
                        "$.customizations[$customizationIndex].options[$optionIndex].maxPermitted",
                        `is`(option.maxPermitted)
                    )
                    jsonPath(
                        "$.customizations[$customizationIndex].options[$optionIndex].price",
                        `is`(option.price.toDouble())
                    )
                    jsonPath(
                        "$.customizations[$customizationIndex].options[$optionIndex].status",
                        `is`(option.status.name)
                    )
                    option.medias.forEachIndexed { mediaIndex, media ->
                        jsonPath(
                            "$.customizations[$customizationIndex].options[$optionIndex]..medias[$mediaIndex].url",
                            `is`(media.url)
                        )
                        jsonPath(
                            "$$.customizations[$customizationIndex].options[$optionIndex]..medias[$mediaIndex].type",
                            `is`(media.type.name)
                        )
                    }
                }
            }
            body.medias.forEachIndexed { mediaIndex, media ->
                jsonPath("$.medias[$mediaIndex].url", `is`(media.url))
                jsonPath("$.medias[$mediaIndex].type", `is`(media.type.name))
            }
        }
    }

    @Test
    fun `Create Offer`() {
        // given
        val storeId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()
        val body = OfferRequestDTO(
            name = randomString(30),
            description = randomString(1000),
            product = GenericRequestIdDTO(UUID.randomUUID()),
            price = BigDecimal.ONE,
            status = Status.AVAILABLE,
            customizations = listOf(
                CustomizationRequestDTO(
                    id = null,
                    name = randomString(50),
                    description = randomString(1000),
                    minPermitted = 0,
                    maxPermitted = 1,
                    status = Status.AVAILABLE,
                    options = listOf(
                        OptionRequestDTO(
                            id = null,
                            name = randomString(50),
                            description = randomString(1000),
                            product = GenericRequestIdDTO(UUID.randomUUID()),
                            price = BigDecimal.ONE,
                            minPermitted = 0,
                            maxPermitted = 1,
                            status = Status.AVAILABLE,
                            customizations = listOf(
                                CustomizationRequestDTO(
                                    id = null,
                                    name = randomString(50),
                                    description = randomString(1000),
                                    minPermitted = 0,
                                    maxPermitted = 1,
                                    status = Status.AVAILABLE,
                                    options = listOf(
                                        OptionRequestDTO(
                                            id = null,
                                            name = randomString(50),
                                            description = randomString(1000),
                                            product = GenericRequestIdDTO(UUID.randomUUID()),
                                            price = BigDecimal.ONE,
                                            minPermitted = 0,
                                            maxPermitted = 1,
                                            status = Status.AVAILABLE,
                                            customizations = listOf(),
                                            medias = emptyList()
                                        ),
                                    )
                                )
                            ),
                            medias = emptyList(),
                        )
                    )
                )
            ),
            medias = emptyList()
        )

        coEvery { createOfferUseCase.execute(Id(storeId), Id(categoryId), any()) } returns Ok(Unit)

        // when
        val result = mockMvc.post("/offers") {
            queryParam("storeId", storeId.toString())
            queryParam("categoryId", categoryId.toString())
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(body)
        }.asyncDispatch().andDo { print() }

        // then
        result.andExpect {
            status { isCreated() }
        }.andExpectAll {
            jsonPath("$.id", anything())
        }
    }

    @Test
    fun `Create Offer with optional values`() {
        // given
        val storeId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()
        val body = OfferRequestDTO(
            name = randomString(30),
            description = null,
            product = GenericRequestIdDTO(UUID.randomUUID()),
            price = BigDecimal.ONE,
            status = Status.AVAILABLE,
            customizations = listOf(
                CustomizationRequestDTO(
                    id = null,
                    name = randomString(50),
                    description = null,
                    minPermitted = 0,
                    maxPermitted = 1,
                    status = Status.AVAILABLE,
                    options = listOf(
                        OptionRequestDTO(
                            id = null,
                            name = randomString(50),
                            description = null,
                            product = GenericRequestIdDTO(UUID.randomUUID()),
                            price = BigDecimal.ONE,
                            minPermitted = 0,
                            maxPermitted = 1,
                            status = Status.AVAILABLE,
                            medias = emptyList()
                        )
                    )
                )
            ),
            medias = emptyList()
        )

        coEvery { createOfferUseCase.execute(Id(storeId), Id(categoryId), any()) } returns Ok(Unit)

        // when
        val result = mockMvc.post("/offers") {
            queryParam("storeId", storeId.toString())
            queryParam("categoryId", categoryId.toString())
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(body)
        }.asyncDispatch().andDo { print() }

        // then
        result.andExpect {
            status { isCreated() }
        }.andExpectAll {
            jsonPath("$.id", anything())
        }
    }

    @Test
    fun `Create Offer without customizations`() {
        // given
        val storeId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()
        val body = OfferRequestDTO(
            name = randomString(30),
            description = randomString(1000),
            product = GenericRequestIdDTO(UUID.randomUUID()),
            price = BigDecimal.ONE,
            status = Status.AVAILABLE,
            medias = emptyList()
        )

        coEvery { createOfferUseCase.execute(Id(storeId), Id(categoryId), any()) } returns Ok(Unit)

        // when
        val result = mockMvc.post("/offers") {
            queryParam("storeId", storeId.toString())
            queryParam("categoryId", categoryId.toString())
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(body)
        }.asyncDispatch().andDo { print() }

        // then
        result.andExpect {
            status { isCreated() }
        }.andExpectAll {
            jsonPath("$.id", anything())
        }
    }

    @Test
    fun `Update Offer`() {
        // given
        val storeId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()
        val offerId = UUID.randomUUID()
        val body = OfferRequestDTO(
            name = randomString(30),
            description = randomString(1000),
            product = GenericRequestIdDTO(UUID.randomUUID()),
            price = BigDecimal.ONE,
            status = Status.AVAILABLE,
            customizations = listOf(
                CustomizationRequestDTO(
                    id = null,
                    name = randomString(50),
                    description = randomString(1000),
                    minPermitted = 0,
                    maxPermitted = 1,
                    status = Status.AVAILABLE,
                    options = listOf(
                        OptionRequestDTO(
                            id = null,
                            name = randomString(50),
                            description = randomString(1000),
                            product = GenericRequestIdDTO(UUID.randomUUID()),
                            price = BigDecimal.ONE,
                            minPermitted = 0,
                            maxPermitted = 1,
                            status = Status.AVAILABLE,
                            customizations = listOf(
                                CustomizationRequestDTO(
                                    id = null,
                                    name = randomString(50),
                                    description = randomString(1000),
                                    minPermitted = 0,
                                    maxPermitted = 1,
                                    status = Status.AVAILABLE,
                                    options = listOf(
                                        OptionRequestDTO(
                                            id = null,
                                            name = randomString(50),
                                            description = randomString(1000),
                                            product = GenericRequestIdDTO(UUID.randomUUID()),
                                            price = BigDecimal.ONE,
                                            minPermitted = 0,
                                            maxPermitted = 1,
                                            status = Status.AVAILABLE,
                                            customizations = listOf(),
                                            medias = emptyList()
                                        )
                                    )
                                )
                            ),
                            medias = emptyList()
                        )
                    )
                )
            ),
            medias = emptyList()
        )

        coEvery { updateOfferUseCase.execute(Id(storeId), Id(categoryId), any()) } returns Ok(Unit)

        // when
        val result = mockMvc.put("/offers/{offerId}", offerId.toString()) {
            queryParam("storeId", storeId.toString())
            queryParam("categoryId", categoryId.toString())
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(body)
        }.asyncDispatch().andDo { print() }

        // then
        result.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `Delete Offer`() {
        // given
        val storeId = UUID.randomUUID()
        val offerId = UUID.randomUUID()

        coEvery { deleteOfferUseCase.execute(Id(storeId), Id(offerId)) } returns Ok(Unit)

        // when
        val result = mockMvc.delete("/offers/{offerId}", offerId.toString()) {
            queryParam("storeId", storeId.toString())
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }.asyncDispatch().andDo { print() }

        // then
        result.andExpect {
            status { isNoContent() }
        }
    }

    @Test
    fun `Add Customization`() {
        // given
        val storeId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()
        val offerId = UUID.randomUUID()
        val body = CustomizationRequestDTO(
            id = null,
            name = randomString(50),
            description = null,
            minPermitted = 0,
            maxPermitted = 1,
            status = Status.AVAILABLE,
            options = listOf(
                OptionRequestDTO(
                    id = null,
                    name = randomString(50),
                    description = null,
                    product = GenericRequestIdDTO(UUID.randomUUID()),
                    price = BigDecimal.ONE,
                    minPermitted = 0,
                    maxPermitted = 1,
                    status = Status.AVAILABLE,
                    customizations = null,
                    medias = emptyList()
                )
            )
        )

        coEvery {
            addCustomizationUseCase.execute(Id(storeId), any(), Id(offerId), any())
        } returns Ok(Unit)

        // when
        val result = mockMvc.post("/offers/{offerId}/customizations", offerId.toString()) {
            queryParam("storeId", storeId.toString())
            queryParam("categoryId", categoryId.toString())
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(body)
        }.asyncDispatch().andDo { print() }

        // then
        result.andExpect {
            status { isCreated() }
        }
    }

    @Test
    fun `Update Customization`() {
        // given
        val storeId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()
        val offerId = UUID.randomUUID()
        val customizationId = UUID.randomUUID()
        val body = CustomizationRequestDTO(
            id = null,
            name = randomString(50),
            description = randomString(1000),
            minPermitted = 0,
            maxPermitted = 1,
            status = Status.AVAILABLE,
            options = listOf(
                OptionRequestDTO(
                    id = null,
                    name = randomString(50),
                    description = randomString(1000),
                    product = GenericRequestIdDTO(UUID.randomUUID()),
                    price = BigDecimal.ONE,
                    minPermitted = 0,
                    maxPermitted = 1,
                    status = Status.AVAILABLE,
                    customizations = null,
                    medias = emptyList()
                )
            )
        )

        coEvery {
            updateCustomizationUseCase.execute(Id(storeId), any(), Id(offerId), any())
        } returns Ok(Unit)

        // when
        val result = mockMvc.put(
            "/offers/{offerId}/customizations/{customizationId}",
            offerId.toString(),
            customizationId.toString()
        ) {
            queryParam("storeId", storeId.toString())
            queryParam("categoryId", categoryId.toString())
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(body)
        }.asyncDispatch().andDo { print() }

        // then
        result.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `Remove Customization`() {
        // given
        val storeId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()
        val offerId = UUID.randomUUID()
        val customizationId = UUID.randomUUID()

        coEvery { removeCustomizationUseCase.execute(Id(storeId), any(), Id(offerId), Id(customizationId)) } returns Ok(Unit)

        // when
        val result = mockMvc.delete(
            "/offers/{offerId}/customizations/{customizationId}",
            offerId.toString(),
            customizationId.toString()
        ) {
            queryParam("storeId", storeId.toString())
            queryParam("categoryId", categoryId.toString())
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }.asyncDispatch().andDo { print() }

        // then
        result.andExpect { status { isNoContent() } }
    }

    @Test
    fun `Add Customization on Children`() {
        // given
        val storeId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()
        val offerId = UUID.randomUUID()
        val optionId = UUID.randomUUID()
        val body = CustomizationRequestDTO(
            id = null,
            name = randomString(50),
            description = null,
            minPermitted = 0,
            maxPermitted = 1,
            status = Status.AVAILABLE,
            options = listOf(
                OptionRequestDTO(
                    id = null,
                    name = randomString(50),
                    description = randomString(1000),
                    product = GenericRequestIdDTO(UUID.randomUUID()),
                    price = BigDecimal.ONE,
                    minPermitted = 0,
                    maxPermitted = 1,
                    status = Status.AVAILABLE,
                    customizations = null,
                    medias = emptyList()
                )
            )
        )

        coEvery {
            addCustomizationOnChildrenUseCase.execute(Id(storeId), any(), Id(offerId), Id(optionId), any())
        } returns Ok(Unit)

        // when
        val result = mockMvc.post(
            "/offers/{offerId}/options/{optionId}/customizations",
            offerId.toString(),
            optionId.toString()
        ) {
            queryParam("storeId", storeId.toString())
            queryParam("categoryId", categoryId.toString())
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(body)
        }.asyncDispatch().andDo { print() }

        // then
        result.andExpect {
            status { isCreated() }
        }
    }

    @Test
    fun `Update Customization on Children`() {
        // given
        val storeId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()
        val offerId = UUID.randomUUID()
        val optionId = UUID.randomUUID()
        val customizationId = UUID.randomUUID()
        val body = CustomizationRequestDTO(
            id = null,
            name = randomString(50),
            description = randomString(1000),
            minPermitted = 0,
            maxPermitted = 1,
            status = Status.AVAILABLE,
            options = listOf(
                OptionRequestDTO(
                    id = null,
                    name = randomString(50),
                    description = randomString(1000),
                    product = GenericRequestIdDTO(UUID.randomUUID()),
                    price = BigDecimal.ONE,
                    minPermitted = 0,
                    maxPermitted = 1,
                    status = Status.AVAILABLE,
                    customizations = null,
                    medias = emptyList()
                )
            )
        )

        coEvery {
            updateCustomizationOnChildrenUseCase.execute(
                Id(storeId),
                any(),
                Id(offerId),
                Id(optionId),
                any()
            )
        } returns Ok(Unit)

        // when
        val result = mockMvc.put(
            "/offers/{offerId}/options/{optionId}/customizations/{customizationId}",
            offerId.toString(),
            optionId.toString(),
            customizationId.toString()
        ) {
            queryParam("storeId", storeId.toString())
            queryParam("categoryId", categoryId.toString())
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(body)
        }.asyncDispatch().andDo { print() }

        // then
        result.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `Remove Customization on Children`() {
        // given
        val storeId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()
        val offerId = UUID.randomUUID()
        val optionId = UUID.randomUUID()
        val customizationId = UUID.randomUUID()

        coEvery {
            removeCustomizationOnChildrenUseCase.execute(
                Id(storeId),
                any(),
                Id(offerId),
                Id(optionId),
                Id(customizationId)
            )
        } returns Ok(Unit)

        // when
        val result = mockMvc.delete(
            "/offers/{offerId}/options/{optionId}/customizations/{customizationId}",
            offerId.toString(),
            optionId.toString(),
            customizationId.toString()
        ) {
            queryParam("storeId", storeId.toString())
            queryParam("categoryId", categoryId.toString())
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }.asyncDispatch().andDo { print() }

        // then
        result.andExpect { status { isNoContent() } }
    }

    @Test
    fun `Add Option`() {
        // given
        val storeId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()
        val offerId = UUID.randomUUID()
        val customizationId = UUID.randomUUID()
        val body = OptionRequestDTO(
            id = UUID.randomUUID(),
            name = randomString(50),
            description = randomString(1000),
            product = GenericRequestIdDTO(UUID.randomUUID()),
            price = BigDecimal.ONE,
            minPermitted = 0,
            maxPermitted = 1,
            status = Status.AVAILABLE,
            medias = emptyList()
        )

        coEvery {
            addOptionOnChildrenUseCase.execute(Id(storeId), any(), Id(offerId), Id(customizationId), body.asEntity())
        } returns Ok(Unit)

        // when
        val result = mockMvc.post(
            "/offers/{offerId}/customizations/{customizationId}/options",
            offerId.toString(),
            customizationId.toString()
        ) {
            queryParam("storeId", storeId.toString())
            queryParam("categoryId", categoryId.toString())
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(body)
        }.asyncDispatch().andDo { print() }

        // then
        result.andExpect {
            status { isCreated() }
        }
    }

    @Test
    fun `Update Option`() {
        // given
        val storeId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()
        val offerId = UUID.randomUUID()
        val customizationId = UUID.randomUUID()
        val optionId = UUID.randomUUID()
        val body = OptionRequestDTO(
            id = null,
            name = randomString(50),
            description = randomString(1000),
            product = GenericRequestIdDTO(UUID.randomUUID()),
            price = BigDecimal.ONE,
            minPermitted = 0,
            maxPermitted = 1,
            status = Status.AVAILABLE,
            customizations = listOf(
                CustomizationRequestDTO(
                    id = null,
                    name = randomString(50),
                    description = randomString(1000),
                    minPermitted = 0,
                    maxPermitted = 1,
                    status = Status.AVAILABLE,
                    options = listOf(
                        OptionRequestDTO(
                            id = null,
                            name = randomString(50),
                            description = randomString(1000),
                            product = GenericRequestIdDTO(UUID.randomUUID()),
                            price = BigDecimal.ONE,
                            minPermitted = 0,
                            maxPermitted = 1,
                            status = Status.AVAILABLE,
                            customizations = null,
                            medias = List(Random.nextInt(0, 11)) {
                                mockMedia().asResponse()
                            },
                        )
                    )
                )
            ),
            medias = emptyList()
        )

        coEvery {
            updateOptionOnChildrenUseCase.execute(
                Id(storeId),
                any(),
                Id(offerId),
                Id(customizationId),
                any()
            )
        } returns Ok(Unit)

        // when
        val result = mockMvc.put(
            "/offers/{offerId}/customizations/{customizationId}/options/{optionId}",
            offerId.toString(),
            customizationId.toString(),
            optionId.toString()
        ) {
            queryParam("storeId", storeId.toString())
            queryParam("categoryId", categoryId.toString())
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(body)
        }.asyncDispatch().andDo { print() }

        // then
        result.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `Remove Option on Children`() {
        // given
        val storeId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()
        val offerId = UUID.randomUUID()
        val customizationId = UUID.randomUUID()
        val optionId = UUID.randomUUID()

        coEvery {
            removeOptionOnChildrenUseCase.execute(Id(storeId), any(), Id(offerId), Id(customizationId), Id(optionId))
        } returns Ok(Unit)

        // when
        val result = mockMvc.delete(
            "/offers/{offerId}/customizations/{customizationId}/options/{optionId}",
            offerId.toString(),
            customizationId.toString(),
            optionId.toString()
        ) {
            queryParam("storeId", storeId.toString())
            queryParam("categoryId", categoryId.toString())
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }.asyncDispatch().andDo { print() }

        // then
        result.andExpect { status { isNoContent() } }
    }
}
