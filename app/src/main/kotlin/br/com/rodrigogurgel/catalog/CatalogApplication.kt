package br.com.rodrigogurgel.catalog

import br.com.rodrigogurgel.catalog.framework.config.datastore.DynamoDBProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(DynamoDBProperties::class)
class CatalogApplication

fun main(args: Array<String>) {
    runApplication<CatalogApplication>(args = args)
}
