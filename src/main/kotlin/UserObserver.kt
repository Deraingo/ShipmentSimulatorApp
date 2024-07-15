class UserObserver : UserInterface {
    private val shipmentUpdateHistory = mutableListOf<String>()

    override fun update(shipment: Shipment) {
        shipmentUpdateHistory.add("Shipment ${shipment.id} went from ${shipment.status} to ${shipment.status} on ${System.currentTimeMillis()}")
    }
}