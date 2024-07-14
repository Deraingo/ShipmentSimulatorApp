import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class TrackingSimulator {
    private val shipments: MutableList<Shipment> = mutableListOf()

    fun findShipment(id: String): Shipment? {
        return shipments.find { it.id == id }
    }

    suspend fun simulateUpdates() {
        val random = Random.Default
        val cities = listOf("Los Angeles CA", "New York NY", "Chicago IL")
        val notes = listOf(
            "Damaged slightly during shipping",
            "Shipping label reprinted due to damage",
            "Inspection completed on exported goods"
        )

        while (true) {
            shipments.forEach { shipment ->
                val status = when (random.nextInt(8)) {
                    0 -> "created"
                    1 -> "shipped"
                    2 -> "location"
                    3 -> "delivered"
                    4 -> "delayed"
                    5 -> "lost"
                    6 -> "canceled"
                    else -> "note added"
                }
                val timestamp = System.currentTimeMillis()
                val otherInfo = when (status) {
                    "shipped", "delayed" -> timestamp + TimeUnit.DAYS.toMillis(7)
                    "location" -> cities.random()
                    "note added" -> notes.random()
                    else -> null
                }
                val update = ShippingUpdate(status, shipment.id, timestamp, otherInfo)
                shipment.addUpdate(update)

                if (status == "delivered") {
                    shipment.currentLocation = "Logan, Utah"
                }
            }
            delay(1000)
        }
    }

    fun addShipment(shipment: Shipment) {
        shipments.add(shipment)
    }

    fun runSimulation() {
    }

    fun createShipment(id: String) {
        val shipment = Shipment(
            status = "created",
            id = id,
            notes = mutableListOf(),
            updateHistory = mutableListOf(),
            expectedDeliveryDateTimestamp = 0L,
            currentLocation = "",
            strategy = CreatedUpdateStrategy(),
            observers = mutableListOf()
        )
        shipments.add(shipment)
    }
}