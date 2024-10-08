import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin

plugins {
    alias(libs.plugins.kotlin.mpp) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.dokka)
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

subprojects {
    group = "io.github.observeroftime.kbigint"
    version = property("project.version") as String

    if (System.getenv("CI") != null) {
        tasks.withType(AbstractTestTask::class) {
            testLogging.events("passed", "skipped", "failed")
        }
    }
}

plugins.withType<NodeJsRootPlugin> {
    the<NodeJsRootExtension>().download = false
}

tasks.dokkaHtmlMultiModule {
    moduleName.set("KBigInt")
    includes.from("README.md")
    pluginsMapConfiguration.set(
        mapOf(
            "org.jetbrains.dokka.base.DokkaBase" to
                """{"footerMessage": "(c) 2024 ObserverOfTime"}"""
        )
    )
}

tasks.wrapper {
    gradleVersion = "8.10"
    distributionType = Wrapper.DistributionType.BIN
}
