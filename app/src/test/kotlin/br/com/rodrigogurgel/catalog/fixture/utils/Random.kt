package br.com.rodrigogurgel.catalog.fixture.utils

fun randomString(
    length: Int,
    allowedChars: String = String((('A'..'Z') + ('a'..'z') + ('0'..'9')).toCharArray()),
): String {
    return String(CharArray(length) { allowedChars.random() })
}
