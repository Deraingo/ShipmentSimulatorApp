import androidx.compose.runtime.*

@Composable
fun TrackerViewHelper() {
    var shipmentId by remember { mutableStateOf("") }
    var shipmentTotes by remember { mutableStateOf(listOf<String>()) }
    var shipmentUpdateHistory by remember { mutableStateOf(listOf<String>()) }
    var expectedShipmentDeliveryDate by remember { mutableStateOf("") }
    var shipmentStatus by remember { mutableStateOf("") }

    fun trackShipment(id: String) {
    }

    fun stopTracking() {
    }
}