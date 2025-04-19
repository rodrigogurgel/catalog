package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.exception

data class DatastoreIntegrationException(
    override val cause: Throwable
) : RuntimeException("Failed to integrate with the datastore.", cause)
