package com.shopify.syrup.extensions

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputFile
import java.io.File

open class SyrupSupportExtensions(project: Project) {

    @InputFile val configFile: Property<File> = project.objects.property(File::class.java)

    companion object {
        const val NAME = "syrupSupport"
    }
}
