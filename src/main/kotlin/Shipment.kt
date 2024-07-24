import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class Shipment(
    status: String,
    val id: String,
    notes: MutableList<String>,
    val updateHistory: MutableList<ShippingUpdate>,
    expectedDeliveryDateTimestamp: Long,
    currentLocation: String,
    private var strategy: ShippingStatusUpdateStrategy,
    private val observers: MutableList<UserInterface>
) {
    var status by mutableStateOf(status)
    var expectedDeliveryDateTimestamp by mutableStateOf(expectedDeliveryDateTimestamp)
    var currentLocation by mutableStateOf(currentLocation)
    var notes by mutableStateOf(notes)

    fun registerObserver(observer: UserInterface) {
        observers.add(observer)
    }

    fun removeObserver(observer: UserInterface) {
        observers.remove(observer)
    }

    private fun notifyObservers() {
        observers.forEach { it.update(this) }
    }

    fun getObservers(): List<UserInterface> {
        return observers.toList()
    }

    fun addUpdate(update: ShippingUpdate) {
        updateHistory.add(update)
        setStrategyBasedOnStatus(update.newStatus)
        strategy.update(this, update)
        notifyObservers()
    }

    fun addNote(note: String) {
        notes.add(note)
    }

    fun changeStrategy(strategy: ShippingStatusUpdateStrategy) {
        this.strategy = strategy
    }

    private fun setStrategyBasedOnStatus(status: String) {
        strategy = when (status) {
            "created" -> CreatedUpdateStrategy()
            "delayed" -> DelayedUpdateStrategy()
            "delivered" -> DeliveredUpdateStrategy()
            "location" -> LocationUpdateStrategy()
            "shipped" -> ShippedUpdateStrategy()
            else -> strategy
        }
    }
}
