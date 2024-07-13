/**
 * 本 convention plugin 簡述：
 *
 * Sponge 提供的 gradle 插件具有運行服務器的 task，它可以下載一箇
 * sponge vanilla 的服務器，並帶着目前編寫的插件啓動它。這非常好。
 * 但是我們希望可以運行我們本地的服務器，以跟具體情況（特定 MC 版本、
 * mods 等）結合來測試正在編寫的插件。
 *
 * 本 conventions plugin 提供一箇 RunLocalMcServerTask，能夠
 * 帶着目前編寫的插件運行伱電腦上已經存在的服務器。
 */

import java.util.jar.Attributes
import java.util.jar.JarFile

plugins {
    application
    java
}

interface RunLocalMcServerPluginConfigExtension {
    var enable: Boolean
    var localServerPath: String?
    var localServerPluginPath: String?
    var localServerJarPath: String?
}
val extension = project.extensions.create<RunLocalMcServerPluginConfigExtension>("runLocalMcServer")

data class RunLocalMcServerPluginConfig (
        var enable: Boolean,
        var localServerFolder: File,
        var localServerPluginFolder: File,
        var localServerJar: File,
)

val usage  = "Usage:\n" +
        "runLocalMcServer {\n" +
        "\tenable=true\t\t// Set false to disable the plugin\n" +
        "\tlocalServerPath=....\n" +
        "\tlocalServerPluginPath=....\n" +
        "\tlocalServerJarPath=....\n" +
        "}"
fun getConfigFromExtension(
        configExtension: RunLocalMcServerPluginConfigExtension
): RunLocalMcServerPluginConfig {
    val config = with(configExtension) {
        RunLocalMcServerPluginConfig(
                enable,
                File(localServerPath ?: throw GradleException("'localServerPath' not set.\n$usage")),
                File(localServerPluginPath ?: throw GradleException("'localServerPluginPath' not set.\n$usage")),
                File(localServerJarPath ?: throw GradleException("'localServerJarPath' not set.\n$usage"))
        )
    }

    // 檢查合法性
    with(config) {
        if (
                !(localServerFolder.exists() && localServerFolder.isDirectory)
        )
            throw GradleException("localServerPath 不存在或不是目錄\n$usage")

        if (
                !(localServerPluginFolder.exists() && localServerPluginFolder.isDirectory)
        )
            throw GradleException("localServerPluginPath 不存在或不是目錄\n$usage")

        if (
                !(localServerJar.exists() && localServerJar.isFile)
        )
            throw GradleException("localServerJarPath 不存在或不是文件\n$usage")

        return this

    }
}


/**
 * 獲取指定 jar 文件 Manifest 裏面的 Attributes。
 */
fun extractManifestAttributes(jarFile: File): Attributes {
    val jarFileAsJarFile = JarFile(jarFile)
    val manifestAttrs = jarFileAsJarFile.manifest.mainAttributes
    jarFileAsJarFile.close()
    return manifestAttrs
}

/**
 * 獲取運行服務器 jar 文件所需的 classpath。
 */
fun getClassPathToRunServerJar(jarFile: File): List<File> {
    val thisProjClasspathFileList = the<SourceSetContainer>()["main"].runtimeClasspath.files.toTypedArray()

    // 以空格分割 Manifest 的 Class-Path 屬性，得出包含所有 classpath 相對路徑字符串的集合
    val serverJarClasspathStrList =
            extractManifestAttributes(jarFile).getValue("Class-Path").split(' ')

    // 將所有 classpath 相對路徑字符串轉換爲具體 File
    val serverJarClasspathFileList =
            serverJarClasspathStrList.map { classPathStr ->
                // 求 classpath 相對於服務器 jar 所在目錄的 File
                jarFile.parentFile.toPath().resolve(classPathStr).toFile()
            }.toTypedArray()


    return listOf(
            jarFile,
            *thisProjClasspathFileList,
            *serverJarClasspathFileList
    )
}


tasks.register("runLocalMcServerTask", JavaExec::class.java) {
    dependsOn(tasks.getByName("classes"))

    val configExtension = project.extensions.getByType(RunLocalMcServerPluginConfigExtension::class.java)

    if (configExtension.enable) {
        val config = getConfigFromExtension(configExtension)
        println(config.localServerJar)

        val manifestAttrs = extractManifestAttributes(config.localServerJar)

        mainClass.set(manifestAttrs.getValue("Main-Class"))
        workingDir(config.localServerFolder)
        classpath(getClassPathToRunServerJar(config.localServerJar))
        standardInput = System.`in`
        extra(manifestAttrs.getValue("TweakClass"))
    } else {
        doFirst {
            println("RunLocalMcServerPlugin 禁用了，所以跳過了 runLocalMcServerTask。")
            throw StopExecutionException()
        }
    }
}