interface ShippingStatusUpdateStrategy {
    fun update(shipment: Shipment, update: ShippingUpdate)
}

