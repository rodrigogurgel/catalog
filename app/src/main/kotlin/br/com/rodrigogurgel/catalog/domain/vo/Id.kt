package br.com.rodrigogurgel.catalog.domain.vo

import java.util.UUID

data class Id(
    val value: UUID,
) {
    constructor() : this(
        UUID.randomUUID(),
    )

    override fun toString(): String {
        return value.toString()
    }
}
