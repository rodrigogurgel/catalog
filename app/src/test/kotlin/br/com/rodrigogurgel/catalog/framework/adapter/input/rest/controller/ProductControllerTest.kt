package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.controller

import br.com.rodrigogurgel.catalog.domain.usecase.product.CountProductsUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.product.CreateProductUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.product.DeleteProductUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.product.GetProductUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.product.GetProductsUseCase
import br.com.rodrigogurgel.catalog.domain.usecase.product.UpdateProductUseCase
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.fixture.mock.mockProduct
import br.com.rodrigogurgel.catalog.fixture.utils.randomString
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.product.ProductRequestMapper.asEntity
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.product.ProductResponseMapper.asResponse
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.request.product.ProductRequestDTO
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.michaelbull.result.Ok
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.isA
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
@WebMvcTest(controllers = [ProductController::class])
class ProductControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var createProductUseCase: CreateProductUseCase

    @MockkBean
    private lateinit var updateProductUseCase: UpdateProductUseCase

    @MockkBean
    private lateinit var getProductUseCase: GetProductUseCase

    @MockkBean
    private lateinit var deleteProductUseCase: DeleteProductUseCase

    @MockkBean
    private lateinit var getProductsUseCase: GetProductsUseCase

    @MockkBean
    private lateinit var countProductsUseCase: CountProductsUseCase

    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    @Test
    fun `Create Product`() {
        // given
        val storeId = UUID.randomUUID()
        val body = ProductRequestDTO(
            name = randomString(30),
            description = randomString(1000),
            medias = emptyList()
        )

        coEvery { createProductUseCase.execute(Id(storeId), any()) } returns Ok(Unit)

        // when
        val result = mockMvc.post(
            "/products",
        ) {
            queryParam("storeId", storeId.toString())
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(body)
        }.asyncDispatch().andDo { print() }

        // then
        result.andExpect {
            status { isCreated() }
        }.andExpectAll {
            jsonPath("$.id", isA(String::class.java))
        }
    }

    @Test
    fun `Create Product with optional values`() {
        // given
        val storeId = UUID.randomUUID()
        val body = ProductRequestDTO(
            name = randomString(30),
            description = null,
            medias = emptyList()
        )

        coEvery { createProductUseCase.execute(Id(storeId), any()) } returns Ok(Unit)

        // when
        val result = mockMvc.post(
            "/products",
        ) {
            queryParam("storeId", storeId.toString())
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(body)
        }.asyncDispatch().andDo { print() }

        // then
        result.andExpect {
            status { isCreated() }
        }.andExpectAll {
            jsonPath("$.id", isA(String::class.java))
        }
    }

    @Test
    fun `Get Product endpoint`() {
        // given
        val storeId = UUID.randomUUID()
        val product = mockProduct()

        coEvery { getProductUseCase.execute(Id(storeId), product.id) } returns Ok(product)

        // when
        val result = mockMvc.get("/products/{productId}", product.id.value.toString()) {
            queryParam("storeId", storeId.toString())
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }.asyncDispatch().andDo { print() }

        // then
        result.andExpect {
            status { isOk() }
        }.andExpect {
            jsonPath("$.id", `is`(product.id.value.toString()))
            jsonPath("$.name", `is`(product.name.value))
            jsonPath("$.description", `is`(product.description?.value))
            product.medias.forEachIndexed { index, media ->
                jsonPath("$.medias[$index].url", `is`(media.url))
                jsonPath("$.medias[$index].type", `is`(media.type.name))
            }
        }
    }

    @Test
    fun `Update Product`() {
        // given
        val storeId = UUID.randomUUID()
        val productId = UUID.randomUUID()
        val body = ProductRequestDTO(
            name = randomString(30),
            description = randomString(1000),
            medias = emptyList()
        )

        coEvery { updateProductUseCase.execute(Id(storeId), any()) } returns Ok(Unit)

        // when
        val result = mockMvc.put(
            "/products/{id}",
            productId.toString()
        ) {
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
    fun `Update Product with optional values`() {
        // given
        val storeId = UUID.randomUUID()
        val productId = UUID.randomUUID()
        val body = ProductRequestDTO(
            name = randomString(30),
            description = null,
            medias = emptyList()
        )

        coEvery { updateProductUseCase.execute(Id(storeId), body.asEntity(productId)) } returns Ok(Unit)

        // when
        val result = mockMvc.put(
            "/products/{id}",
            productId.toString()
        ) {
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
    fun `Delete Product`() {
        // given
        val storeId = UUID.randomUUID()
        val productId = UUID.randomUUID()

        coEvery { deleteProductUseCase.execute(Id(storeId), Id(productId)) } returns Ok(Unit)

        // when
        val result = mockMvc.delete(
            "/products/{id}",
            productId.toString()
        ) {
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
    fun `Get Page of Products`() {
        val storeId = UUID.randomUUID()
        val products = List(10) {
            mockProduct()
        }.sortedBy { body -> body.name.value }

        coEvery { countProductsUseCase.execute(Id(storeId)) } returns Ok(products.size.toLong())
        coEvery { getProductsUseCase.execute(Id(storeId), 20, 0) } returns Ok(products)

        // when
        mockMvc.get("/products") {
            queryParam("storeId", storeId.toString())
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }.asyncDispatch()
            .andDo { print() }
            .andExpect {
                status { isOk() }
            }.andExpectAll {
                products
                    .map { it.asResponse() }
                    .forEachIndexed { productIndex, body ->
                        jsonPath("$.data[$productIndex].id", `is`(body.id))
                        jsonPath("$.data[$productIndex].name", `is`(body.name))
                        jsonPath("$.data[$productIndex].description", `is`(body.description))
                        body.medias.forEachIndexed { mediaIndex, media ->
                            jsonPath("$.data[$productIndex].medias[$mediaIndex].url", `is`(media.url))
                            jsonPath("$.data[$productIndex].medias[$mediaIndex].type", `is`(media.type.name))
                        }
                    }
            }
    }
}
