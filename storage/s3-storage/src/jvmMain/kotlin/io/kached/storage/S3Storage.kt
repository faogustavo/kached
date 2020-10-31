package io.kached.storage

import io.kached.StringStorage
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.core.sync.ResponseTransformer
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest

class S3Storage constructor(
    private val s3Client: S3Client,
    private val bucket: String,
    path: String = ""
) : StringStorage {
    private val directory = if (path.endsWith("/")) path.dropLast(1) else path

    override suspend fun set(key: String, data: String) {
        val putRequest = PutObjectRequest.builder()
            .bucket(bucket)
            .key("$directory/$key")
            .build()
        s3Client.putObject(putRequest, RequestBody.fromString(data))
    }

    override suspend fun get(key: String): String? {
        val getRequest = GetObjectRequest.builder()
            .bucket(bucket)
            .key("$directory/$key")
            .build()
        return s3Client.getObject(getRequest, ResponseTransformer.toBytes()).asUtf8String()
    }

    override suspend fun unset(key: String) {
        val deleteRequest = DeleteObjectRequest.builder()
            .bucket(bucket)
            .key("$directory/$key")
            .build()
        s3Client.deleteObject(deleteRequest)
    }

    /**
     *  This method is not being implemented because we cant differentiate between cached items and items that were already
     *  on the bucket, so we wont implement it to avoid accidental wipes
     */
    override suspend fun clear() {
        throw NotImplementedError("This method was not implemented due to safety reasons, if you want to use it please extend this class and override it")
    }
}
