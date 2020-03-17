package com.shopify.syrup.extensions

import org.gradle.api.tasks.Input
import org.yaml.snakeyaml.Yaml
import java.io.File

open class SchemaConfig(@Input val name: String) {
    @Input var config: String = ""
    @Input var graphql: String = ""
    @Input var format: Boolean = false
    @Input var generateReport: Boolean = false

    companion object {
        const val NAME = "syrup"
    }
}

fun SchemaConfig.moduleNameToSrcPath(projectDir: File): File {
    val yamlModule = Yaml().load<HashMap<String, String>>(File(projectDir.absolutePath, config).inputStream())["moduleName"]
    val moduleName = if (yamlModule.isNullOrEmpty()) "com.shopify.syrup" else yamlModule
    return File(projectDir, "${File.separator}src${File.separator}main${File.separator}java${File.separator}${moduleName.replace(".", File.separator)}")
}
