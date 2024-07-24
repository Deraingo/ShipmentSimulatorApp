class CreatedUpdateStrategy : ShippingStatusUpdateStrategy {
    override val type: String = "created"
    override fun update(shipment: Shipment, update: ShippingUpdate) {
        shipment.status = update.newStatus
        shipment.expectedDeliveryDateTimestamp = update.timestamp ?: 0L
    }
}