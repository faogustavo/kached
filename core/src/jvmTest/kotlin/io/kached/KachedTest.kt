package io.kached

import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
class KachedTest {

    class Person(val id: String, val name: String) {
        companion object {
            val KEY = "user"
            val INSTANCE = Person("1", "Gustavo")
            val SERIAL_VALUE = "serial-value"
            val ENCRYPTED_VALUE = "encrypted-value"
        }
    }

    object FakeException : Throwable()

    lateinit var logger: Logger
    lateinit var serializer: Serializer
    lateinit var encryptor: Encryptor
    lateinit var storage: Storage

    val subject by lazy {
        kached<Person> {
            this.logger = this@KachedTest.logger
            this.serializer = this@KachedTest.serializer
            this.encryptor = this@KachedTest.encryptor
            this.storage = this@KachedTest.storage
        }
    }

    @Test
    fun set_shouldSerializeEncryptAndStore() = runBlockingTest {
        mockLogger(throwError = false)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = false)
        mockStorage(throwError = false)

        subject.set(Person.KEY, Person.INSTANCE)

        coVerify(exactly = 1) { serializer.serialize(Person.INSTANCE) }
        coVerify(exactly = 1) { encryptor.encrypt(Person.SERIAL_VALUE) }
        coVerify(exactly = 1) { storage.put(Person.KEY, Person.ENCRYPTED_VALUE) }
    }

    @Test
    fun set_shouldLogSteps() = runBlockingTest {
        mockLogger(throwError = false)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = false)
        mockStorage(throwError = false)

        subject.set(Person.KEY, Person.INSTANCE)

        coVerify(exactly = 1) { logger.log("Kached -> set(${Person.KEY})") }
    }

    @Test
    fun get_shouldGetFromStoreDecryptAndDeserialize() = runBlockingTest {
        mockLogger(throwError = false)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = false)
        mockStorage(throwError = false, hasValue = true)

        subject.get(Person.KEY)

        coVerify(exactly = 1) { storage.get(Person.KEY) }
        coVerify(exactly = 1) { encryptor.decrypt(Person.ENCRYPTED_VALUE) }
        coVerify(exactly = 1) { serializer.deserialize(Person.SERIAL_VALUE) }
    }

    @Test
    fun get_withStoreReturningNull_returnsImmediately() = runBlockingTest {
        mockLogger(throwError = false)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = false)
        mockStorage(throwError = false, hasValue = false)

        subject.get(Person.KEY)

        coVerify(exactly = 1) { storage.get(Person.KEY) }
        coVerify(exactly = 1) { logger.log("There is no value for key = ${Person.KEY}") }
        coVerify(exactly = 0) { encryptor.decrypt(Person.ENCRYPTED_VALUE) }
        coVerify(exactly = 0) { serializer.deserialize(Person.SERIAL_VALUE) }
    }

    @Test
    fun get_shouldLogSteps() = runBlockingTest {
        mockLogger(throwError = false)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = false)
        mockStorage(throwError = false, hasValue = true)

        subject.get(Person.KEY)

        coVerify(exactly = 1) { logger.log("Kached -> get(${Person.KEY})") }
    }

    @Test
    fun set_withErrorSerializingValue_logException() = runBlockingTest {
        mockLogger(throwError = false)
        mockSerializer(throwError = true)
        mockEncryptor(throwError = false)
        mockStorage(throwError = false)

        subject.set(Person.KEY, Person.INSTANCE)

        coVerify(exactly = 1) { serializer.serialize(Person.INSTANCE) }
        coVerify(exactly = 0) { storage.put(any(), any()) }
        coVerify(exactly = 0) { encryptor.encrypt(Person.SERIAL_VALUE) }
        coVerify(exactly = 1) { logger.log("Failed to serialize value with key = ${Person.KEY}") }
        coVerify(exactly = 1) { logger.log(FakeException) }
    }

    @Test
    fun set_withErrorEncryptingValue_logException() = runBlockingTest {
        mockLogger(throwError = false)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = true)
        mockStorage(throwError = false)

        subject.set(Person.KEY, Person.INSTANCE)

        coVerify(exactly = 1) { encryptor.encrypt(Person.SERIAL_VALUE) }
        coVerify(exactly = 0) { storage.put(any(), any()) }
        coVerify(exactly = 1) { logger.log("Failed to encrypt data with key = ${Person.KEY}") }
        coVerify(exactly = 1) { logger.log(FakeException) }
    }

    @Test
    fun set_withErrorStoringValue_logException() = runBlockingTest {
        mockLogger(throwError = false)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = false)
        mockStorage(throwError = true)

        subject.set(Person.KEY, Person.INSTANCE)

        coVerify(exactly = 1) { storage.put(Person.KEY, Person.ENCRYPTED_VALUE) }
        coVerify(exactly = 1) { logger.log("Failed to store data with key = ${Person.KEY}") }
        coVerify(exactly = 1) { logger.log(FakeException) }
    }

    @Test
    fun get_withErrorGettingFromStore_logException() = runBlockingTest {
        mockLogger(throwError = false)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = false)
        mockStorage(throwError = true, hasValue = false)

        subject.get(Person.KEY)

        coVerify(exactly = 1) { storage.get(Person.KEY) }
        coVerify(exactly = 1) { logger.log("Failed to acquire data from storage with key = ${Person.KEY}") }
        coVerify(exactly = 0) { encryptor.decrypt(any()) }
        coVerify(exactly = 0) { serializer.deserialize(any()) }
    }

    @Test
    fun get_withErrorDecryptingValue_logException() = runBlockingTest {
        mockLogger(throwError = false)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = true)
        mockStorage(throwError = false, hasValue = true)

        subject.get(Person.KEY)

        coVerify(exactly = 1) { encryptor.decrypt(Person.ENCRYPTED_VALUE) }
        coVerify(exactly = 1) { logger.log("Failed to decrypt data with key = ${Person.KEY}") }
        coVerify(exactly = 0) { serializer.deserialize(any()) }
    }

    @Test
    fun get_withErrorDeserializing_logException() = runBlockingTest {
        mockLogger(throwError = false)
        mockSerializer(throwError = true)
        mockEncryptor(throwError = false)
        mockStorage(throwError = false, hasValue = true)

        subject.get(Person.KEY)

        coVerify(exactly = 1) { serializer.deserialize(Person.SERIAL_VALUE) }
        coVerify(exactly = 1) { logger.log("Failed to deserialize data with key = ${Person.KEY}") }
    }

    @Test
    fun set_withLoggerThrowingException_tryLogCatchException() = runBlockingTest {
        mockLogger(throwError = true)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = false)
        mockStorage(throwError = false)

        subject.set(Person.KEY, Person.INSTANCE)

        coVerify(exactly = 1) { logger.log("Kached -> set(${Person.KEY})") }
        coVerify(exactly = 1) { logger.log(FakeException) }
    }

    @Test
    fun get_withLoggerThrowingException_tryLogCatchException() = runBlockingTest {
        mockLogger(throwError = true)
        mockSerializer(throwError = false)
        mockEncryptor(throwError = false)
        mockStorage(throwError = false, hasValue = true)

        subject.get(Person.KEY)

        coVerify(exactly = 1) { logger.log("Kached -> get(${Person.KEY})") }
        coVerify(exactly = 1) { logger.log(FakeException) }
    }

    private fun mockLogger(
        throwError: Boolean = false,
    ) {
        logger = if (throwError) {
            mockk {
                coEvery { log(any<String>()) } throws FakeException
                coEvery { log(any<Throwable>()) } throws FakeException
            }
        } else {
            mockk {
                coEvery { log(any<String>()) } just Runs
                coEvery { log(any<Throwable>()) } just Runs
            }
        }
    }

    private fun mockSerializer(
        throwError: Boolean = false,
    ) {
        serializer = if (throwError) {
            mockk {
                coEvery { serialize(any()) } throws FakeException
                coEvery { deserialize<Person>(any()) } throws FakeException
            }
        } else {
            mockk {
                coEvery { serialize(any()) } returns Person.SERIAL_VALUE
                coEvery { deserialize<Person>(any()) } returns Person.INSTANCE
            }
        }
    }

    private fun mockEncryptor(
        throwError: Boolean = false,
    ) {
        encryptor = if (throwError) {
            mockk {
                coEvery { encrypt(any()) } throws FakeException
                coEvery { decrypt(any()) } throws FakeException
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
                    coEvery { put(any(), any()) } throws FakeException
                    coEvery { this@mockk.get(any()) } throws FakeException
                }
            }
            hasValue -> {
                mockk {
                    coEvery { put(any(), any()) } just Runs
                    coEvery { this@mockk.get(any()) } returns Person.ENCRYPTED_VALUE
                }
            }
            else -> {
                mockk {
                    coEvery { put(any(), any()) } just Runs
                    coEvery { this@mockk.get(any()) } returns null
                }
            }
        }
    }
}
