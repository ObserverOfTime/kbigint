import java.net.URL
import org.gradle.internal.os.OperatingSystem
import org.jetbrains.dokka.gradle.DokkaTaskPartial
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
        publishLibraryVariants("release")
    }

    js(IR) {
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
    }

    when {
        os.isLinux -> {
            linuxX64()
            linuxArm64()
        }
        os.isWindows -> {
            mingwX64()
        }
        os.isMacOsX -> {
            macosX64()
            macosArm64()
            iosArm64()
            iosSimulatorArm64()
        }
        else -> {
            val arch = System.getProperty("os.arch")
            throw GradleException("Unsupported platform: $os ($arch)")
        }
    }

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
    compileSdk = 34
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

publishing {
    publications {
        create<MavenPublication>("kbigintSerialization") {
            from(components["kotlin"])
            pom {
                name.set("KBigInt Serialization")
                description.set("The serialization module of the KBigInt library")
                url.set("https://observeroftime.github.io/kbigint/")
                inceptionYear.set("2024")
                licenses {
                    license {
                        name.set("Apache License 2.0")
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
                }
                ciManagement {
                    system.set("GitHub Actions")
                    url.set("https://github.com/ObserverOfTime/kbigint/actions")
                }
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
            url = uri(rootProject.layout.buildDirectory.dir("repos"))
        }
    }
}

signing {
    isRequired = System.getenv("CI") != null
    sign(publishing.publications["kbigintSerialization"])
    if (isRequired) {
        val key = System.getenv("SIGNING_KEY")
        val password = System.getenv("SIGNING_PASSWORD")
        useInMemoryPgpKeys(key, password)
    }
}

tasks.withType<DokkaTaskPartial>().configureEach {
    moduleName.set("KBigInt Serialization")
    suppressInheritedMembers.set(false)
    pluginsMapConfiguration.set(
        mapOf(
            "org.jetbrains.dokka.base.DokkaBase" to
                """{"footerMessage": "(c) 2024 ObserverOfTime"}"""
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
