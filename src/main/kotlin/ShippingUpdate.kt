
data class ShippingUpdate(
    val previousStatus: String,
    val newStatus: String,
    val timestamp: Long,
    val otherInfo: Any? = null,
    val type: Shipment? = null,
    val id: String = "",
    val shipmentType: Comparable<*>? = null,
    val expectedDeliveryDays: Int? = null,
    val notes: String? = null,
)
