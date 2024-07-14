import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun TrackerViewHelper() {
    val trackingSimulator = remember { mutableStateOf(TrackingSimulator()) }
    val shipmentId = remember { mutableStateOf(TextFieldValue("")) }
    val trackedShipments = remember { mutableStateListOf<Shipment>() }
    var shipmentTotes by remember { mutableStateOf(listOf<String>()) }
    var shipmentUpdateHistory by remember { mutableStateOf(listOf<String>()) }
    var expectedShipmentDeliveryDate by remember { mutableStateOf("") }
    var shipmentStatus by remember { mutableStateOf("") }
    val userObserver = UserObserver()
    var warningMessage by remember { mutableStateOf<String?>(null) }


    fun trackShipment(id: String) {
        val shipment = trackingSimulator.value.findShipment(id)
        shipment?.registerObserver(userObserver)
    }

    fun stopTracking() {
    }


    Column {
        TextField(
            value = shipmentId.value,
            onValueChange = {
                if (it.text.length <= 6) {
                    shipmentId.value = it
                }
            },
            label = { Text("Enter Shipment ID") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        Button(onClick = {
            if (shipmentId.value.text.length == 6 && shipmentId.value.text.startsWith("S")) {
                val shipment = trackingSimulator.value.findShipment(shipmentId.value.text)
                if (shipment != null) {
                    trackedShipments.add(shipment)
                    warningMessage = null
                } else {
                    warningMessage = "Shipment doesn't exist"
                }
            } else {
                warningMessage = "Invalid Shipment ID. It should be 6 characters long and start with 'S'."
            }
        }) {
            Text("Track")
        }
        if(warningMessage != null){
            Card(
                modifier = Modifier.padding(top = 8.dp),
                backgroundColor = Color.Red,
                contentColor = Color.White
            ) {
                Text(text = warningMessage!!)
            }
        }
        trackedShipments.forEach { shipment ->
            Card(
                modifier = Modifier.padding(top = 8.dp),
                elevation = 4.dp
            ) {
                Column {
                    Text(text = "Shipment ID: ${shipment.id}")
                    Text(text = "Status: ${shipment.status}")
                    Text(text = "Location: ${shipment.currentLocation}")
                    Text(text = "Expected Delivery: ${shipment.expectedDeliveryDateTimestamp}")
                    Text(text = "Notes: ${shipment.notes.joinToString()}")
                    IconButton(onClick = { trackedShipments.remove(shipment) }) {
                        Icon(Icons.Filled.Close, contentDescription = "Stop Tracking")
                    }
                }
            }
        }
    }
}