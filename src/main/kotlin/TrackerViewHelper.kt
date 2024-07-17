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
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TrackerViewHelper() {
    val trackingSimulator = remember { TrackingSimulator() }
    val shipmentId = remember { mutableStateOf(TextFieldValue("")) }
    val trackedShipments = remember { mutableStateListOf<Shipment>() }
    var warningMessage by remember { mutableStateOf<String?>(null) }
    val userObserver = UserObserver()

    fun trackShipment(id: String) {
        if (id.length == 6 && id.startsWith("S") && id.drop(1).all { it.isDigit() }) {
            val existingShipment = trackedShipments.find { it.id == id }
            if (existingShipment != null) {
                existingShipment.registerObserver(userObserver)
                val update = trackingSimulator.getUpdateForShipment(id)
                if (update != null) {
                    existingShipment.addUpdate(update)
                }
            } else {
                val shipment = trackingSimulator.createNewShipment(id)
                shipment.registerObserver(userObserver)
                trackedShipments.add(shipment)
            }
            warningMessage = null
        } else {
            warningMessage = "Invalid Shipment ID. It should be 6 characters long, start with 'S' and be followed by numbers."
        }
    }

    fun stopTracking(shipment: Shipment) {
        shipment.removeObserver(userObserver)
        trackedShipments.remove(shipment)
    }

    fun convertTimestampToDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    LaunchedEffect(key1 = Unit) {
        while (true) {
            delay(1000)
            trackingSimulator.simulateUpdates()
            trackedShipments.forEach { shipment ->
                val update = trackingSimulator.getUpdateForShipment(shipment.id)
                if (update != null && !(shipment.currentLocation == "Logan, UT" && shipment.status == "delivered")) {
                    shipment.addUpdate(update)
                }
            }
        }
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
        val uiTrackedShipments by derivedStateOf { trackedShipments.toList() }
        uiTrackedShipments.forEach { shipment ->
            Card(
                modifier = Modifier.padding(top = 8.dp),
                elevation = 4.dp
            ) {
                Column {
                    userObserver.shipmentUpdateHistory.forEach { update ->
                        Text(text = update)
                    }
                    Text(text = "Shipment ID: ${shipment.id}")
                    Text(text = "Status: ${shipment.status}")
                    Text(text = "Location: ${shipment.currentLocation}")
                    Text(text = "Expected Delivery: ${convertTimestampToDate(shipment.expectedDeliveryDateTimestamp)}")
                    Text(text = "Notes: ${shipment.notes.joinToString()}")
                    IconButton(onClick = { stopTracking(shipment) }) {
                        Icon(Icons.Filled.Close, contentDescription = "Stop Tracking")
                    }
                }
            }
        }
    }
}