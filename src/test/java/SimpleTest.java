import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimpleTest {
    private Core appCore;

    @BeforeEach
    public void setUp() throws Exception {
        appCore = new Core();
    }

    @Test
    @DisplayName("Simple multiplication should work")
    public void testMultiply() {
        assertEquals(20, appCore.multiply(4,5),
                "Regular multiplication should work");
    }

    @RepeatedTest(5)
    @DisplayName("Ensure correct generating random number")
    public void testMultiplyWithZero() {
        int randNumber = appCore.getRandom(0,5);
        assertTrue(appCore.getRandom(0,5) <= 5, "Random number from 0 to 5: " + randNumber);
    }
}
