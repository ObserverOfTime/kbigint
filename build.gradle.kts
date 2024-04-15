import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnLockMismatchReport
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension

plugins {
    alias(libs.plugins.kotlin.mpp) apply false
    alias(libs.plugins.android.library) apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

subprojects {
    group = "io.github.observeroftime.kbigint"
    version = "0.1.0"

    if (System.getenv("CI") != null) {
        tasks.withType(AbstractTestTask::class) {
            testLogging.events("passed", "skipped", "failed")
        }
    }
}

plugins.withType<NodeJsRootPlugin> {
    the<NodeJsRootExtension>().download = false
}

plugins.withType<YarnPlugin> {
    the<YarnRootExtension>().apply {
        download = false
        yarnLockAutoReplace = true
        yarnLockMismatchReport = YarnLockMismatchReport.WARNING
    }
}

tasks.named<Wrapper>("wrapper") {
    gradleVersion = "8.7"
    distributionType = Wrapper.DistributionType.BIN
}
