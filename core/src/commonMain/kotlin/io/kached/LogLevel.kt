package io.kached

enum class AcceptedLogLevel {
    All, Warning, Error, None;
}

enum class LogLevel {
    Info, Warning, Error;

    fun isAtLeast(other: AcceptedLogLevel) = this.ordinal >= other.ordinal
}
