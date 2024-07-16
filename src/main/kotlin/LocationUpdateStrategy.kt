class LocationUpdateStrategy : ShippingStatusUpdateStrategy {
    override fun update(shipment: Shipment, update: ShippingUpdate) {
        shipment.status = "location"
        shipment.currentLocation = (update.otherInfo as? String) ?: ""
    }
}