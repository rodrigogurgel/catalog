package br.com.rodrigogurgel.catalog.domain.entity

import br.com.rodrigogurgel.catalog.domain.exception.CustomizationAlreadyExistsException
import br.com.rodrigogurgel.catalog.domain.exception.CustomizationNotFoundException
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.domain.vo.Media
import br.com.rodrigogurgel.catalog.domain.vo.Name
import br.com.rodrigogurgel.catalog.domain.vo.Price
import br.com.rodrigogurgel.catalog.domain.vo.Quantity
import br.com.rodrigogurgel.catalog.domain.vo.Status
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

data class Option(
    val id: Id,
    val name: Name,
    var product: Product?,
    var quantity: Quantity,
    var status: Status,
    var price: Price,
    val customizations: MutableList<Customization>,
    var medias: List<Media>,
) {
    /**
     * Retrieves all the customizations that are present in the children of the current customization.
     *
     * @return A list of Customization objects that are found in the children of the current customization.
     */
    fun getAllCustomizationsInChildren(): List<Customization> {
        return customizations + customizations.flatMap { it.getAllCustomizationsInChildren() }
    }

    /**
     * Retrieves all the options in the current object and its child customizations.
     *
     * @return A list of options present in the current object and its child customizations.
     */
    fun getAllOptionsInChildren(): List<Option> {
        return customizations.flatMap { it.getAllOptionsInChildren() }
    }

    /**
     * Adds customization to the Option.
     *
     * @param customization The customization to be added.
     * @throws CustomizationAlreadyExistsException if customization with the same ID already exists in the Option.
     */
    fun addCustomization(customization: Customization) {
        if (customizations.any { it.id == customization.id }) {
            throw CustomizationAlreadyExistsException(customization.id)
        }

        customizations.add(customization)
    }

    /**
     * Updates customization with the given data.
     *
     * @param customization The customization object containing the updated information.
     * @throws CustomizationNotFoundException if the customization with the specified ID is not found.
     */
    fun updateCustomization(customization: Customization) {
        val index = customizations.indexOfFirst { it.id == customization.id }
        if (index == -1) throw CustomizationNotFoundException(customization.id)
        customizations[index] = customization
    }

    /**
     * Remove customization with the given customization ID.
     *
     * @param customizationId The ID of the customization to be removed.
     */
    fun removeCustomization(customizationId: Id) {
        customizations.removeIf { it.id == customizationId }
    }

    /**
     * Calculates the minimal price for the Option.
     *
     * @return The minimal price.
     */
    fun minimalPrice(): Price {
        val minPermittedOrOne = if (quantity.minPermitted == 0) 1 else quantity.minPermitted

        return Price(
            (price.value * minPermittedOrOne.toBigDecimal()) +
                customizations.sumOf {
                    it.minimalPrice().value
                },
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Option) return false

        return EqualsBuilder()
            .append(id, other.id)
            .append(name, other.name)
            .append(product, other.product)
            .append(price.value, other.price.value)
            .append(quantity, other.quantity)
            .append(status, other.status)
            .append(customizations, other.customizations)
            .isEquals
    }

    override fun hashCode(): Int {
        return HashCodeBuilder()
            .append(id)
            .append(name)
            .append(product)
            .append(price.value)
            .append(quantity)
            .append(status)
            .append(customizations)
            .toHashCode()
    }
}
