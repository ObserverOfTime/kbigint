import java.net.URL
import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

val os: OperatingSystem = OperatingSystem.current()

plugins {
    `maven-publish`
    signing
    alias(libs.plugins.kotlin.mpp)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dokka)
}

kotlin {
    jvm()

    androidTarget {
        withSourcesJar(true)
        publishLibraryVariants("release")
    }

    js {
        moduleName = project.name

        browser {
            webpackTask {
                esModules.set(true)
            }

            testTask {
                useKarma {
                    useChromiumHeadless()
                }
            }
        }

        nodejs {
            testTask {
                useMocha()
            }
        }

        useEsModules()

        @Suppress("OPT_IN_USAGE")
        compilerOptions.target.set("es2015")
    }

    linuxX64 {}
    linuxArm64 {}

    mingwX64 {}

    macosX64 {}
    macosArm64 {}
    iosArm64 {}
    iosSimulatorArm64 {}

    applyDefaultHierarchyTemplate()

    jvmToolchain(17)

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":kbigint"))
                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlin.serialization.core)
            }
        }

        commonTest {
            languageSettings {
                @OptIn(ExperimentalKotlinGradlePluginApi::class)
                compilerOptions {
                    freeCompilerArgs.add("-Xexpect-actual-classes")
                }
            }

            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlin.serialization.json)
            }
        }

        jsTest {
            resources.srcDir(project(":kbigint").file("src/javascript"))
        }
    }
}

android {
    namespace = "$group.serialization"
    compileSdk = 35
    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        resValues = false
    }
}

tasks.dokkaHtmlPartial {
    moduleName.set("KBigInt Serialization")
    suppressInheritedMembers.set(false)
    pluginsMapConfiguration.set(
        mapOf(
            "org.jetbrains.dokka.base.DokkaBase" to
                """{"footerMessage": "(c) 2025 ObserverOfTime"}"""
        )
    )
    dokkaSourceSets.configureEach {
        jdkVersion.set(17)
        includes.from(file("README.md"))
        externalDocumentationLink {
            url.set(URL("https://kotlinlang.org/api/kotlinx.serialization/"))
        }
    }
}

tasks.withType<AbstractPublishToMaven>().configureEach {
    mustRunAfter(tasks.withType<Sign>())
}

tasks.create<Jar>("javadocJar") {
    group = "documentation"
    archiveClassifier.set("javadoc")
    from(files("README.md"))
}

publishing {
    publications.withType(MavenPublication::class) {
        artifact(tasks["javadocJar"])
        pom {
            name.set("KBigInt Serialization")
            description.set("The serialization module of the KBigInt library")
            url.set("https://observeroftime.github.io/kbigint/")
            inceptionYear.set("2024")
            licenses {
                license {
                    name.set("Apache License 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0")
                }
            }
            developers {
                developer {
                    id.set("ObserverOfTime")
                    name.set("Ioannis Somos")
                    email.set("chronobserver@disroot.org")
                    url.set("https://github.com/ObserverOfTime")
                }
            }
            scm {
                url.set("https://github.com/ObserverOfTime/kbigint")
                connection.set("scm:git:git://github.com/ObserverOfTime/kbigint.git")
                developerConnection.set("scm:git:ssh://github.com/ObserverOfTime/kbigint.git")
            }
            ciManagement {
                system.set("GitHub Actions")
                url.set("https://github.com/ObserverOfTime/kbigint/actions")
            }
        }
    }

    repositories {
        maven {
            name = "GitHub"
            url = uri("https://maven.pkg.github.com/observeroftime/kbigint")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }

        maven {
            name = "local"
            url = uri(rootProject.layout.buildDirectory.dir("repo"))
        }
    }
}

signing {
    isRequired = System.getenv("CI") != null
    if (isRequired) {
        val key = System.getenv("SIGNING_KEY")
        val password = System.getenv("SIGNING_PASSWORD")
        useInMemoryPgpKeys(key, password)
    }
    sign(publishing.publications)
}
