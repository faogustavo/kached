package io.kached.impl

import io.kached.Encryptor
import io.kached.LogLevel
import io.kached.Logger
import io.kached.Serializer
import io.kached.Storage
import io.kached.StorageType
import io.kached.kached
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import kotlin.reflect.typeOf

@ExperimentalCoroutinesApi
class KachedImplTest {

    class Person(val id: String, val name: String) {
        companion object {
            val KEY = "user"
            val INSTANCE = Person("1", "Gustavo")
            val SERIAL_VALUE = "serial-value"
            val ENCRYPTED_VALUE = "encrypted-value"
        }
    }

    object LoggerError : Throwable()
    object SerializationError : Throwable()
    object EncryptorError : Throwable()
    object StorageException : Throwable()
    object MemoryStorageException : Throwable()

    private lateinit var logger: Logger
    private lateinit var serializer: Serializer
    private lateinit var encryptor: Encryptor
    private lateinit var storage: Storage<String>
    private lateinit var memoryStorage: Storage<Person>
    private var storageType = StorageType.ONLY_DISK

    val subject by lazy {
        kached<Person> {
            this.logger = this@KachedImplTest.logger
            this.serializer = this@KachedImplTest.serializer
            this.encryptor = this@KachedImplTest.encryptor
            this.storage = this@KachedImplTest.storage
            this.storageType = this@KachedImplTest.storageType
            this.memoryStorageBuilder = { memoryStorage }
        }
    }

    @Test
    fun set_shouldSerializeEncryptAndStore() = runBlockingTest {
        storageType = StorageType.ONLY_DISK
        mockLogger(throwError = false)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = false)
        mockStorage(throwError = false)

        subject.set(Person.KEY, Person.INSTANCE)

