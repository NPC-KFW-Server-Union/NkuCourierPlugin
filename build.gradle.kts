import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.spongepowered.gradle.plugin.config.PluginLoaders
import org.spongepowered.plugin.metadata.model.PluginDependency

repositories {
    mavenLocal()
    maven(url = "https://jitpack.io")
}

dependencies {
    // Spring framework
    implementation("org.springframework:spring-core:5.3.20")
    implementation("org.springframework:spring-context:5.3.20")
    implementation("org.springframework:spring-test:5.3.20")

    // Apache commons-validator
    implementation("commons-validator:commons-validator:1.9.0")

    // 確保測試中可用 Sponge api.
    testRuntimeOnly("org.spongepowered:spongeapi:7.4.0")

    // MiraiHttp (NPC&KFW 修改版)
    implementation("com.github.NPC-KFW-Server-Union:MiraiHttp:1.1.0-nku")
    //implementation("io.github.xiaoyi311:MiraiHttp:1.1.0-nku")

    // Junit
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

    // Other things for test
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple
    testImplementation("org.slf4j:slf4j-simple:2.0.13")

    //testRuntimeOnly("org.slf4j:slf4j-api:1.7.25")
}

application {
    // Make shadowjar happy.
    mainClass.set("top.eati.npc_kfw_union.plugin.courier.NkuCourierPlugin")
}

plugins {
    `java-library`
    id("org.spongepowered.gradle.plugin") version "2.1.1"
    id("run-local-mc-server")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("java")
}

group = "top.eati.npc_kfw_union.plugin"
version = "1.0-SNAPSHOT"

runLocalMcServer {
    enable = false
//    localServerPath = "/run/media/chihhao-su/PROJECT/GAME/NPCCRAFT/sponge_test_server/"
//    localServerPluginPath = "/run/media/chihhao-su/PROJECT/GAME/NPCCRAFT/sponge_test_server/mods/plugins"
//    localServerJarPath = "/run/media/chihhao-su/PROJECT/GAME/NPCCRAFT/sponge_test_server/forge-1.12.2-14.23.5.2860.jar"
}

tasks {
    named<ShadowJar>("shadowJar") {
        relocate("org.apache.common", "shadow.org.apache.common")
    }
    named<Test>("test") {
        useJUnitPlatform()
    }
}


sponge {
    apiVersion("7.4.0")
    license("All-Rights-Reserved")
    loader {
        name(PluginLoaders.JAVA_PLAIN)
        version("1.0")
    }
    plugin("nku-mc-msg-2-mirai") {
        displayName("NKU Courier Plugin")
        entrypoint("top.eati.npc_kfw_union.plugin.courier.NkuCourierPlugin")
        description("Bridge minecraft player's chat with Mirai")
        links {
            // homepage("https://spongepowered.org")
            // source("https://spongepowered.org/source")
            // issues("https://spongepowered.org/issues")
        }
        dependency("spongeapi") {
            loadOrder(PluginDependency.LoadOrder.AFTER)
            optional(false)
        }
    }
}



val javaTarget = 8 // Sponge targets a minimum of Java 8
java {
    sourceCompatibility = JavaVersion.toVersion(javaTarget)
    targetCompatibility = JavaVersion.toVersion(javaTarget)
    if (JavaVersion.current() < JavaVersion.toVersion(javaTarget)) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(javaTarget))
    }
}

tasks.withType(JavaCompile::class).configureEach {
    options.apply {
        encoding = "utf-8" // Consistent source file encoding
        if (JavaVersion.current().isJava10Compatible) {
            release.set(javaTarget)
        }
    }
}

tasks.withType(Jar::class) {
    // [FML]: There was a problem reading the entry META-INF/versions/9/module-info.class
    // in the jar McMsg2Mirai-1.0-SNAPSHOT-all.jar - probably a corrupt zip
    exclude("META-INF/versions/9/module-info.class")
}


// Make sure all tasks which produce archives (jar, sources jar, javadoc jar, etc) produce more consistent output
tasks.withType(AbstractArchiveTask::class).configureEach {
    isReproducibleFileOrder = true
    isPreserveFileTimestamps = false
}

tasks.register("findDependenciesIncludeTheFile") {
    doLast {
        configurations.runtimeClasspath.get().forEach { file ->
            if (file.isDirectory) {
                val targetFile = file.resolve("META-INF/versions/9/module-info.class")
                if (targetFile.exists()) {
                    println("Found in: $file")
                }
            } else if (file.name.endsWith(".jar")) {
                zipTree(file).matching { include("META-INF/versions/9/module-info.class") }.forEach { _ ->
                    println("Found in: $file")
                }
            }
        }
    }
}