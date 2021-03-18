import dungeoncrawler.handlers.Controls;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ControlsTest {

    private Controls controls;

    @Before
    public void setup() {
        controls = new Controls();
    }

    @Test
    public void testLoad() {
        controls.resetKeys();
        assertEquals("W", controls.getKey("up"));
    }

    @Test
    public void testSetKey() {
        controls.setKey("up", "U");
        assertEquals("U", controls.getKey("up"));
    }

    @Test
    public void testSave() {
        controls.setKey("up", "U");
        Controls controls2 = new Controls();
        assertEquals("U", controls.getKey("up"));
    }

    @Test
    public void testReset() {
        controls.setKey("up", "U");
        assertEquals("U", controls.getKey("up"));
        controls.resetKeys();
        assertEquals("W", controls.getKey("up"));
    }
}
