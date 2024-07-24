interface ShippingStatusUpdateStrategy {
    val type: String
    fun update(shipment: Shipment, update: ShippingUpdate)
}

