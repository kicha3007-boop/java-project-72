plugins {
    application
    jacoco
    alias(libs.plugins.versions)
    alias(libs.plugins.spotless)
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.compileJava {
    options.encoding = "UTF-8"
}

application {
    mainClass = "hexlet.code.App"
}

dependencies {
    implementation(libs.javalin)
    implementation(libs.javalin.rendering.jte)
    implementation(libs.jte)
    implementation(libs.slf4j.simple)

    implementation(libs.hikaricp)
    implementation(libs.h2)
    implementation(libs.postgresql)

    implementation(libs.unirest)
    implementation(libs.jsoup)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.assertj)
    testImplementation(libs.mockwebserver)
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
    finalizedBy(tasks.jacocoTestReport)
}

spotless {
    java {
        target("src/**/*.java")
        importOrder()
        removeUnusedImports()
        googleJavaFormat().aosp()
        formatAnnotations()
        trimTrailingWhitespace()
        endWithNewline()
    }
}

// Точка входа поднимает сервер на порту из окружения — её проверяет запуск, а не юнит-тесты
val coverageExcludes = listOf("hexlet/code/App.class")

fun JacocoReportBase.excludeEntryPoint() {
    classDirectories.setFrom(
        files(classDirectories.files.map { fileTree(it) { exclude(coverageExcludes) } }),
    )
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    excludeEntryPoint()
    reports {
        xml.required = true
        html.required = true
    }
}

// Порог покрытия: ниже него `make build` падает, а вместе с ним и сборка в CI
tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)
    excludeEntryPoint()
    violationRules {
        rule {
            limit {
                counter = "INSTRUCTION"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}

tasks.check { dependsOn(tasks.jacocoTestCoverageVerification) }

// Render и подобные хостинги собирают приложение задачей stage
tasks.register("stage") {
    dependsOn("clean", "installDist")
}

tasks.installDist {
    mustRunAfter(tasks.clean)
}
