import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class TrackingSimulator {
    private val shipments: MutableList<Shipment> = mutableListOf()

    fun findShipment(id: String): Shipment? {
        return shipments.find { it.id == id }
    }

    fun getUpdateForShipment(id: String): ShippingUpdate? {
        return shipments.find { it.id == id }?.updateHistory?.lastOrNull()
    }

    fun addShipment(shipment: Shipment) {
        shipments.add(shipment)
    }


    suspend fun simulateUpdates() {
        val random = Random.Default
        val cities = listOf("Los Angeles, CA", "New York, NY", "Chicago, IL", "Salt Lake City, UT", "Denver, CO", "Logan, UT")
        val notes = listOf(
            "Damaged slightly during shipping",
            "Shipping label reprinted due to damage",
            "Inspection completed on exported goods"
        )

        while (true) {
            shipments.forEach { shipment ->
                if (shipment.currentLocation != "Logan, UT" && shipment.status != "delivered") {

                    val status = when (random.nextInt(6)) {
                        0 -> "shipped"
                        1 -> "location"
                        2 -> "delivered"
                        3 -> "delayed"
                        4 -> "lost"
                        else -> "noteadded"
                    }

                    val timestamp = System.currentTimeMillis()
                    val otherInfo = when (status) {
                        "shipped", "delayed" -> timestamp + TimeUnit.DAYS.toMillis(7)
                        "location" -> cities.random()
                        "noteadded" -> notes.random()
                        else -> null
                    }
                    val previousStatus = shipment.status
                    val update = ShippingUpdate(previousStatus, status, timestamp, otherInfo)

                    shipment.addUpdate(update)

                    when (status) {
                        "location" -> {
                            shipment.currentLocation = otherInfo as String
                        }
                        "noteadded" -> {
                            shipment.addNote(otherInfo as String)
                        }
                        "shipped", "delayed" -> {
                            shipment.expectedDeliveryDateTimestamp = getRandomFutureTimestamp()
                        }
                    }

                    if (shipment.currentLocation == "Logan, UT") {
                        shipment.changeStrategy(DeliveredUpdateStrategy())
                        shipment.status = "delivered"
                    }
                }
            }
            delay(1000)
        }
    }

    private fun getRandomFutureTimestamp(): Long {
        val now = System.currentTimeMillis()
        val oneWeekFromNow = now + TimeUnit.DAYS.toMillis(7)
        return (now..oneWeekFromNow).random()
    }


    fun createNewShipment(id: String): Shipment {
        val initialLocation = "Los Angeles CA"
        val shipment = Shipment(
            status = "created",
            id = id,
            notes = mutableListOf(),
            updateHistory = mutableListOf(),
            expectedDeliveryDateTimestamp = 0L,
            currentLocation = initialLocation,
            strategy = CreatedUpdateStrategy(),
            observers = mutableListOf()
        )
        shipments.add(shipment)
        return shipment
    }

    private fun addUpdate(id: String, update: ShippingUpdate) {
        val shipment = shipments.find { it.id == id }
        shipment?.addUpdate(update)
    }

    suspend fun fetchUpdates() {
        val client = HttpClient(CIO)
        try {
            val response: HttpResponse = client.get("http://localhost:3000/api/shipment/updates")
            val jsonString = response.bodyAsText()
            println("Response body: $jsonString")  // Log the raw response
            val updates = parseUpdates(jsonString)
            updates.forEach { update ->
                addUpdate(update.id, update)
            }
        } catch (e: Exception) {
            println("Error fetching updates: ${e.message}")
        } finally {
            client.close()
        }
    }

    private fun parseUpdates(jsonString: String): List<ShippingUpdate> {
        val mapper = jacksonObjectMapper()
        return mapper.readValue(jsonString)
    }
}
