package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.util.Base64

object CursorUtils {
    private val mapper: ObjectMapper = jacksonObjectMapper()

    private fun AttributeValue.toAttributeValueString(): String {
        return this.s() ?: this.n() ?: this.bool()?.toString() ?: ""
    }

    fun encode(key: Map<String, AttributeValue>): String {
        val serializableMap = key.mapValues { it.value.toAttributeValueString() }
        val json = mapper.writeValueAsString(serializableMap)
        return Base64.getUrlEncoder().encodeToString(json.toByteArray())
    }

    fun decode(cursor: String): Map<String, AttributeValue> {
        val decoded = String(Base64.getUrlDecoder().decode(cursor))
        val stringMap = mapper.readValue<Map<String, String>>(decoded)
        return stringMap.mapValues { AttributeValue.builder().s(it.value).build() }
    }
}
