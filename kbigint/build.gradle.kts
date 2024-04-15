import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    `maven-publish`
    signing
    alias(libs.plugins.kotlin.mpp)
    alias(libs.plugins.android.library)
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

    linuxX64 {
        compilations.configureEach {
            cinterops.create("tommath") {
                val vendor = defFile.parentFile.resolveSibling("vendor")
                includeDirs.allHeaders(vendor.resolve("include"))
                extraOpts("-libraryPath", vendor.resolve("lib"))
            }
        }
    }

    jvmToolchain(17)

    sourceSets {
        commonMain {
            languageSettings {
                @OptIn(ExperimentalKotlinGradlePluginApi::class)
                compilerOptions {
                    freeCompilerArgs.add("-Xexpect-actual-classes")
                }
            }

            dependencies {
                implementation(libs.kotlin.stdlib)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

android {
    namespace = group.toString()
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
        create<MavenPublication>("kbigint") {
            from(components["kotlin"])
            pom {
                name.set("KBigInt")
                description.set("Kotlin Multiplatform BigInteger library")
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
            url = uri("https://maven.pkg.github.com/ObserverOfTime/kbigint")
            credentials {
                username = System.getenv("GITHUB_REPOSITORY_OWNER")
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
    sign(publishing.publications["kbigint"])
    if (isRequired) {
        val key = System.getenv("SIGNING_KEY")
        val password = System.getenv("SIGNING_PASSWORD")
        useInMemoryPgpKeys(key, password)
    }
}
