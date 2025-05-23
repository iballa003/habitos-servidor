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
import org.iesharia.database.DatabaseFactory
import org.jetbrains.exposed.sql.selectAll
import com.typesafe.config.ConfigFactory
import io.ktor.http.*
import io.ktor.server.config.HoconApplicationConfig
import io.ktor.server.request.*
import org.iesharia.model.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import at.favre.lib.crypto.bcrypt.BCrypt
import org.jetbrains.exposed.sql.insert
import java.time.LocalDateTime

fun main() {
    embeddedServer(Netty, environment = applicationEngineEnvironment {
        config = HoconApplicationConfig(ConfigFactory.load("application.conf"))
        module(Application::module)
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
            call.respondText("Ktor funcionando")
        }
        post("/login") {
            val login = call.receive<LoginRequest>()
            val usuario = transaction {
                Usuarios.select {
                    (Usuarios.email eq login.email)
                }.singleOrNull()
            }
            if (usuario != null){
                val hashFromDb = usuario[Usuarios.password]

                val result = BCrypt.verifyer().verify(login.password.toCharArray(), hashFromDb)
                if(result.verified){
                    call.respond(LoginResponse(true, usuario[Usuarios.id], "Login Correcto"))
                }else{
                    call.respond(HttpStatusCode.Unauthorized, LoginResponse(false,null,"Contraseña incorrecta"))
                }

            }else{
                call.respond(HttpStatusCode.Unauthorized, LoginResponse(false,null,"Usuario incorrecto"))
            }
        }
        post("/register") {
            val nuevoUsuario = call.receive<RegisterRequest>()

            val passwordHash = BCrypt.withDefaults()
                .hashToString(12, nuevoUsuario.password.toCharArray())

            val existe = transaction {
                Usuarios.select { Usuarios.email eq nuevoUsuario.email }.any()
            }

            if (existe) {
                println("emaiill")
                call.respondText("El email ya está registrado", status = HttpStatusCode.Conflict)
            } else {
                transaction {
                    Usuarios.insert {
                        it[nombre] = nuevoUsuario.nombre
                        it[email] = nuevoUsuario.email
                        it[password] = passwordHash
                        it[creadoEn] = LocalDateTime.now()
                    }
                }
                println("Registro completado. Enviando respuesta...")
                call.respondText("Usuario registrado correctamente", status = HttpStatusCode.Created)
            }
        }
        get("/habitos") {
            val userId = call.request.queryParameters["usuarioId"]?.toIntOrNull()
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, "Falta usuarioId")
                return@get
            }

            val habitos = transaction {
                Habitos.select { Habitos.usuarioId eq userId }.map {
                    Habito(
                        id = it[Habitos.id],
                        nombre = it[Habitos.nombre],
                        descripcion = it[Habitos.descripcion],
                        meta = it[Habitos.metaDiaria],
                        estado = it[Habitos.estado],
                        categoriaId = it[Habitos.categoriaId]
                    )
                }
            }

            call.respond(habitos)
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
