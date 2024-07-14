data class ShippingUpdate(
    val previousStatus: String,
    val newStatus: String,
    val timestamp: Long,
    val otherInfo: Any? = null
)