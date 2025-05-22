package org.iesharia.model
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val success: Boolean,
    val userId: Int? = null,
    val message: String
)