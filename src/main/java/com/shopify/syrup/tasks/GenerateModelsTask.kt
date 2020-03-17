package com.shopify.syrup.tasks

import com.shopify.syrup.extensions.SchemaConfig
import com.shopify.syrup.SyrupPlugin
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
import java.io.ByteArrayOutputStream
import java.io.File

open class GenerateModelsTask : DefaultTask() {

    @Nested lateinit var schemaConfig: SchemaConfig

    @TaskAction
    fun action() {
        val execCmd = mutableListOf(
            "syrup",
            "generate-models",
            File(schemaConfig.graphql).path,
            File(project.buildDir.path, SyrupPlugin.SYRUP_GENERATED_SOURCE_TMP_PATTERN.format(schemaConfig.name)).path,
            "Kotlin",
            "--project",
            File(schemaConfig.config).path,
            "--schema",
            File(schemaConfig.config).path
        ).apply {
            if (schemaConfig.generateReport) {
                add("--reports")
                add(project.projectDir.path)
            }
        }

        val outputStream = ByteArrayOutputStream()
        val errorStream = ByteArrayOutputStream()

        val exitValue = project.exec {
            it.standardOutput = outputStream
            it.errorOutput = errorStream
            it.workingDir = project.projectDir
            it.commandLine = execCmd
            it.isIgnoreExitValue = true
        }.exitValue

        println("\n$outputStream\n")
        if (exitValue != 0) {
            println("\n$errorStream\n")
            throw GradleException("$name task failed, see above output")
        }
    }

    companion object {
        const val NAME_PATTERN = "generate%sSyrupModels"
    }
}
