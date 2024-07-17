class ShippedUpdateStrategy : ShippingStatusUpdateStrategy {
    override fun update(shipment: Shipment, update: ShippingUpdate) {
        shipment.status = update.newStatus
        shipment.expectedDeliveryDateTimestamp = (update.otherInfo as? Long) ?: 0L
    }
}