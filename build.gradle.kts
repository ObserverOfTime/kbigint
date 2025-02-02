import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsPlugin

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

dependencies {
    dokka(project(":kbigint"))
    dokka(project(":kbigint-serialization"))
}

plugins.withType<NodeJsPlugin> {
    the<NodeJsEnvSpec>().download = false
}

dokka {
    moduleName.set("KBigInt")
    dokkaSourceSets.configureEach {
        includes.from("README.md")
    }
    pluginsConfiguration.html {
        footerMessage.set("(c) 2024-2025 ObserverOfTime")
    }
}

tasks.wrapper {
    gradleVersion = "8.10.2"
    distributionType = Wrapper.DistributionType.BIN
}
