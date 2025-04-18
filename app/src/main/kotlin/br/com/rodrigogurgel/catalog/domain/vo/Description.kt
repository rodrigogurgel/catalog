package br.com.rodrigogurgel.catalog.domain.vo

import br.com.rodrigogurgel.catalog.domain.exception.DescriptionLengthException

@JvmInline
value class Description(
    val value: String,
) {
    companion object {
        const val MIN_LENGTH = 3
        const val MAX_LENGTH = 1000
    }

    init {
        if (value.length !in MIN_LENGTH..MAX_LENGTH) throw DescriptionLengthException(value)
    }

    override fun toString(): String {
        return value
    }
}
