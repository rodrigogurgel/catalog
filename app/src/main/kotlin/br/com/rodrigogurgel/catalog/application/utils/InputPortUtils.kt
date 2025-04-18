package br.com.rodrigogurgel.catalog.application.utils

private const val MIN_LIMIT_VALUE = 0
private const val MAX_LIMIT_VALUE = 20
private const val MIN_OFFSET_VALUE = 0

fun normalizeLimit(limit: Int): Int = limit.coerceIn(MIN_LIMIT_VALUE, MAX_LIMIT_VALUE)

fun normalizeOffset(offset: Int): Int = offset.coerceAtLeast(MIN_OFFSET_VALUE)
