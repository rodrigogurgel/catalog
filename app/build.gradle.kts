import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import kotlinx.kover.gradle.plugin.dsl.GroupingEntityType

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

dependencies {
    // Versions
    val apacheCommonsVersion = "3.17.0"
    val detektVersion = "1.23.8"
    val kotestVersion = "5.9.1"
    val springMockkVersion = "4.0.2"

    // Misc
    implementation("org.apache.commons:commons-lang3:$apacheCommonsVersion")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Kotlinx Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // Project Reactor
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    // Jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Spring
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // Detekt
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")

    // Spring Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // Project Reactor Test
    testImplementation("io.projectreactor:reactor-test")

    // JUnit5 Test
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Mockk Test
    testImplementation("com.ninja-squad:springmockk:$springMockkVersion")

    // Kotest Test
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")

    // Kotlinx Coroutines Test
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
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
                    "br.com.rodrigogurgel.catalog.domain.vo.*"
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

tasks.check {
    dependsOn(tasks.detekt)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
