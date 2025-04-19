package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.controller

import br.com.rodrigogurgel.catalog.domain.usecase.category.CountCategoriesUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.category.CreateCategoryUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.category.DeleteCategoryUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.category.GetCategoriesUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.category.GetCategoryUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.category.UpdateCategoryUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.offer.CountOffersUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.offer.GetOffersUseCase
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.domain.vo.Status
import br.com.rodrigogurgel.catalog.fixture.mock.mockCategory
import br.com.rodrigogurgel.catalog.fixture.mock.mockCustomization
import br.com.rodrigogurgel.catalog.fixture.mock.mockOfferWith
import br.com.rodrigogurgel.catalog.fixture.utils.randomString
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.category.CategoryRequestMapper.asEntity
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.category.CategoryResponseMapper.asResponse
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.offer.OfferResponseMapper.asResponse
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.request.category.CategoryRequestDTO
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.michaelbull.result.Ok
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import org.hamcrest.CoreMatchers
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
import java.util.UUID

@ActiveProfiles("test")
@WebMvcTest(controllers = [CategoryController::class])
class CategoryControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    private val objectMapper = jacksonObjectMapper()

    @MockkBean
    private lateinit var getCategoryUseCase: GetCategoryUseCase

    @MockkBean
    private lateinit var getCategoriesUseCase: GetCategoriesUseCase

    @MockkBean
    private lateinit var createCategoryUseCase: CreateCategoryUseCase

    @MockkBean
    private lateinit var updateCategoryUseCase: UpdateCategoryUseCase

    @MockkBean
    private lateinit var deleteCategoryUseCase: DeleteCategoryUseCase

    @MockkBean
    private lateinit var countCategoriesUseCase: CountCategoriesUseCase

    @MockkBean
    private lateinit var countOffersUseCase: CountOffersUseCase

    @MockkBean
    private lateinit var getOffersUseCase: GetOffersUseCase

    @Test
    fun `Get Category`() {
        // given
        val storeId = UUID.randomUUID()
        val category = mockCategory()

        coEvery { getCategoryUseCase.execute(Id(storeId), category.id) } returns Ok(category)

        // when
        val result = mockMvc.get("/categories/{categoryId}", category.id.value.toString()) {
            queryParam("storeId", storeId.toString())
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }.asyncDispatch().andDo { print() }

        // then
        result.andExpect {
            status { isOk() }
        }.andExpectAll {
            jsonPath("$.id", CoreMatchers.`is`(category.id.value.toString()))
            jsonPath("$.name", CoreMatchers.`is`(category.name.value))
            jsonPath("$.description", CoreMatchers.`is`(category.description?.value))
            jsonPath("$.status", CoreMatchers.`is`(category.status.name))
        }
    }

    @Test
    fun `Create Category`() {
        // given
        val storeId = UUID.randomUUID()
        val body = CategoryRequestDTO(
            name = randomString(30),
            description = randomString(1000),
            status = Status.AVAILABLE
        )

        coEvery { createCategoryUseCase.execute(Id(storeId), any()) } returns Ok(Unit)

        // when
        val result = mockMvc.post("/categories") {
            queryParam("storeId", storeId.toString())
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(body)
        }.asyncDispatch().andDo { print() }

        // then
        result.andExpect {
            status { isCreated() }
        }.andExpectAll {
            jsonPath("$.id", CoreMatchers.anything())
        }
    }

    @Test
    fun `Create Category with optional values`() {
        // given
        val storeId = UUID.randomUUID()
        val body = CategoryRequestDTO(
            name = randomString(30),
            description = null,
            status = Status.AVAILABLE
        )

        coEvery { createCategoryUseCase.execute(Id(storeId), any()) } returns Ok(Unit)

        // when
        val result = mockMvc.post("/categories") {
            queryParam("storeId", storeId.toString())
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(body)
        }.asyncDispatch().andDo { print() }

        // then
        result.andExpect {
            status { isCreated() }
        }.andExpectAll {
            jsonPath("$.id", CoreMatchers.anything())
        }
    }

    @Test
    fun `Update Category`() {
        // given
        val storeId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()
        val body = CategoryRequestDTO(
            name = randomString(30),
            description = randomString(1000),
            status = Status.AVAILABLE
        )

        coEvery { updateCategoryUseCase.execute(Id(storeId), any()) } returns Ok(Unit)

        // when
        val result = mockMvc.put("/categories/{id}", categoryId) {
            queryParam("storeId", storeId.toString())
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
    fun `Update Category with optional values`() {
        // given
        val storeId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()
        val body = CategoryRequestDTO(
            name = randomString(30),
            description = null,
            status = Status.AVAILABLE
        )
        val category = body.asEntity(categoryId)
        coEvery { updateCategoryUseCase.execute(Id(storeId), category) } returns Ok(Unit)

        // when
        val result = mockMvc.put("/categories/{id}", categoryId) {
            queryParam("storeId", storeId.toString())
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
    fun `Delete Category`() {
        // given
        val storeId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()

        coEvery { deleteCategoryUseCase.execute(Id(storeId), Id(categoryId)) } returns Ok(Unit)

        // when
        val result = mockMvc.delete("/categories/{id}", categoryId) {
            queryParam("storeId", storeId.toString())
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }.asyncDispatch().andDo { print() }

        result.andExpect {
            status { isNoContent() }
        }
    }

    @Test
    fun `Get Page of Categories`() {
        // given
        val storeId = UUID.randomUUID()
        val categories = List(10) {
            mockCategory()
        }.sortedBy { it.name.value }

        coEvery { countCategoriesUseCase.execute(Id(storeId)) } returns Ok(categories.size.toLong())
        coEvery { getCategoriesUseCase.execute(Id(storeId), 20, null) } returns Ok(null to categories)

        // when
        val result = mockMvc.get("/categories") {
            queryParam("storeId", storeId.toString())
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }.asyncDispatch().andDo { print() }

        // when
        result.andExpect {
            status { isOk() }
        }.andExpectAll {
            jsonPath("$.limit", CoreMatchers.`is`(20))
            jsonPath("$.total", CoreMatchers.`is`(10))
            categories.map {
                it.asResponse()
            }.sortedBy { body -> body.name }.forEachIndexed { index, body ->
                jsonPath("$.data[$index].id", CoreMatchers.`is`(body.id))
                jsonPath("$.data[$index].name", CoreMatchers.`is`(body.name))
                jsonPath("$.data[$index].description", CoreMatchers.`is`(body.description))
                jsonPath("$.data[$index].status", CoreMatchers.`is`(body.status.name))
            }
        }
    }

    @Test
    fun `Get Offers by Category Id`() {
        // given
        val storeId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()
        val offers = List(10) {
            mockOfferWith {
                customizations = mutableListOf(mockCustomization(), mockCustomization())
            }
        }.sortedBy { it.name.value }

        coEvery { getOffersUseCase.execute(Id(storeId), Id(categoryId), 20, 0) } returns Ok(offers)
        coEvery { countOffersUseCase.execute(Id(storeId), Id(categoryId)) } returns Ok(offers.size.toLong())

        // when
        val result = mockMvc.get("/categories/{categoryId}/offers", categoryId.toString()) {
            queryParam("storeId", storeId.toString())
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }.asyncDispatch().andDo { print() }

        result.andExpect {
            status { isOk() }
        }.andExpectAll {
            jsonPath("$.limit", CoreMatchers.`is`(20))
            jsonPath("$.total", CoreMatchers.`is`(10))
            offers.map { it.asResponse() }.sortedBy { body -> body.name }.forEachIndexed { index, offer ->
                jsonPath("$.data[$index].id", CoreMatchers.`is`(offer.id))
                jsonPath("$.data[$index].name", CoreMatchers.`is`(offer.name))
                jsonPath("$.data[$index].product.id", CoreMatchers.`is`(offer.product?.id))
                jsonPath("$.data[$index].status", CoreMatchers.`is`(offer.status.name))
                jsonPath("$.data[$index].price", CoreMatchers.`is`(offer.price.toDouble()))
                offer.customizations.forEachIndexed { customizationIndex, customization ->
                    jsonPath(
                        "$.data[$index].customizations[$customizationIndex].id",
                        CoreMatchers.`is`(customization.id)
                    )
                    jsonPath(
                        "$.data[$index].customizations[$customizationIndex].name",
                        CoreMatchers.`is`(customization.name)
                    )
                    jsonPath(
                        "$.data[$index].customizations[$customizationIndex].description",
                        CoreMatchers.`is`(customization.description)
                    )
                    jsonPath(
                        "$.data[$index].customizations[$customizationIndex].minPermitted",
                        CoreMatchers.`is`(customization.minPermitted)
                    )
                    jsonPath(
                        "$.data[$index].customizations[$customizationIndex].maxPermitted",
                        CoreMatchers.`is`(customization.maxPermitted)
                    )
                    jsonPath(
                        "$.data[$index].customizations[$customizationIndex].status",
                        CoreMatchers.`is`(customization.status.name)
                    )
                    customization.options.forEachIndexed { optionIndex, option ->
                        jsonPath(
                            "$.data[$index].customizations[$customizationIndex].options[$optionIndex].id",
                            CoreMatchers.`is`(option.id)
                        )
                        jsonPath(
                            "$.data[$index].customizations[$customizationIndex].options[$optionIndex].product.id",
                            CoreMatchers.`is`(option.product?.id)
                        )
                        jsonPath(
                            "$.data[$index].customizations[$customizationIndex].options[$optionIndex].minPermitted",
                            CoreMatchers.`is`(option.minPermitted)
                        )
                        jsonPath(
                            "$.data[$index].customizations[$customizationIndex].options[$optionIndex].maxPermitted",
                            CoreMatchers.`is`(option.maxPermitted)
                        )
                        jsonPath(
                            "$.data[$index].customizations[$customizationIndex].options[$optionIndex].price",
                            CoreMatchers.`is`(option.price.toDouble())
                        )
                        jsonPath(
                            "$.data[$index].customizations[$customizationIndex].options[$optionIndex].status",
                            CoreMatchers.`is`(option.status.name)
                        )
                    }
                }
            }
        }
    }
}
