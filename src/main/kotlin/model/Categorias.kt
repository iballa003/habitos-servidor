package org.iesharia.model
import org.jetbrains.exposed.sql.Table

object Categorias : Table("categorias") {
    val id = integer("id").autoIncrement()
    val nombre = varchar("nombre", 100)
    override val primaryKey = PrimaryKey(id)
}
