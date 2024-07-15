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
        val cities = listOf("Los Angeles CA", "New York NY", "Chicago IL", "Salt Lake City UT", "Denver CO", "Logan UT")
        val notes = listOf(
            "Damaged slightly during shipping",
            "Shipping label reprinted due to damage",
            "Inspection completed on exported goods"
        )

        while (true) {
            shipments.forEach { shipment ->
                if (shipment.currentLocation != "Logan UT") {
                    val status = when (random.nextInt(7)) {
                        0 -> "shipped"
                        1 -> "location"
                        2 -> "delivered"
                        3 -> "delayed"
                        4 -> "lost"
                        5 -> "canceled"
                        else -> "noteadded"
                    }
                    val timestamp = System.currentTimeMillis()
                    val otherInfo = when (status) {
                        "shipped", "delayed" -> timestamp + TimeUnit.DAYS.toMillis(7)
                        "location" -> cities.random()
                        "noteadded" -> notes.random()
                        else -> null
                    }
                    val update = ShippingUpdate(status, shipment.id, timestamp, otherInfo)
                    shipment.addUpdate(update)

                    if (shipment.currentLocation == "Logan UT") {
                        shipment.changeStrategy(DeliveredUpdateStrategy())
                    }
                }
            }
            delay(1000)
        }
    }

    fun addShipment(shipment: Shipment) {
        shipments.add(shipment)
    }

    fun createNewShipment(id: String): Shipment {
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
        return shipment
    }

    fun readShipmentsFromFile(filename: String) {
        val lines = File(filename).readLines()
        lines.forEach { line ->
            val parts = line.split(",")
            val updateType = parts[0]
            val shipmentId = parts[1]
            val timestampOfUpdate = parts[2].toLong()
            val otherInfo = if (parts.size > 3) parts[3] else null
            val update = ShippingUpdate(updateType, shipmentId, timestampOfUpdate, otherInfo)
            val shipment = findShipment(shipmentId) ?: createNewShipment(shipmentId)
            shipment.addUpdate(update)
        }
    }
}

