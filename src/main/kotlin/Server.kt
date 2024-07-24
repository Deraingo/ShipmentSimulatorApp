import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.jackson.*
import io.ktor.http.HttpStatusCode

data class ShipmentUpdate(
    val type: String,
    val id: String,
    val timestamp: Long? = null,
    val shipmentType: String? = null,
    val expectedDeliveryDays: Int? = null,
    val notes: String? = null
)

fun main() {
    embeddedServer(Netty, port = 3000) {
        install(ContentNegotiation) {
            jackson { }
        }
        routing {
            route("/api/shipment/updates") {
                post {
                    val update = call.receive<ShipmentUpdate>()
                    println("Received shipment update: $update")

                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }.start(wait = true)
}
