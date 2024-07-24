class DelayedUpdateStrategy : ShippingStatusUpdateStrategy {
    override val type: String = "delayed"
    override fun update(shipment: Shipment, update: ShippingUpdate) {
        shipment.status = update.newStatus
        shipment.expectedDeliveryDateTimestamp = update.otherInfo as? Long ?: 0L
    }
}