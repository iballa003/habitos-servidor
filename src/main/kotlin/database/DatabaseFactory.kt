package org.iesharia.database

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.iesharia.model.Categorias
import org.iesharia.model.Habitos

object DatabaseFactory {

    fun init(environment: ApplicationEnvironment) {
        val config = environment.config.config("ktor.database")
        val url = config.property("url").getString()

        val driver = config.property("driver").getString()
        val user = config.property("user").getString()
        val password = config.property("password").getString()

        val db = Database.connect(
            url = url,
            driver = driver,
            user = user,
            password = password
        )

        transaction(db) {
            SchemaUtils.create(Categorias, Habitos)
        }
    }
}