class Shipment(
    var status: String,
    val id: String,
    val notes: MutableList<String>,
    val updateHistory: MutableList<ShippingUpdate>,
    var expectedDeliveryDateTimestamp: Long,
    var currentLocation: String,
    private var strategy: ShippingStatusUpdateStrategy,
    private val observers: MutableList<UserInterface>
) {
    fun registerObserver(observer: UserInterface) {
        observers.add(observer)
    }

    fun removeObserver(observer: UserInterface) {
        observers.remove(observer)
    }

    fun notifyObservers() {
        observers.forEach { it.update(this) }
    }

    fun addUpdate(update: ShippingUpdate) {
        updateHistory.add(update)
        strategy.update(this, update)
        notifyObservers()
    }

    fun addNote(note: String) {
        notes.add(note)
    }

    fun changeStrategy(strategy: ShippingStatusUpdateStrategy) {
        this.strategy = strategy
    }
}