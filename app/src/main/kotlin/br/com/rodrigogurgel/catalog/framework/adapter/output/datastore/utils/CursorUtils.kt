package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.util.Base64

object CursorUtils {
    private val mapper: ObjectMapper = jacksonObjectMapper()

    private fun encode(value: Int): String {
        val json = mapper.writeValueAsString(value)
        return Base64.getUrlEncoder().encodeToString(json.toByteArray())
    }

    fun nextCursor(total: Int, limit: Int, cursor: String?): String? {
        val cursorValue = cursor?.let { CursorUtils.decode(cursor) } ?: 0
        val nextCursor = cursorValue + 1
        return if (total > limit) {
            CursorUtils.encode(nextCursor)
        } else {
            null
        }
    }

    fun decode(cursor: String): Int {
        return if (cursor.isBlank()) {
            0
        } else {
            val decoded = String(Base64.getUrlDecoder().decode(cursor))
            mapper.readValue<Int>(decoded)
        }
    }
}
