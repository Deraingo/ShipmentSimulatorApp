class ShippedUpdateStrategy : ShippingStatusUpdateStrategy {
    override fun update(shipment: Shipment, update: ShippingUpdate) {
        shipment.status = "shipped"
        shipment.expectedDeliveryDateTimestamp = update.otherInfo as Long
    }
}