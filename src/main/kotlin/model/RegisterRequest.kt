package org.iesharia.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val nombre: String,
    val email: String,
    val password: String
)