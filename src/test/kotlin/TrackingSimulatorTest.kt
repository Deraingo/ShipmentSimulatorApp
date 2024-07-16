import kotlin.test.Test
import kotlinx.coroutines.*
import org.junit.jupiter.api.*
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlin.test.assertEquals


class TrackingSimulatorTest {

    @Test
    fun `test shipment creation`() {
        val shipment = TrackingSimulator().createNewShipment("S12345")
        assertEquals("S12345", shipment.id)
        assertEquals("created", shipment.status)
    }

    @Test
    fun `test shipment tracking`() {
        val simulator = TrackingSimulator()
        val shipment = simulator.createNewShipment("S12345")
        simulator.addShipment(shipment)
        assertEquals(shipment, simulator.findShipment("S12345"))
    }

    @Test
    fun `test shipment update`() {
        val shipment = TrackingSimulator().createNewShipment("S12345")
        shipment.addUpdate(ShippingUpdate("created", "shipped", System.currentTimeMillis()))
        assertEquals("shipped", shipment.status)
    }

    @Test
    fun `test shipment note addition`() {
        val shipment = TrackingSimulator().createNewShipment("S12345")
        shipment.addNote("Test note")
        assertTrue(shipment.notes.contains("Test note"))
    }

    @Test
    fun `test shipment observer registration`() {
        val shipment = TrackingSimulator().createNewShipment("S12345")
        val observer = UserObserver()
        shipment.registerObserver(observer)
        // Assuming you have a method to get observers
        assertTrue(shipment.getObservers().contains(observer))
    }

    @Test
    fun `test shipment observer removal`() {
        val shipment = TrackingSimulator().createNewShipment("S12345")
        val observer = UserObserver()
        shipment.registerObserver(observer)
        shipment.removeObserver(observer)
        // Assuming you have a method to get observers
        assertFalse(shipment.getObservers().contains(observer))
    }

    @Test
    fun `test shipment location update`() {
        val shipment = TrackingSimulator().createNewShipment("S12345")
        shipment.addUpdate(ShippingUpdate("created", "location", System.currentTimeMillis(), "New York"))
        assertEquals("New York", shipment.currentLocation)
    }

    @Test
    fun `test shipment delivery date update`() {
        val shipment = TrackingSimulator().createNewShipment("S12345")
        val timestamp = System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7
        shipment.addUpdate(ShippingUpdate("created", "shipped", System.currentTimeMillis(), timestamp))
        assertEquals(timestamp, shipment.expectedDeliveryDateTimestamp)
    }

    @Test
    fun `test shipment status after delivery`() {
        val shipment = TrackingSimulator().createNewShipment("S12345")
        shipment.addUpdate(ShippingUpdate("created", "delivered", System.currentTimeMillis()))
        assertEquals("delivered", shipment.status)
    }

    @Test
    fun `test shipment status after being lost`() {
        val shipment = TrackingSimulator().createNewShipment("S12345")
        shipment.addUpdate(ShippingUpdate("created", "lost", System.currentTimeMillis()))
        assertEquals("lost", shipment.status)
    }

    @Test
    fun `test shipment status after being canceled`() {
        val shipment = TrackingSimulator().createNewShipment("S12345")
        shipment.addUpdate(ShippingUpdate("created", "canceled", System.currentTimeMillis()))
        assertEquals("canceled", shipment.status)
    }

    @Test
    fun `test shipment status after being delayed`() {
        val shipment = TrackingSimulator().createNewShipment("S12345")
        shipment.addUpdate(ShippingUpdate("created", "delayed", System.currentTimeMillis()))
        assertEquals("delayed", shipment.status)
    }

    @Test
    fun `test shipment status after being shipped`() {
        val shipment = TrackingSimulator().createNewShipment("S12345")
        shipment.addUpdate(ShippingUpdate("created", "shipped", System.currentTimeMillis()))
        assertEquals("shipped", shipment.status)
    }

    @Test
    fun `test shipment status after note added`() {
        val shipment = TrackingSimulator().createNewShipment("S12345")
        shipment.addUpdate(ShippingUpdate("created", "noteadded", System.currentTimeMillis(), "Test note"))
        assertTrue(shipment.notes.contains("Test note"))
    }

    @Test
    fun `test shipment status after creation`() {
        val shipment = TrackingSimulator().createNewShipment("S12345")
        shipment.addUpdate(ShippingUpdate("created", "created", System.currentTimeMillis()))
        assertEquals("created", shipment.status)
    }
}
