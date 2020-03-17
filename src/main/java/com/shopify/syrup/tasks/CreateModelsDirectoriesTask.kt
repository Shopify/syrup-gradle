package com.shopify.syrup.tasks

import com.shopify.syrup.extensions.SchemaConfig
import com.shopify.syrup.extensions.moduleNameToSrcPath
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
import java.io.File

open class CreateModelsDirectoriesTask : DefaultTask() {
    @Nested lateinit var schemaConfig: SchemaConfig

    @TaskAction
    fun action() {
        listOf(
            schemaConfig.moduleNameToSrcPath(project.projectDir).path,
            File(project.buildDir.path, SYRUP_GENERATED_SOURCE_TMP).path
        ).forEach { File(it).mkdirs() }
    }

    companion object {
        const val NAME_PATTERN = "create%sModelsOutputDirectories"
        const val SYRUP_GENERATED_SOURCE_TMP = "/generated/source/syrup/tmp"
    }
}
