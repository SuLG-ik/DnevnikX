package ru.sulgik.auth.core

class AuthorizationProducerDescriptor(
    val order: Int = 0,
)

interface AuthorizationProducer {

    val descriptor: AuthorizationProducerDescriptor

    suspend fun produceAuthorization(data: AuthScope)

}