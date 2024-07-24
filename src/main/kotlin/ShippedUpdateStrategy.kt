class ShippedUpdateStrategy : ShippingStatusUpdateStrategy {
    override val type: String = "shipped"
    override fun update(shipment: Shipment, update: ShippingUpdate) {
        shipment.status = update.newStatus
        shipment.expectedDeliveryDateTimestamp = update.otherInfo as? Long ?: 0L
    }
}