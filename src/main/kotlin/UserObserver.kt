import androidx.compose.runtime.mutableStateListOf

class UserObserver : UserInterface {
    val shipmentUpdateHistory = mutableStateListOf<String>()

    override fun update(shipment: Shipment) {
        shipmentUpdateHistory.add("Shipment ${shipment.id} went from ${shipment.status} to ${shipment.status} on ${System.currentTimeMillis()}")
    }
}