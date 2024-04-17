# Module KBigInt

Kotlin Multiplatform BigInteger library.

## Add the dependency

### Maven Central Repository

**Not available yet.**

### GitHub Package Registry

Add these to `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.github.observeroftime.kbigint:kbigint")
}

repositories {
    maven {
        url = uri("https://maven.pkg.github.com/observeroftime/kbigint")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}
```

You need to set your GitHub credentials either in the `gpr.user` & `gpr.token` properties,
or in the `GITHUB_ACTOR` & `GITHUB_TOKEN` environment variables. (`GITHUB_ACTOR` is automatically
set in GitHub actions and `GITHUB_TOKEN` can be set to `${{github.token}}` in `env`.)

## Use the library

```kotlin
import io.github.observeroftime.kbigint.KBigInt

val number = KBigInt("9007199254740991")
```

See the class docs for supported methods.
