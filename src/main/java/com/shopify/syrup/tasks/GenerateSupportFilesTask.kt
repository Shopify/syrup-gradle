package com.shopify.syrup.tasks

import com.shopify.syrup.extensions.SyrupSupportExtensions
import com.shopify.syrup.SyrupPlugin
import com.shopify.syrup.syrupBin
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
import java.io.ByteArrayOutputStream
import java.io.File

open class GenerateSupportFilesTask : DefaultTask() {

    @Nested lateinit var extension: SyrupSupportExtensions

    @TaskAction
    fun action() {
        val execCmd = mutableListOf(
            project.syrupBin(),
            "generate-support-files",
            File(project.projectDir, SyrupPlugin.SYRUP_SUPPORT_PACKAGE).path,
            "Kotlin",
            "--project",
            extension.configFile.get().path,
            "--schema",
            extension.configFile.get().path
        )

        val outputStream = ByteArrayOutputStream()

        val exitValue = project.exec {
            it.standardOutput = outputStream
            it.errorOutput = outputStream
            it.workingDir = project.projectDir
            it.commandLine = execCmd
            it.isIgnoreExitValue = true
        }.exitValue

        if (exitValue != 0) {
            println(outputStream.toString())
            throw GradleException("$name task failed, see above output")
        }
    }

    companion object {
        const val NAME = "generateSyrupSupportFiles"
    }
}
