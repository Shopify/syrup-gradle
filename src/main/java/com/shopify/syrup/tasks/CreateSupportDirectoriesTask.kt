package com.shopify.syrup.tasks

import com.shopify.syrup.SyrupPlugin
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class CreateSupportDirectoriesTask : DefaultTask() {

    @TaskAction
    fun action() {
        File(project.projectDir.path, SyrupPlugin.SYRUP_SUPPORT_PACKAGE).mkdirs()
    }

    companion object {
        const val NAME = "createSupportOutputDirectories"
    }
}
