package com.ahmedsamir.pulse.core.model

sealed class PulseException(message: String) : Exception(message) {
    class NetworkException(message: String = "No internet connection") : PulseException(message)
    class AuthException(message: String = "Authentication failed") : PulseException(message)
    class NotFoundException(message: String = "Resource not found") : PulseException(message)
    class UnknownException(message: String = "Something went wrong") : PulseException(message)
}