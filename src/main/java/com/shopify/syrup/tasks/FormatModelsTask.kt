package com.shopify.syrup.tasks

import com.shopify.syrup.extensions.SchemaConfig
import com.shopify.syrup.extensions.moduleNameToSrcPath
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.ByteArrayOutputStream
import java.io.File

open class FormatModelsTask : DefaultTask() {

    lateinit var schemaConfig: SchemaConfig

    @TaskAction
    fun action() {
        if (!schemaConfig.format) {
            println("ktlint formatting not enabled, skipping")
        }

        project.javaexec {
            it.classpath = project.configurations.getByName(JAVA_TASK)
            it.main = MAIN_CLASS

            it.standardOutput = ByteArrayOutputStream()
            it.isIgnoreExitValue = true

            it.args = listOf(
                "-F",
                schemaConfig.moduleNameToSrcPath(project.projectDir).path + ALL_KOTLIN_SRC
            )
        }
    }

    companion object {
        const val NAME_PATTERN = "format%sModels"
        private const val JAVA_TASK = "ktlint"
        private const val MAIN_CLASS = "com.pinterest.ktlint.Main"
        private val ALL_KOTLIN_SRC = "${File.separator}**${File.separator}*.kt"
    }
}
