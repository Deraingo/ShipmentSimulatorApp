class DeliveredUpdateStrategy : ShippingStatusUpdateStrategy {
    override fun update(shipment: Shipment, update: ShippingUpdate) {
        shipment.status = "delivered"
        shipment.currentLocation = "Delivered"
    }
}