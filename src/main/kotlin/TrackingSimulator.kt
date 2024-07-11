class TrackingSimulator {
    private val shipments: MutableList<Shipment> = mutableListOf()

    fun findShipment(id: String): Shipment? {
        return shipments.find { it.id == id }
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