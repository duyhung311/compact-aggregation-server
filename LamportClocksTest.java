import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

public class LamportClocksTest {
    private LamportClock clock;

    // Create a new LamportClock instance before each test
    @Before
    public void setUp() {
        clock = new LamportClock();
    }


    @BeforeEach
    public void tearDown() {
        clock = null;
    }

    // Test the increment method, which should increase the clock's value by 1.
    @Test
    public void testIncrement() {
        // Initially, the clock's value should be 0
        assertEquals(0, clock.getCurrentValue());

        // After incrementing, the value should be 1
        clock.issueLamportClockValue();
        assertEquals(1, clock.getCurrentValue());

        // Increment multiple times
        clock.issueLamportClockValue();
        clock.issueLamportClockValue();
        assertEquals(3, clock.getCurrentValue());
    }

    // Test the update method, which updates the clock based on a received value.
    @Test
    public void testUpdate() {
        // Initially, the clock's value should be 0
        assertEquals(0, clock.getCurrentValue());

        // Update with a value less than the current value (should not change the value)
        clock.receiveEvent(0);
        assertEquals(1, clock.getCurrentValue());
        // doing 4 local events
        clock.issueLamportClockValue();
        clock.issueLamportClockValue();
        clock.issueLamportClockValue();
        clock.issueLamportClockValue();
        //then assert value
        assertEquals(5, clock.getCurrentValue());
        // doing another local event
        clock.receiveEvent(0);
        assertEquals(6, clock.getCurrentValue());

    }
}