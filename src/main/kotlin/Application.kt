package org.iesharia

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.response.respond
import org.jetbrains.exposed.sql.transactions.transaction
import org.iesharia.model.Categorias
import org.iesharia.database.DatabaseFactory
import org.jetbrains.exposed.sql.selectAll
import com.typesafe.config.ConfigFactory
import io.ktor.server.config.HoconApplicationConfig

fun main() {
    embeddedServer(Netty, environment = applicationEngineEnvironment {
        config = HoconApplicationConfig(ConfigFactory.load("application.conf"))
        module(Application::module) // 👈 ESTE YA ejecuta todo lo de Application.module()
        connector {
            port = 8080
            host = "0.0.0.0"
        }
    }).start(wait = true)
}



fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    println("Test value: " + environment.config.property("ktor.testvalue").getString())
    DatabaseFactory.init(environment)

    routing {
        get("/") {
            call.respondText("Servidor Ktor funcionando 🚀")
        }
        get("/categorias") {
            val categorias = transaction {
                Categorias.selectAll().map {
                    Categoria(
                        id = it[Categorias.id],
                        nombre = it[Categorias.nombre]
                    )
                }
            }
            call.respond(categorias)
        }


    }
}
