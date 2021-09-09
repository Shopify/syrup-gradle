package com.shopify.syrup

import com.shopify.syrup.extensions.SchemaConfig
import com.shopify.syrup.extensions.SyrupSupportExtensions
import com.shopify.syrup.tasks.CleanGenerateModelsTask
import com.shopify.syrup.tasks.CreateModelsDirectoriesTask
import com.shopify.syrup.tasks.CreateSupportDirectoriesTask
import com.shopify.syrup.tasks.DeleteTemporaryFilesTask
import com.shopify.syrup.tasks.FormatModelsTask
import com.shopify.syrup.tasks.GenerateModelsTask
import com.shopify.syrup.tasks.GenerateSupportFilesTask
import com.shopify.syrup.tasks.MoveModelsTask
import groovy.lang.GString
import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.IOException

class SyrupPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val schemas = project.container(SchemaConfig::class.java)
        project.extensions.add(SchemaConfig.NAME, schemas)
        project.extensions.create(SyrupSupportExtensions.NAME, SyrupSupportExtensions::class.java, project)

        project.afterEvaluate {
            @Suppress("UNCHECKED_CAST") val schemaConfigs: NamedDomainObjectContainer<SchemaConfig> = project.extensions.findByName(SchemaConfig.NAME) as NamedDomainObjectContainer<SchemaConfig>
            val supportConfig = project.extensions.findByType(SyrupSupportExtensions::class.java)

            createNamedModelTasks(project, schemaConfigs)
            createModelTasks(project, schemaConfigs)
            supportConfig?.let { createSupportTasks(project, it) }
        }
    }


    private fun createNamedModelTasks(project: Project, schemas: NamedDomainObjectContainer<SchemaConfig>) {
        schemas.all { schema ->
            val capitalizedName = schema.name.capitalize()

            val createTaskName = CreateModelsDirectoriesTask.NAME_PATTERN.format(capitalizedName)
            val executeTaskName = GenerateModelsTask.NAME_PATTERN.format(capitalizedName)
            val cleanTaskName = CleanGenerateModelsTask.NAME_PATTERN.format(capitalizedName)
            val organizeTaskName = MoveModelsTask.NAME_PATTERN.format(capitalizedName)
            val formatTaskName = FormatModelsTask.NAME_PATTERN.format(capitalizedName)
            val deleteTaskName = DeleteTemporaryFilesTask.NAME_PATTERN.format(capitalizedName)

            project.tasks.create(createTaskName, CreateModelsDirectoriesTask::class.java) { it.schemaConfig = schema }
            project.tasks.create(executeTaskName, GenerateModelsTask::class.java) { it.schemaConfig = schema }.mustRunAfter(createTaskName)
            project.tasks.create(cleanTaskName, CleanGenerateModelsTask::class.java) { it.schemaConfig = schema }.mustRunAfter(executeTaskName)
            project.tasks.create(organizeTaskName, MoveModelsTask::class.java) { it.schemaConfig = schema }.mustRunAfter(cleanTaskName)
            project.tasks.create(formatTaskName, FormatModelsTask::class.java) { it.schemaConfig = schema }.mustRunAfter(organizeTaskName)
            project.tasks.create(deleteTaskName, DeleteTemporaryFilesTask::class.java) { it.schemaConfig = schema }.mustRunAfter(formatTaskName)

            val taskName = GENERATE_MODELS_TASK_PATTERN.format(capitalizedName)

            project.tasks.create(taskName, DefaultTask::class.java) {
                it.group = SYRUP_TASK_GROUP
                it.dependsOn(listOf(createTaskName, executeTaskName, cleanTaskName, organizeTaskName, formatTaskName, deleteTaskName))
            }
        }
    }

    private fun createModelTasks(project: Project, schemas: NamedDomainObjectContainer<SchemaConfig>) {
        val modelTaskList = mutableListOf<String>()
        schemas.all { modelTaskList.add(GENERATE_MODELS_TASK_PATTERN.format(it.name.capitalize())) }

        project.tasks.create(GENERATE_MODELS_TASK, DefaultTask::class.java) { it.group = SYRUP_TASK_GROUP }
            .dependsOn(modelTaskList)
    }

    private fun createSupportTasks(project: Project, extensions: SyrupSupportExtensions) {
        project.tasks.create(CreateSupportDirectoriesTask.NAME, CreateSupportDirectoriesTask::class.java)
        project.tasks.create(GenerateSupportFilesTask.NAME, GenerateSupportFilesTask::class.java) {
            it.extension = extensions
        }.mustRunAfter(CreateSupportDirectoriesTask.NAME)

        project.tasks.create(GENERATE_SUPPORT_TASK, DefaultTask::class.java) {
            it.group = SYRUP_TASK_GROUP
        }.dependsOn(CreateSupportDirectoriesTask.NAME, GenerateSupportFilesTask.NAME)
    }

    companion object {
        const val SYRUP_GENERATED_SOURCE_TMP_PATTERN = "/generated/source/syrup/%s/tmp"
        const val SYRUP_SUPPORT_PACKAGE = "/src/main/java/com/shopify/syrup/support"
        private const val SYRUP_TASK_GROUP = "syrup"
        private const val GENERATE_MODELS_TASK = "generateModels"
        private const val GENERATE_MODELS_TASK_PATTERN = "generate%sModels"
        private const val GENERATE_SUPPORT_TASK = "generateSupportFiles"
    }
}

fun Project.syrupBin(): String {
    val syrupBin = properties["syrupBin"] as? GString ?: "syrup"

    try {
        Runtime.getRuntime().exec("$syrupBin -v").apply { waitFor() }.exitValue()
    } catch (e: IOException) {
        throw IOException(
            """
Unable to find syrup executable in the exec PATH. Ensure syrup is installed
and accessible in your PATH, or manually set `project.ext.syrupBin` to the
executable's path.
            """
        )
    }

    return syrupBin.toString()
}
