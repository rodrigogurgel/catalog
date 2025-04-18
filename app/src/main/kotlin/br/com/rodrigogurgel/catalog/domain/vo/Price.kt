package br.com.rodrigogurgel.catalog.domain.vo

import br.com.rodrigogurgel.catalog.domain.exception.PriceNegativeException
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import java.math.BigDecimal
import java.math.RoundingMode

class Price(value: BigDecimal) {
    val value: BigDecimal = value.setScale(2, RoundingMode.UP)

    init {
        if (value.setScale(2, RoundingMode.UP) < BigDecimal.ZERO) throw PriceNegativeException(value)
    }

    companion object {
        val ZERO = Price(BigDecimal.ZERO)
    }

    override fun toString(): String {
        return value.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Price) return false

        return EqualsBuilder()
            .append(value, other.value)
            .isEquals
    }

    override fun hashCode(): Int {
        return HashCodeBuilder()
            .append(value)
            .toHashCode()
    }
}
