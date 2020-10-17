package io.kached

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class EmptyEncryptorTest {

    @Test
    fun encrypt_returnsOriginalData() = runBlockingTest {
        val data = "lorem ipsum"

        val result = EmptyEncryptor.encrypt(data)

        assertEquals(data, result)
    }

    @Test
    fun decrypt_returnsOriginalData() = runBlockingTest {
        val data = "lorem ipsum"

        val result = EmptyEncryptor.decrypt(data)

        assertEquals(data, result)
    }
}
