package org.iesharia.model
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object Usuarios : Table("usuarios") {
    val id = integer("id").autoIncrement()
    val nombre = varchar("nombre", 100)
    val email = varchar("email", 100).uniqueIndex()
    val password = varchar("password", 100)
    val creadoEn = datetime("creado_en")
    override val primaryKey = PrimaryKey(id)
}