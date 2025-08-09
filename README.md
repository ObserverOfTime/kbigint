# KBigInt

Kotlin Multiplatform BigInteger library.

<div style="margin-top:16px">
  <a href="https://github.com/ObserverOfTime/kbigint/actions/workflows/ci.yml" style="border-bottom:unset">
    <img alt="CI" src="https://img.shields.io/github/actions/workflow/status/ObserverOfTime/kbigint/ci.yml?logo=github&label=CI"/>
  </a>
  <a href="https://observeroftime.github.io/kbigint/" style="border-bottom:unset">
    <img alt="Pages" src="https://img.shields.io/github/deployments/ObserverOfTime/kbigint/github-pages?logo=kotlin&label=Documentation"/>
  </a>
  <a href="https://central.sonatype.com/artifact/io.github.observeroftime.kbigint/kbigint" style="border-bottom:unset">
    <img alt="Central" src="https://img.shields.io/maven-central/v/io.github.observeroftime.kbigint/kbigint?logo=sonatype&label=Maven%20Central"/>
  </a>
</div>

## Platforms

| Platform |          Implementation          |
|:--------:|:--------------------------------:|
|   JVM    |   [BigInteger][BigInteger-JVM]   |
| Android  | [BigInteger][BigInteger-Android] |
|    JS    |      [BigInt][]<sup>*</sup>      |
|  Native  |          [LibTomMath][]          |
|   WASM   |        _Not implemented_         |


[BigInteger-JVM]: https://docs.oracle.com/javase/17/docs/api/java.base/java/math/BigInteger.html
[BigInteger-Android]: https://developer.android.com/reference/java/math/BigInteger
[BigInt]: https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/BigInt
[LibTomMath]: https://github.com/libtom/libtommath/tree/master

<sup>*</sup> Kotlin/JS does not fully support `BigInt` ([KT-48980]).

[KT-48980]: https://youtrack.jetbrains.com/issue/KT-48980/

## Motivation

Kotlin does not support `BigInteger` and `BigDecimal` types in multiplatform projects ([KT-20912]).
<br/>However, I was not interested in `BigDecimal` so this library only implements `BigInteger`.
<br/>Serialization is implemented in a separate module so the main library can be used on its own.

[KT-20912]: https://youtrack.jetbrains.com/issue/KT-20912/

### Alternatives

#### [kotlin-multiplatform-bignum](https://github.com/ionspin/kotlin-multiplatform-bignum)

- Fully custom implementation.

#### [korlibs-bignumber](https://github.com/korlibs/korlibs-bignumber)

- No serialization.
- Custom Native implementation.

#### [kmath-core](https://github.com/SciProgCentre/kmath/tree/master/kmath-core)

- Maths library.
- No serialization.
- Fully custom implementation.

#### [mpbignum](https://codeberg.org/loke/array/src/branch/master/mpbignum)

- Internal library.
- No serialization.
- Depends on `Apache Commons Math` in JVM.
- Only Linux is supported in Native (using `gmp`).

#### [kmulti-bignumber](https://github.com/kmulti/kmulti-bignumber)

- No longer updated.
- No serialization.
- Stub JS implementation.
- No Native implementation.

## License

[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
