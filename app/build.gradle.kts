import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import kotlinx.kover.gradle.plugin.dsl.GroupingEntityType
import java.net.URL

plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.spring") version "2.0.21"

    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"

    id("io.gitlab.arturbosch.detekt") version "1.23.8"
    id("org.jetbrains.kotlinx.kover") version "0.9.1"
}

group = "br.com.rodrigogurgel"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

// Versions
val apacheCommonsVersion = "3.17.0"
val detektVersion = "1.23.8"
val kotestVersion = "5.9.1"
val springMockkVersion = "4.0.2"
val opentelemetryBOMAlpha = "1.47.0-alpha"
val opentelemetryInstrumentationBOM = "2.13.3"
val opentelemetryInstrumentationBOMAlpha = "2.13.3-alpha"
val logstashVersion = "8.0"
val kotlinResultVersion = "2.0.1"
val springDocVersion = "2.8.5"
val amazonSDKVersion = "2.31.23"
val kotlinxCoroutinesSlf4jMDCVersion = "1.10.2"

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Kotlinx Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // Project Reactor
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    // Jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Logstash
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashVersion")

    // Misc
    implementation("org.apache.commons:commons-lang3:$apacheCommonsVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:$kotlinxCoroutinesSlf4jMDCVersion")

    // Spring
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-web")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Mongo DB
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springDocVersion")
    implementation("org.springdoc:springdoc-openapi-starter-common:$springDocVersion")

    // Observability
    implementation("io.opentelemetry:opentelemetry-exporter-otlp")
    implementation("io.opentelemetry:opentelemetry-extension-kotlin")
    implementation("io.opentelemetry:opentelemetry-api-incubator")
    implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-api-incubator")

    // Kotlin Result
    implementation("com.michael-bull.kotlin-result:kotlin-result:$kotlinResultVersion")
    implementation("com.michael-bull.kotlin-result:kotlin-result-coroutines:$kotlinResultVersion")

    // Detekt
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")

    // Spring Test
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude("org.mockito", "mockito-junit-jupiter")
        exclude("org.mockito", "mockito-core")
    }
    testImplementation("org.springframework.boot:spring-boot-testcontainers")

    // Project Reactor Test
    testImplementation("io.projectreactor:reactor-test")

    // JUnit5 Test
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Mockk Test
    testImplementation("com.ninja-squad:springmockk:$springMockkVersion")

    // Test Containers
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mongodb")

    // Kotest Test
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")

    // Kotlinx Coroutines Test
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")

    // MongoDB Test
    testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo:4.20.0")
}

dependencyManagement {
    imports {
        mavenBom("io.opentelemetry:opentelemetry-bom-alpha:$opentelemetryBOMAlpha")
        mavenBom("io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom:$opentelemetryInstrumentationBOM")
        mavenBom("io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom-alpha:$opentelemetryInstrumentationBOMAlpha")
        mavenBom("software.amazon.awssdk:bom:$amazonSDKVersion")
    }
}

detekt {
    autoCorrect = true
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom("$projectDir/config/detekt.yaml")
}

kover {
    reports {
        filters {
            excludes {
                classes(
                    "br.com.rodrigogurgel.catalog.CatalogApplication*",
                    "br.com.rodrigogurgel.catalog.domain.vo.*",
                    "br.com.rodrigogurgel.catalog.common.*",
                    "br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.utils.*",
                    "br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.extensions.*",
                    "br.com.rodrigogurgel.catalog.framework.adapter.input.rest.filter.*",
                    "br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.media.*",
                    "br.com.rodrigogurgel.catalog.framework.adapter.input.rest.extensions.*",
                    "br.com.rodrigogurgel.catalog.framework.adapter.input.rest.extensions.*",
                    "br.com.rodrigogurgel.catalog.framework.config.*",
                    "br.com.rodrigogurgel.catalog.framework.adapter.input.rest.requestservelet.*",
                    "br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.utils.*"
                )
            }
        }

        total {
            html {
                onCheck = true
            }
        }
        verify {
            rule {
                groupBy = GroupingEntityType.CLASS
                bound {
                    coverageUnits = CoverageUnit.LINE
                    minValue = 90
                }
            }

            rule {
                groupBy = GroupingEntityType.APPLICATION
                bound {
                    coverageUnits = CoverageUnit.BRANCH
                    minValue = 90
                }

                bound {
                    coverageUnits = CoverageUnit.INSTRUCTION
                    minValue = 90
                }
            }
        }
    }
}

tasks.register("downloadOtelAgent") {
    group = "otel"
    description = "Baixa o OpenTelemetry Java Agent mais recente"

    val outputDir = layout.buildDirectory.dir("otel")
    val outputFile = outputDir.map { it.file("opentelemetry-javaagent.jar") }

    outputs.file(outputFile)

    doLast {
        val url =
            URL("https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar")

        val targetFile = outputFile.get().asFile
        targetFile.parentFile.mkdirs()

        println("📥 Baixando o agente para: ${targetFile.absolutePath}")
        url.openStream().use { input ->
            targetFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        println("✅ Download completo.")
    }
}

tasks.check {
    dependsOn(tasks.detekt)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    jvmArgs("--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED")
    useJUnitPlatform()
}
