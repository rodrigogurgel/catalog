package br.com.rodrigogurgel.catalog.domain.vo

import java.util.UUID

@JvmInline
value class Id(
    val value: UUID,
) {
    override fun toString(): String {
        return value.toString()
    }
}
