class LocationUpdateStrategy : ShippingStatusUpdateStrategy {
    override fun update(shipment: Shipment, update: ShippingUpdate) {
        shipment.status = update.newStatus
        shipment.currentLocation = (update.otherInfo as? String) ?: ""
    }
}