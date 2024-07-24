class DeliveredUpdateStrategy : ShippingStatusUpdateStrategy {
    override val type: String = "delivered"
    override fun update(shipment: Shipment, update: ShippingUpdate) {
        shipment.status = update.newStatus
        shipment.currentLocation = "Logan, UT"
    }
}