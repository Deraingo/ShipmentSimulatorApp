import kotlin.test.Test
import kotlinx.coroutines.*
import org.junit.jupiter.api.*
import io.mockk.*
import junit.framework.TestCase.assertTrue
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull


class TrackingSimulatorTest {

    private lateinit var simulator: TrackingSimulator

    @BeforeEach
    fun setUp() {
        simulator = TrackingSimulator()
    }

    @Test
    fun `should track shipment successfully`() {
        val shipment = simulator.createNewShipment("S12345")
        simulator.addShipment(shipment)
        val trackedShipment = simulator.findShipment("S12345")
        assertNotNull(trackedShipment)
        assertEquals("S12345", trackedShipment?.id)
    }

    @Test
    fun `should stop tracking shipment`() {
        val shipment = simulator.createNewShipment("S12345")
        simulator.addShipment(shipment)
        val observer = mockk<UserObserver>(relaxed = true)
        shipment.registerObserver(observer)
        shipment.removeObserver(observer)
        shipment.addUpdate(ShippingUpdate("created", "S12345", System.currentTimeMillis(), null))
        verify(exactly = 0) { observer.update(any()) }
    }

    @Test
    fun `should process shipment updates correctly`() {
        val shipment = simulator.createNewShipment("S12345")
        simulator.addShipment(shipment)
        val update = ShippingUpdate("shipped", "S12345", System.currentTimeMillis(), System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7))
        shipment.addUpdate(update)
        assertEquals("shipped", shipment.status)
        assertNotNull(shipment.expectedDeliveryDateTimestamp)
    }

    @Test
    fun `should read shipments from file`() {
        val filename = "shipments.txt"
        val fileContent = """
            created,S12345,1622557740
            shipped,S12345,1622561340,1623166140
        """.trimIndent()
        File(filename).writeText(fileContent)
        simulator.readShipmentsFromFile(filename)
        val shipment = simulator.findShipment("S12345")
        assertNotNull(shipment)
        assertEquals(2, shipment?.updateHistory?.size)
        File(filename).delete()
    }

    @Test
    fun `should notify observers on shipment update`() {
        val shipment = simulator.createNewShipment("S12345")
        simulator.addShipment(shipment)
        val observer = mockk<UserObserver>(relaxed = true)
        shipment.registerObserver(observer)
        val update = ShippingUpdate("shipped", "S12345", System.currentTimeMillis(), System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7))
        shipment.addUpdate(update)
        verify { observer.update(any()) }
    }

    @Test
    fun `should handle non-existent shipment tracking`() {
        val shipmentId = "S99999"
        val trackedShipment = simulator.findShipment(shipmentId)
        assertNull(trackedShipment)
    }

    @Test
    fun `should simulate updates correctly`() = runBlocking {
        val shipment = simulator.createNewShipment("S12345")
        simulator.addShipment(shipment)
        launch { simulator.simulateUpdates() }
        delay(2000) // wait for some updates to be processed
        val trackedShipment = simulator.findShipment("S12345")
        assertNotNull(trackedShipment)
        assertTrue(trackedShipment.updateHistory.isNotEmpty())
    }
}

annotation class BeforeEach
