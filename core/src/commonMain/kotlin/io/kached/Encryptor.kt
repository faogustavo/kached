package io.kached

interface Encryptor {
    suspend fun encrypt(data: String): String
    suspend fun decrypt(data: String): String
}

internal object EmptyEncryptor : Encryptor {
    override suspend fun encrypt(data: String): String = data
    override suspend fun decrypt(data: String): String = data
}
