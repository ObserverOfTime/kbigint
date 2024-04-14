import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension

plugins {
    alias(libs.plugins.kotlin.mpp) apply false
    alias(libs.plugins.android.library) apply false
}

allprojects {
    group = "io.github.observeroftime.kbigint"
    version = "0.1.0"

    repositories {
        google()
        mavenCentral()
    }
}

plugins.withType<NodeJsRootPlugin> {
    the<NodeJsRootExtension>().download = false
}

plugins.withType<YarnPlugin> {
    the<YarnRootExtension>().download = false
}

tasks.wrapper {
    gradleVersion = "8.7"
    distributionType = Wrapper.DistributionType.BIN
}
