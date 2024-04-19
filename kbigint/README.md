# Module KBigInt

Kotlin Multiplatform BigInteger library.

## Add the dependency

Add these to `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.github.observeroftime.kbigint:kbigint")
}

repositories {
    mavenCentral()
}
```

## Use the library

```kotlin
import io.github.observeroftime.kbigint.KBigInt

val number = KBigInt("9007199254740991")
```

See the [class docs] for supported methods.

[class docs]: https://observeroftime.github.io/kbigint/kbigint/io.github.observeroftime.kbigint/-k-big-int/index.html
