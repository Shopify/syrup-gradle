package com.shopify.syrup.tasks

import com.shopify.syrup.extensions.SchemaConfig
import com.shopify.syrup.extensions.moduleNameToSrcPath
import com.shopify.syrup.SyrupPlugin
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class MoveModelsTask : DefaultTask() {

    lateinit var schemaConfig: SchemaConfig

    @TaskAction
    fun action() {
        val modelsOutputDirectoryPath = schemaConfig.moduleNameToSrcPath(project.projectDir)
        val temporaryModelsOutputDirectoryPath = File(project.buildDir.path, SyrupPlugin.SYRUP_GENERATED_SOURCE_TMP_PATTERN.format(schemaConfig.name))

        project.copy { copySpec ->
            copySpec.from(File(temporaryModelsOutputDirectoryPath, ENUMS_DIR)) {
                it.into(ENUMS_DIR.toLowerCase())
                it.include("*.kt")
            }

            copySpec.from(File(temporaryModelsOutputDirectoryPath, QUERIES_DIR)) {
                it.into(QUERIES_DIR.toLowerCase())
                it.include("*.kt")
            }

            copySpec.from(File(temporaryModelsOutputDirectoryPath, INPUTS_DIR)) {
                it.into(INPUTS_DIR.toLowerCase())
                it.include("*.kt")
            }

            copySpec.from(File(temporaryModelsOutputDirectoryPath, MUTATIONS_DIR)) {
                it.into(MUTATIONS_DIR.toLowerCase())
                it.include("*.kt")
            }

            copySpec.from(File(temporaryModelsOutputDirectoryPath, RESPONSES_DIR)) {
                it.into(RESPONSES_DIR.toLowerCase())
                it.include("*.kt")
            }

            copySpec.from(File(temporaryModelsOutputDirectoryPath, FRAGMENTS_DIR)) {
                it.into(FRAGMENTS_DIR.toLowerCase())
                it.include("*.kt")
            }

            copySpec.includeEmptyDirs = true
            copySpec.into(modelsOutputDirectoryPath)
        }
    }

    companion object {
        const val NAME_PATTERN = "move%sModels"

        private const val ENUMS_DIR = "/Enums"
        private const val QUERIES_DIR = "/Queries"
        private const val INPUTS_DIR = "/Inputs"
        private const val MUTATIONS_DIR = "/Mutations"
        private const val RESPONSES_DIR = "/Responses"
        private const val FRAGMENTS_DIR = "/Fragments"
    }
}
