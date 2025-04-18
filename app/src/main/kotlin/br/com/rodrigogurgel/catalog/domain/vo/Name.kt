package br.com.rodrigogurgel.catalog.domain.vo

import br.com.rodrigogurgel.catalog.domain.exception.NameLengthException

@JvmInline
value class Name(
    val value: String,
) {
    companion object {
        const val MIN_LENGTH = 3
        const val MAX_LENGTH = 50
    }

    init {
        if (value.length !in MIN_LENGTH..MAX_LENGTH) throw NameLengthException(value)
    }

    override fun toString(): String {
        return value
    }
}
