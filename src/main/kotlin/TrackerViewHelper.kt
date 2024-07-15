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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun TrackerViewHelper() {
    val trackingSimulator = remember { TrackingSimulator() }
    val shipmentId = remember { mutableStateOf(TextFieldValue("")) }
    val trackedShipments = remember { mutableStateListOf<Shipment>() }
    var warningMessage by remember { mutableStateOf<String?>(null) }
    val userObserver = UserObserver()

    fun trackShipment(id: String) {
        if (id.length == 6 && id.startsWith("S") && id.drop(1).all { it.isDigit() }) {
            val shipment = trackingSimulator.createNewShipment(id)
            shipment.registerObserver(userObserver)
            trackedShipments.add(shipment)
            warningMessage = null
        } else {
            warningMessage = "Invalid Shipment ID. It should be 6 characters long, start with 'S' and be followed by numbers."
        }
    }

    fun stopTracking(shipment: Shipment) {
        shipment.removeObserver(userObserver)
        trackedShipments.remove(shipment)
    }

    GlobalScope.launch {
        trackingSimulator.simulateUpdates()
    }

    Column {
        TextField(
            value = shipmentId.value,
            onValueChange = { shipmentId.value = it },
            label = { Text("Enter Shipment ID") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        Button(onClick = { trackShipment(shipmentId.value.text) }) {
            Text("Track")
        }
        if (warningMessage != null) {
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
                    IconButton(onClick = { stopTracking(shipment) }) {
                        Icon(Icons.Filled.Close, contentDescription = "Stop Tracking")
                    }
                }
            }
        }
    }
}