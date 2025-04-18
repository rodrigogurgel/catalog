package br.com.rodrigogurgel.catalog.domain.entity

import br.com.rodrigogurgel.catalog.domain.exception.CustomizationMinPermittedException
import br.com.rodrigogurgel.catalog.domain.exception.CustomizationOptionsIsEmptyException
import br.com.rodrigogurgel.catalog.domain.exception.OptionAlreadyExistsException
import br.com.rodrigogurgel.catalog.domain.exception.OptionNotFoundException
import br.com.rodrigogurgel.catalog.domain.vo.Description
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.domain.vo.Name
import br.com.rodrigogurgel.catalog.domain.vo.Price
import br.com.rodrigogurgel.catalog.domain.vo.Quantity
import br.com.rodrigogurgel.catalog.domain.vo.Status
import br.com.rodrigogurgel.catalog.domain.vo.Status.AVAILABLE
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

class Customization(
    val id: Id,
    var name: Name,
    var description: Description?,
    quantity: Quantity,
    var status: Status,
    val options: MutableList<Option>,
) {
    var quantity: Quantity = quantity
        set(value) {
            field = value
            validateQuantity()
        }

    init {
        validateOptions()
        validateQuantity()
    }

    private fun validateOptions() {
        if (options.isEmpty()) throw CustomizationOptionsIsEmptyException(id)
    }

    private fun validateQuantity() {
        val availableOptions = options.count { it.status == AVAILABLE }
        if (quantity.minPermitted > availableOptions) {
            throw CustomizationMinPermittedException(id)
        }
    }

    /**
     * Adds an option to the customization.
     *
     * @param option The option to be added.
     * @throws OptionAlreadyExistsException if an option with the same ID already exists in the customization.
     */
    fun addOption(option: Option) {
        if (options.any { it.id == option.id }) throw OptionAlreadyExistsException(option.id)
        options.add(option)
        validateOptions()
        validateQuantity()
    }

    /**
     * Update an option in the customization.
     *
     * @param option The option to be updated.
     * @throws OptionNotFoundException if the option with the given id is not found.
     */
    fun updateOption(option: Option) {
        val index = options.indexOfFirst { it.id == option.id }
        if (index == -1) throw OptionNotFoundException(option.id)
        options[index] = option
        validateOptions()
        validateQuantity()
    }

    /**
     * Removes the specified option from the customization.
     *
     * @param optionId The ID of the option to be removed.
     * @throws OptionNotFoundException if the option with the specified ID is not found.
     */
    fun removeOption(optionId: Id) {
        options.removeIf { it.id == optionId }
        validateOptions()
        validateQuantity()
    }

    /**
     * Retrieves all the customizations that are present in the children of the current customization.
     *
     * @return A list of Customization objects that are found in the children of the current customization.
     */
    fun getAllCustomizationsInChildren(): List<Customization> {
        return options.flatMap { it.getAllCustomizationsInChildren() }
    }

    /**
     * Returns the list of options found in the children of this option.
     *
     * @return The list of options found in the children of this option.
     */
    fun getAllOptionsInChildren(): List<Option> {
        return options + options.flatMap { it.getAllOptionsInChildren() }
    }

    /**
     * Calculates the minimal price based on the options.
     *
     * @return The minimal price.
     */
    fun minimalPrice(): Price {
        return options.asSequence()
            .map { it.minimalPrice() }
            .sortedBy { it.value }
            .take(quantity.minPermitted)
            .sumOf { it.value }
            .let { Price(it) }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Customization) return false

        return EqualsBuilder()
            .append(id, other.id)
            .append(name, other.name)
            .append(description, other.description)
            .append(quantity, other.quantity)
            .append(status, other.status)
            .append(options, other.options)
            .isEquals
    }

    override fun hashCode(): Int {
        return HashCodeBuilder()
            .append(id)
            .append(name)
            .append(description)
            .append(quantity)
            .append(status)
            .append(options)
            .toHashCode()
    }
}
