class DelayedUpdateStrategy : ShippingStatusUpdateStrategy {
    override fun update(shipment: Shipment, update: ShippingUpdate) {
        shipment.status = "delayed"
        shipment.expectedDeliveryDateTimestamp = (update.otherInfo as? Long) ?: 0L
    }
}