package br.com.rodrigogurgel.catalog.common.logger.extensions

import net.logstash.logback.marker.Markers.appendEntries
import org.slf4j.Logger
import org.slf4j.Marker

const val CATEGORY = "category"
const val CATEGORY_ID = "category_id"
const val CUSTOMIZATION = "customization"
const val CUSTOMIZATION_ID = "customization_id"
const val LIMIT = "limit"
const val OFFER = "offer"
const val OFFER_ID = "offer_id"
const val CURSOR = "cursor"
const val PRODUCT = "product"
const val PRODUCT_ID = "product_id"
const val PRODUCT_IDS = "product_ids"
const val OPTION = "option"
const val OPTION_ID = "option_id"
const val RESULT = "result"
const val STORE_ID = "store_id"

private const val CONTENT = "content"

private fun buildMarker(vararg values: Pair<String, Any?>): Marker {
    return appendEntries(mutableMapOf(*values))
}

fun Logger.success(action: String, vararg content: Pair<String, Any?>) {
    val marker = buildMarker(CONTENT to mapOf(*content))
    info(marker, "$action completed successfully.")
}

fun Logger.failure(action: String, throwable: Throwable, vararg content: Pair<String, Any?>) {
    val marker = buildMarker(CONTENT to mapOf(*content))
    error(marker, "Failed to ${action.lowercase()}.", throwable)
}

fun Logger.requestReceived(vararg content: Pair<String, Any?>) {
    val marker = buildMarker(CONTENT to mapOf(*content))
    Long.MAX_VALUE
    info(marker, "Request received.")
}

fun Logger.responseProduced(vararg content: Pair<String, Any?>) {
    val marker = buildMarker(CONTENT to mapOf(*content))
    info(marker, "Response produced.")
}

fun Logger.responseProduced(throwable: Throwable, vararg content: Pair<String, Any?>) {
    val marker = buildMarker(CONTENT to mapOf(*content))
    error(marker, "Response produced.", throwable)
}
