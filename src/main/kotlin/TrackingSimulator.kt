import kotlinx.coroutines.delay
import java.io.File
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

    fun addShipment(shipment: Shipment) {
        shipments.add(shipment)
    }

    fun createNewShipment(id: String): Shipment {
        val initialLocation = "Los Angeles CA"  // Start with a predefined location
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
}

