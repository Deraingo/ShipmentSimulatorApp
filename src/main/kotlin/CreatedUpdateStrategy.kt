class CreatedUpdateStrategy : ShippingStatusUpdateStrategy {
    override fun update(shipment: Shipment, update: ShippingUpdate) {
        shipment.status = "created"
    }
}