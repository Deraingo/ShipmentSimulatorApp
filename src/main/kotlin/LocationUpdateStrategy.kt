class LocationUpdateStrategy : ShippingStatusUpdateStrategy {
    override val type: String = "location"
    override fun update(shipment: Shipment, update: ShippingUpdate) {
        shipment.status = update.newStatus
        shipment.currentLocation = update.otherInfo as? String ?: ""
    }
}