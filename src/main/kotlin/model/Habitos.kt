package org.iesharia.model

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object Habitos : Table("habitos") {
    val id = integer("id").autoIncrement()
    val nombre = varchar("nombre", 100)
    val descripcion = text("descripcion")
    val metaDiaria = integer("meta_diaria")
    val estado = bool("estado")
    val creadoEn = datetime("creado_en")
    val usuarioId = integer("usuario_id")
    val categoriaId = integer("categoria_id")

    override val primaryKey = PrimaryKey(id)
}