        coVerify(exactly = 1) { serializer.serialize(Person.INSTANCE, Person::class, typeOf<Person>()) }
        coVerify(exactly = 1) { encryptor.encrypt(Person.SERIAL_VALUE) }
        coVerify(exactly = 1) { storage.set(Person.KEY, Person.ENCRYPTED_VALUE) }
    }

    @Test
    fun set_shouldLogSteps() = runBlockingTest {
        storageType = StorageType.ONLY_DISK
        mockLogger(throwError = false)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = false)
        mockStorage(throwError = false)

        subject.set(Person.KEY, Person.INSTANCE)

        coVerify(exactly = 1) { logger.log("Kached -> set(${Person.KEY})", LogLevel.Info) }
        coVerify(exactly = 1) { logger.log("Storing value in disk", LogLevel.Info) }
    }

    @Test
    fun set_withMemoryStorage_savesItToMemory() = runBlockingTest {
        storageType = StorageType.ONLY_MEMORY
        mockMemoryStorage(throwError = false)
        mockLogger()
        mockSerializer()
        mockEncryptor()
        mockStorage()

        subject.set(Person.KEY, Person.INSTANCE)

        coVerify(exactly = 1) { memoryStorage.set(Person.KEY, Person.INSTANCE) }
    }

    @Test
    fun set_withMemoryStorage_shouldLogSteps() = runBlockingTest {
        storageType = StorageType.ONLY_MEMORY
        mockMemoryStorage(throwError = false)
        mockLogger()
        mockSerializer()
        mockEncryptor()
        mockStorage()

        subject.set(Person.KEY, Person.INSTANCE)

        coVerify(exactly = 1) { logger.log("Kached -> set(${Person.KEY})", LogLevel.Info) }
        coVerify(exactly = 1) { logger.log("Storing value in memory", LogLevel.Info) }
    }

    @Test
    fun get_shouldGetFromStoreDecryptAndDeserialize() = runBlockingTest {
        storageType = StorageType.ONLY_DISK
        mockLogger(throwError = false)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = false)
        mockStorage(throwError = false, hasValue = true)

        subject.get(Person.KEY)

        coVerify(exactly = 1) { storage.get(Person.KEY) }
        coVerify(exactly = 1) { encryptor.decrypt(Person.ENCRYPTED_VALUE) }
        coVerify(exactly = 1) { serializer.deserialize(Person.SERIAL_VALUE, Person::class, typeOf<Person>()) }
    }

    @Test
    fun get_withStoreReturningNull_returnsImmediately() = runBlockingTest {
        storageType = StorageType.ONLY_DISK
        mockLogger(throwError = false)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = false)
        mockStorage(throwError = false, hasValue = false)

        subject.get(Person.KEY)

        coVerify(exactly = 1) { storage.get(Person.KEY) }
        coVerify(exactly = 1) { logger.log("There is no value for key = ${Person.KEY}", LogLevel.Warning) }
        coVerify(exactly = 0) { encryptor.decrypt(Person.ENCRYPTED_VALUE) }
        coVerify(exactly = 0) { serializer.deserialize(any(), any(), any()) }
    }

    @Test
    fun get_shouldLogSteps() = runBlockingTest {
        mockLogger(throwError = false)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = false)
        mockStorage(throwError = false, hasValue = true)

        subject.get(Person.KEY)

        coVerify(exactly = 1) { logger.log("Kached -> get(${Person.KEY})", LogLevel.Info) }
        coVerify(exactly = 1) { logger.log("Trying to get value from disk", LogLevel.Info) }
    }

    @Test
    fun get_withMemoryStorage_getValueFromIt() = runBlockingTest {
        storageType = StorageType.ONLY_MEMORY
        mockMemoryStorage(throwError = false)
        mockLogger()
        mockSerializer()
        mockEncryptor()
        mockStorage()

        subject.get(Person.KEY)

        coVerify(exactly = 1) { memoryStorage.get(Person.KEY) }
    }

    @Test
    fun get_withMixedStorageAndMemoryValue_notGetFromStorage() = runBlockingTest {
        storageType = StorageType.MIXED
        mockMemoryStorage(throwError = false, hasValue = true)
        mockLogger()
        mockSerializer()
        mockEncryptor()
        mockStorage()

        subject.get(Person.KEY)

        coVerify(exactly = 1) { memoryStorage.get(Person.KEY) }
        coVerify(exactly = 0) { storage.get(Person.KEY) }
    }

    @Test
    fun get_withMixedStorageAndNoMemoryValue_getFromStorage() = runBlockingTest {
        storageType = StorageType.MIXED
        mockMemoryStorage(throwError = false, hasValue = false)
        mockLogger(throwError = false)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = false)
        mockStorage(throwError = false, hasValue = true)

        subject.get(Person.KEY)

        coVerify(exactly = 1) { memoryStorage.get(Person.KEY) }
        coVerify(exactly = 1) { storage.get(Person.KEY) }
        coVerify(exactly = 1) { encryptor.decrypt(Person.ENCRYPTED_VALUE) }
        coVerify(exactly = 1) { serializer.deserialize(Person.SERIAL_VALUE, Person::class, typeOf<Person>()) }
        coVerify(exactly = 1) { memoryStorage.set(Person.KEY, Person.INSTANCE) }
    }

    @Test
    fun get_withMemoryStorage_shouldLogSteps() = runBlockingTest {
        storageType = StorageType.ONLY_MEMORY
        mockMemoryStorage(throwError = false)
        mockLogger(throwError = false)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = false)
        mockStorage(throwError = false, hasValue = true)

        subject.get(Person.KEY)

        coVerify(exactly = 1) { logger.log("Kached -> get(${Person.KEY})", LogLevel.Info) }
        coVerify(exactly = 1) { logger.log("Trying to get value from memory", LogLevel.Info) }
    }

    @Test
    fun unset_shouldCallStorage() = runBlockingTest {
        storageType = StorageType.ONLY_DISK
        mockLogger(throwError = false)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = false)
        mockStorage(throwError = false, hasValue = true)

        subject.unset(Person.KEY)

        coVerify { storage.unset(Person.KEY) }
    }

    @Test
    fun unset_shouldLogSteps() = runBlockingTest {
        storageType = StorageType.ONLY_DISK
        mockLogger(throwError = false)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = false)
        mockStorage(throwError = false, hasValue = true)

        subject.unset(Person.KEY)

        coVerify(exactly = 1) { logger.log("Kached -> unset(${Person.KEY})", LogLevel.Info) }
        coVerify(exactly = 1) { logger.log("Removing value from disk", LogLevel.Info) }
    }

    @Test
    fun unset_withMemoryStorage_shouldCallStorage() = runBlockingTest {
        storageType = StorageType.ONLY_MEMORY
        mockMemoryStorage(throwError = false)
        mockLogger(throwError = false)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = false)
        mockStorage(throwError = false, hasValue = true)

        subject.unset(Person.KEY)

        coVerify { memoryStorage.unset(Person.KEY) }
    }

    @Test
    fun unset_withMemoryStorage_shouldLogSteps() = runBlockingTest {
        storageType = StorageType.ONLY_MEMORY
        mockMemoryStorage(throwError = false)
        mockLogger(throwError = false)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = false)
        mockStorage(throwError = false, hasValue = true)

        subject.unset(Person.KEY)

        coVerify(exactly = 1) { logger.log("Kached -> unset(${Person.KEY})", LogLevel.Info) }
        coVerify(exactly = 1) { logger.log("Removing value from memory", LogLevel.Info) }
    }

    @Test
    fun clear_shouldCallStorage() = runBlockingTest {
        storageType = StorageType.ONLY_DISK
        mockLogger(throwError = false)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = false)
        mockStorage(throwError = false, hasValue = true)

        subject.clear()

        coVerify(exactly = 1) { storage.clear() }
    }

    @Test
    fun clear_shouldLogSteps() = runBlockingTest {
        storageType = StorageType.ONLY_DISK
        mockLogger(throwError = false)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = false)
        mockStorage(throwError = false, hasValue = true)

        subject.clear()

        coVerify(exactly = 1) { logger.log("Kached -> clear()", LogLevel.Info) }
        coVerify(exactly = 1) { logger.log("Clearing values from disk", LogLevel.Info) }
    }

    @Test
    fun clear_withMemoryStorage_shouldCallStorage() = runBlockingTest {
        storageType = StorageType.ONLY_MEMORY
        mockMemoryStorage(throwError = false)
        mockLogger(throwError = false)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = false)
        mockStorage(throwError = false, hasValue = true)

        subject.clear()

        coVerify(exactly = 1) { memoryStorage.clear() }
    }

    @Test
    fun clear_withMemoryStorage_shouldLogSteps() = runBlockingTest {
        storageType = StorageType.ONLY_MEMORY
        mockMemoryStorage(throwError = false)
        mockLogger(throwError = false)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = false)
        mockStorage(throwError = false, hasValue = true)

        subject.clear()

        coVerify(exactly = 1) { logger.log("Kached -> clear()", LogLevel.Info) }
        coVerify(exactly = 1) { logger.log("Clearing values from memory", LogLevel.Info) }
    }

    @Test
    fun set_withErrorSerializingValue_logException() = runBlockingTest {
        storageType = StorageType.ONLY_DISK
        mockLogger(throwError = false)
        mockSerializer(throwError = true)
        mockEncryptor(throwError = false)
        mockStorage(throwError = false)

        subject.set(Person.KEY, Person.INSTANCE)

        coVerify(exactly = 1) { serializer.serialize(Person.INSTANCE, Person::class, typeOf<Person>()) }
        coVerify(exactly = 0) { storage.set(any(), any()) }
        coVerify(exactly = 0) { encryptor.encrypt(Person.SERIAL_VALUE) }
        coVerify(exactly = 1) { logger.log("Failed to serialize value with key = ${Person.KEY}", LogLevel.Warning) }
        coVerify(exactly = 1) { logger.log(SerializationError, LogLevel.Error) }
    }

    @Test
    fun set_withErrorEncryptingValue_logException() = runBlockingTest {
        storageType = StorageType.ONLY_DISK
        mockLogger(throwError = false)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = true)
        mockStorage(throwError = false)

        subject.set(Person.KEY, Person.INSTANCE)

        coVerify(exactly = 1) { encryptor.encrypt(Person.SERIAL_VALUE) }
        coVerify(exactly = 0) { storage.set(any(), any()) }
        coVerify(exactly = 1) { logger.log("Failed to encrypt data with key = ${Person.KEY}", LogLevel.Warning) }
        coVerify(exactly = 1) { logger.log(EncryptorError, LogLevel.Error) }
    }

    @Test
    fun set_withErrorStoringValue_logException() = runBlockingTest {
        storageType = StorageType.ONLY_DISK
        mockLogger(throwError = false)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = false)
        mockStorage(throwError = true)

        subject.set(Person.KEY, Person.INSTANCE)

        coVerify(exactly = 1) { storage.set(Person.KEY, Person.ENCRYPTED_VALUE) }
        coVerify(exactly = 1) { logger.log("Failed to store data with key = ${Person.KEY}", LogLevel.Warning) }
        coVerify(exactly = 1) { logger.log(StorageException, LogLevel.Error) }
    }

    @Test
    fun set_withMemoryStorageAndErrorStoringValue_logException() = runBlockingTest {
        storageType = StorageType.ONLY_MEMORY
        mockMemoryStorage(throwError = true)
        mockLogger()
        mockSerializer()
        mockEncryptor()
        mockStorage()

        subject.set(Person.KEY, Person.INSTANCE)

        coVerify(exactly = 1) { memoryStorage.set(Person.KEY, Person.INSTANCE) }
        coVerify(exactly = 1) {
            logger.log(
                "Failed to store data with key = ${Person.KEY} in memory",
                LogLevel.Warning
            )
        }
    }

    @Test
    fun get_withErrorGettingFromStore_logException() = runBlockingTest {
        storageType = StorageType.ONLY_DISK
        mockLogger(throwError = false)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = false)
        mockStorage(throwError = true, hasValue = false)

        subject.get(Person.KEY)

        coVerify(exactly = 1) { storage.get(Person.KEY) }
        coVerify(exactly = 1) {
            logger.log(
                "Failed to acquire data from storage with key = ${Person.KEY}",
                LogLevel.Warning
            )
        }
        coVerify(exactly = 1) { logger.log(StorageException, LogLevel.Error) }
        coVerify(exactly = 0) { encryptor.decrypt(any()) }
        coVerify(exactly = 0) { serializer.deserialize(any(), any(), any()) }
    }

    @Test
    fun get_withErrorDecryptingValue_logException() = runBlockingTest {
        storageType = StorageType.ONLY_DISK
        mockLogger(throwError = false)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = true)
        mockStorage(throwError = false, hasValue = true)

        subject.get(Person.KEY)

        coVerify(exactly = 1) { encryptor.decrypt(Person.ENCRYPTED_VALUE) }
        coVerify(exactly = 1) { logger.log("Failed to decrypt data with key = ${Person.KEY}", LogLevel.Warning) }
        coVerify(exactly = 1) { logger.log(EncryptorError, LogLevel.Error) }
        coVerify(exactly = 0) { serializer.deserialize(any(), any(), any()) }
    }

    @Test
    fun get_withErrorDeserializing_logException() = runBlockingTest {
        storageType = StorageType.ONLY_DISK
        mockLogger(throwError = false)
        mockSerializer(throwError = true)
        mockEncryptor(throwError = false)
        mockStorage(throwError = false, hasValue = true)

        subject.get(Person.KEY)

        coVerify(exactly = 1) { serializer.deserialize(Person.SERIAL_VALUE, Person::class, typeOf<Person>()) }
        coVerify(exactly = 1) { logger.log("Failed to deserialize data with key = ${Person.KEY}", LogLevel.Warning) }
        coVerify(exactly = 1) { logger.log(SerializationError, LogLevel.Error) }
    }

    @Test
    fun get_withMemoryStorageAndErrorStoringValue_logException() = runBlockingTest {
        storageType = StorageType.ONLY_MEMORY
        mockMemoryStorage(throwError = true)
        mockLogger()
        mockSerializer()
        mockEncryptor()
        mockStorage()

        subject.get(Person.KEY)

        coVerify(exactly = 1) { memoryStorage.get(Person.KEY) }
        coVerify(exactly = 1) {
            logger.log(
                "Failed to acquire data from memory storage with key = ${Person.KEY}",
                LogLevel.Warning
            )
        }
    }

    @Test
    fun unset_withError_logException() = runBlockingTest {
        storageType = StorageType.ONLY_DISK
        mockLogger(throwError = false)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = false)
        mockStorage(throwError = true, hasValue = true)

        subject.unset(Person.KEY)

        coVerify(exactly = 1) { storage.unset(Person.KEY) }
        coVerify(exactly = 1) { logger.log("Failed to unset value where key = ${Person.KEY}", LogLevel.Warning) }
    }

    @Test
    fun unset_withMemoryStorag1eAndError_logException() = runBlockingTest {
        storageType = StorageType.ONLY_MEMORY
        mockMemoryStorage(throwError = true)
        mockLogger()
        mockSerializer()
        mockEncryptor()
        mockStorage()

        subject.unset(Person.KEY)

        coVerify(exactly = 1) { memoryStorage.unset(Person.KEY) }
        coVerify(exactly = 1) {
            logger.log(
                "Failed to unset value from memory where key = ${Person.KEY}",
                LogLevel.Warning
            )
        }
    }

    @Test
    fun clear_withError_logException() = runBlockingTest {
        storageType = StorageType.ONLY_DISK
        mockLogger(throwError = false)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = false)
        mockStorage(throwError = true, hasValue = true)

        subject.clear()

        coVerify(exactly = 1) { storage.clear() }
        coVerify(exactly = 1) { logger.log("Failed to clear storage", LogLevel.Warning) }
    }

    @Test
    fun clear_withMemoryStorageAndError_logException() = runBlockingTest {
        storageType = StorageType.ONLY_MEMORY
        mockMemoryStorage(throwError = true)
        mockLogger()
        mockSerializer()
        mockEncryptor()
        mockStorage()

        subject.clear()

        coVerify(exactly = 1) { memoryStorage.clear() }
        coVerify(exactly = 1) { logger.log("Failed to clear memory storage", LogLevel.Warning) }
    }

    @Test
    fun set_withLoggerThrowingException_tryLogCatchException() = runBlockingTest {
        storageType = StorageType.ONLY_DISK
        mockLogger(throwError = true)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = false)
        mockStorage(throwError = false)

        subject.set(Person.KEY, Person.INSTANCE)

        coVerify(exactly = 1) { logger.log("Kached -> set(${Person.KEY})", LogLevel.Info) }
        coVerify(exactly = 2) { logger.log(LoggerError, LogLevel.Error) }
    }

    @Test
    fun get_withLoggerThrowingException_tryLogCatchException() = runBlockingTest {
        storageType = StorageType.ONLY_DISK
        mockLogger(throwError = true)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = false)
        mockStorage(throwError = false, hasValue = true)

        subject.get(Person.KEY)

        coVerify(exactly = 1) { logger.log("Kached -> get(${Person.KEY})", LogLevel.Info) }
        coVerify(exactly = 2) { logger.log(LoggerError, LogLevel.Error) }
    }

    @Test
    fun unset_withLoggerThrowingException_tryLogCatchException() = runBlockingTest {
        storageType = StorageType.ONLY_DISK
        mockLogger(throwError = true)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = false)
        mockStorage(throwError = false, hasValue = true)

        subject.unset(Person.KEY)

        coVerify(exactly = 1) { logger.log("Kached -> unset(${Person.KEY})", LogLevel.Info) }
        coVerify(exactly = 2) { logger.log(LoggerError, LogLevel.Error) }
    }

    @Test
    fun clear_withLoggerThrowingException_tryLogCatchException() = runBlockingTest {
        storageType = StorageType.ONLY_DISK
        mockLogger(throwError = true)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = false)
        mockStorage(throwError = false, hasValue = true)

        subject.clear()

        coVerify(exactly = 1) { logger.log("Kached -> clear()", LogLevel.Info) }
        coVerify(exactly = 2) { logger.log(LoggerError, LogLevel.Error) }
    }

    private fun mockLogger(
        throwError: Boolean = false,
    ) {
        logger = if (throwError) {
            mockk {
                coEvery { log(any<String>(), any()) } throws LoggerError
                coEvery { log(any<Throwable>(), any()) } throws LoggerError
            }
        } else {
            mockk {
                coEvery { log(any<String>(), any()) } just Runs
                coEvery { log(any<Throwable>(), any()) } just Runs
            }
        }
    }

    private fun mockSerializer(
        throwError: Boolean = false,
    ) {
        serializer = if (throwError) {
            mockk {
                coEvery { serialize(any(), any(), any()) } throws SerializationError
                coEvery { deserialize<Person>(any(), any(), any()) } throws SerializationError
            }
        } else {
            mockk {
                coEvery { serialize(any(), any(), any()) } returns Person.SERIAL_VALUE
                coEvery { deserialize<Person>(any(), any(), any()) } returns Person.INSTANCE
            }
        }
    }

    private fun mockEncryptor(
        throwError: Boolean = false,
    ) {
        encryptor = if (throwError) {
            mockk {
                coEvery { encrypt(any()) } throws EncryptorError
                coEvery { decrypt(any()) } throws EncryptorError
            }
        } else {
            mockk {
                coEvery { encrypt(any()) } returns Person.ENCRYPTED_VALUE
                coEvery { decrypt(any()) } returns Person.SERIAL_VALUE
            }
        }
    }

    private fun mockStorage(
        throwError: Boolean = false,
        hasValue: Boolean = false,
    ) {
        storage = when {
            throwError -> {
                mockk {
                    coEvery { this@mockk.get(any()) } throws StorageException
                    coEvery { set(any(), any()) } throws StorageException
                    coEvery { unset(any()) } throws StorageException
                    coEvery { clear() } throws StorageException
                }
            }
            hasValue -> {
                mockk {
                    coEvery { this@mockk.get(any()) } returns Person.ENCRYPTED_VALUE
                    coEvery { set(any(), any()) } just Runs
                    coEvery { unset(any()) } just Runs
                    coEvery { clear() } just Runs
                }
            }
            else -> {
                mockk {
                    coEvery { this@mockk.get(any()) } returns null
                    coEvery { set(any(), any()) } just Runs
                    coEvery { unset(any()) } just Runs
                    coEvery { clear() } just Runs
                }
            }
        }
    }

    private fun mockMemoryStorage(
        throwError: Boolean = false,
        hasValue: Boolean = false,
    ) {
        memoryStorage = when {
            throwError -> {
                mockk {
                    coEvery { this@mockk.get(any()) } throws MemoryStorageException
                    coEvery { set(any(), any()) } throws MemoryStorageException
                    coEvery { unset(any()) } throws MemoryStorageException
                    coEvery { clear() } throws MemoryStorageException
                }
            }
            hasValue -> {
                mockk {
                    coEvery { this@mockk.get(any()) } returns Person.INSTANCE
                    coEvery { set(any(), any()) } just Runs
                    coEvery { unset(any()) } just Runs
                    coEvery { clear() } just Runs
                }
            }
            else -> {
                mockk {
                    coEvery { this@mockk.get(any()) } returns null
                    coEvery { set(any(), any()) } just Runs
                    coEvery { unset(any()) } just Runs
                    coEvery { clear() } just Runs
                }
            }
        }
    }
}
