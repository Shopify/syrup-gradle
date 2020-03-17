package com.shopify.syrup.tasks

import com.shopify.syrup.extensions.SchemaConfig
import com.shopify.syrup.extensions.moduleNameToSrcPath
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
import java.io.File


open class CleanGenerateModelsTask : DefaultTask() {

    @Nested lateinit var schemaConfig: SchemaConfig

    @TaskAction
    fun action() {
        val modelsOutputDirectoryPath = schemaConfig.moduleNameToSrcPath(project.projectDir)

        listOf("enums", "fragments", "inputs", "queries", "mutations", "responses").forEach {
            project.delete(File(modelsOutputDirectoryPath, it))
        }
    }

    companion object {
        const val NAME_PATTERN = "delete%sOutputDirectories"
    }
}
