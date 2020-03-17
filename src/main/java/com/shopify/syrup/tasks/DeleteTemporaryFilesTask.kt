package com.shopify.syrup.tasks

import com.shopify.syrup.extensions.SchemaConfig
import com.shopify.syrup.SyrupPlugin
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
import java.io.File

open class DeleteTemporaryFilesTask : DefaultTask() {

    @Nested lateinit var schemaConfig: SchemaConfig

    @TaskAction
    fun action() {
        val modelsOutputDirectoryPath = File(project.buildDir.path, SyrupPlugin.SYRUP_GENERATED_SOURCE_TMP_PATTERN.format(schemaConfig.name))
        project.delete(modelsOutputDirectoryPath)
    }

    companion object {
        const val NAME_PATTERN = "delete%sTemporaryDirectories"
    }
}
