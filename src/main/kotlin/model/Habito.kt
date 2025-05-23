package org.iesharia.model
import kotlinx.serialization.Serializable

@Serializable
data class Habito(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val meta: Int,
    val estado: Boolean,
    val categoriaId: Int
)
