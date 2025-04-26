package br.com.rodrigogurgel.catalog.domain.entity

import br.com.rodrigogurgel.catalog.domain.exception.CustomizationAlreadyExistsException
import br.com.rodrigogurgel.catalog.domain.exception.CustomizationNotFoundException
import br.com.rodrigogurgel.catalog.domain.exception.DuplicatedCustomizationException
import br.com.rodrigogurgel.catalog.domain.exception.DuplicatedOptionException
import br.com.rodrigogurgel.catalog.domain.exception.OfferPriceZeroException
import br.com.rodrigogurgel.catalog.domain.vo.Description
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.domain.vo.Media
import br.com.rodrigogurgel.catalog.domain.vo.Name
import br.com.rodrigogurgel.catalog.domain.vo.Price
import br.com.rodrigogurgel.catalog.domain.vo.Status
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

class Offer(
    val id: Id,
    var name: Name,
    var description: Description?,
    var product: Product?,
    price: Price,
    var status: Status,
    val customizations: MutableList<Customization>,
    var medias: List<Media>,
) {
    var price: Price = price
        set(value) {
            field = value
            validatePrice()
        }

    init {
        validate()
    }

    private val customizationsById: Map<Id, Customization>
        get() {
            val customizationInChildren =
                customizations.flatMap { customization -> customization.getAllCustomizationsInChildren() }
            val allCustomizations = customizations + customizationInChildren
            return allCustomizations.associateBy { it.id }
        }

    private val optionsById: Map<Id, Option>
        get() =
            customizations
                .flatMap { it.getAllOptionsInChildren() }
                .associateBy { it.id }

    private fun validatePrice() {
        if (price.value <= Price.ZERO.value) throw OfferPriceZeroException(id)
    }

    /**
     * Adds customization to the Offer.
     *
     * @param customization the customization to be added
     * @throws CustomizationAlreadyExistsException if a customization with the same id already exists in the Offer
     */
    fun addCustomization(customization: Customization) {
        if (customizations.any { it.id == customization.id }) throw CustomizationAlreadyExistsException(id)
        customizations.add(customization)
    }

    /**
     * Updates customization in the Offer.
     *
     * @param customization the customization to be updated
     * @throws CustomizationNotFoundException if the customization with the specified id is not found in the Offer
     */
    fun updateCustomization(customization: Customization) {
        val index = customizations.indexOfFirst { it.id == customization.id }
        if (index == -1) throw CustomizationNotFoundException(customization.id)
        customizations[index] = customization
    }

    /**
     * Removes customization from the Offer.
     *
     * @param customizationId the ID of the customization to be removed
     */
    fun removeCustomization(customizationId: Id) {
        customizations.removeIf { it.id == customizationId }
    }

    /**
     * Finds an Option in the children of the Offer by its Id.
     *
     * @param id The Id of the Option to be found.
     * @return The Option with the specified Id, or null if not found.
     */
    fun findOptionInChildrenById(id: Id): Option? {
        return optionsById[id]
    }

    /**
     * Finds customization in the Offer or in the children of the Offer by its Id.
     *
     * @param id The Id of the Customization to be found.
     * @return The Customization with the specified Id, or null if not found.
     */
    fun findCustomizationInChildrenById(id: Id): Customization? {
        return customizationsById[id]
    }

    /**
     * Calculates the minimal price of the Offer.
     *
     * @return The minimal price.
     */
    fun minimalPrice(): Price {
        return Price(price.value + customizations.sumOf { it.minimalPrice().value })
    }

    fun getAllProducts(): List<Product> {
        val subProducts = optionsById.values.mapNotNull { option -> option.product }
        return if (product != null) {
            subProducts + product!!
        } else {
            subProducts
        }
    }

    private fun fetchDuplicatedCustomizationIds(): List<Id> {
        return this.customizations.flatMap { customization ->
            customization.getAllCustomizationsInChildren() + customization
        }
            .groupingBy { customization -> customization.id }
            .eachCount()
            .filter { idByCount -> idByCount.value > 1 }
            .map { duplicatedId -> duplicatedId.key }
    }

    private fun fetchDuplicatedOptionIds(): List<Id> {
        return this.customizations.flatMap { customization -> customization.getAllOptionsInChildren() }
            .groupingBy { option -> option.id }
            .eachCount()
            .filter { idByCount -> idByCount.value > 1 }
            .map { duplicatedId -> duplicatedId.key }
    }

    fun validate() {
        val duplicatedCustomizations = this.fetchDuplicatedCustomizationIds()
        if (duplicatedCustomizations.isNotEmpty()) {
            throw DuplicatedCustomizationException(
                duplicatedCustomizations,
            )
        }
        val duplicatedOptionIds = this.fetchDuplicatedOptionIds()
        if (duplicatedOptionIds.isNotEmpty()) throw DuplicatedOptionException(duplicatedOptionIds)

        validatePrice()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Offer) return false

        return EqualsBuilder()
            .append(id, other.id)
            .append(name, other.name)
            .append(description, other.description)
            .append(product, other.product)
            .append(price.value, other.price.value)
            .append(status, other.status)
            .append(customizations, other.customizations)
            .isEquals
    }

    override fun hashCode(): Int {
        return HashCodeBuilder()
            .append(id)
            .append(name)
            .append(description)
            .append(product)
            .append(price.value)
            .append(status)
            .append(customizations)
            .toHashCode()
    }
}
