# Kached

<p align="center">
    <a href="https://bintray.com/faogustavo/maven/kached"><img src="https://img.shields.io/badge/dynamic/json.svg?label=latest%20release&url=https%3A%2F%2Fapi.bintray.com%2F%2Fpackages%2Ffaogustavo%2Fmaven%2Fkached%2Fversions%2F_latest&query=name&colorB=0094cd&style=for-the-badge" alt="Bintray"/></a>
    <a href="https://www.apache.org/licenses/LICENSE-2.0.html"><img src="https://img.shields.io/github/license/faogustavo/kached.svg?style=for-the-badge" alt="License"/></a>
    <a href="https://github.com/faogustavo/kached/issues"><img src="https://img.shields.io/github/issues/faogustavo/kached.svg?style=for-the-badge" alt="GitHub issues"/></a>
</p>

<p align="center">
    <a href="/"><img src="https://img.shields.io/badge/Kotlin%20Version-1.4.0-blue?style=for-the-badge&logo=Kotlin" alt="GitHub top language"/></a>
    <a href="/"><img src="https://img.shields.io/github/languages/top/faogustavo/kached.svg?style=for-the-badge&logoColor=white" alt="GitHub top language"/></a>
    <a href="/"><img src="https://img.shields.io/badge/KOTLIN%20MULTIPLATFORM-yes-green?style=for-the-badge" alt="GitHub top language"/></a>
</p>

An user-friendly, modular and secure key value storage.

```kotlin
// Create your kached instance
val cache = kached<String> {
    logger = SimpleLogger()
    serializer = GsonSerializer(Gson())
    storage = SimpleMemoryStorage()
}

// Put some value
cache.set("foo", "bar")

// Get the value
cache.get("foo") // Returns value or null

// Remove value
cache.unset("foo")

// Clear cache
cache.clear()
```

## How it works?

```
                     +--------------------------+
                     |                          |
              +------+       val data: T        +<--------+
              |      |                          |         |
              |      +--------------------------+         |
              |                                           |
              v                                           |
+-------------+------------+                +-------------+------------+
|                          |                |                          |
|  set(key, value)         |                |  parse(decryptedValue)   |
|                          |                |                          |
+-------------+------------+                +-------------+------------+
              |                                           ^
              |                                           |
+-------------v------------+                +-------------+------------+
|                          |                |                          |
|  serialize(value)        |                |  retrieveValue(key)      |
|                          |                |                          |
+-------------+------------+                +-------------+------------+
              |                                           ^
              |                                           |
+-------------v------------+                +-------------+------------+
|                          |                |                          |
|  encrypt(serialValue)    |                |  decrypt(valueFromStore) |
|                          |                |                          |
+-------------+------------+                +-------------+------------+
              |                                           ^
              |                                           |
+-------------v------------+                +-------------+------------+
|                          |                |                          |
|  store(encryptedValue)   |                |  get(key)                |
|                          |                |                          |
+-------------+------------+                +-------------+------------+
              |                                           ^
              |                                           |
              |       +--------------------------+        |
              |       |                          |        |
              +------>+       cache_value        +--------+
                      |                          |
                      +--------------------------+

```

## Installation

```groovy
// Add core dependency
implementation "io.kached:core:$version"

// Pick one serializer
implementation "io.kached:gson-serializer:$version"

// Pick one storage
implementation "io.kached:shared-prefs-storage:$version"
implementation "io.kached:simple-memory-storage:$version"

// [Optional] Choose one encryptor
// TODO: Implement encryption libs

// [Optional] Choose one logger
implementation "io.kached:simple-logger:$version"
```
