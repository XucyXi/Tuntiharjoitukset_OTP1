package assignments.inclass1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TemperatureConverterTest {

    private TemperatureConverter converter;

    @BeforeEach
    void setUp() {
        converter = new TemperatureConverter();
    }

    @Test
    void fahrenheitToCelsius() {
        // Test with multiple inputs
        assertEquals(0.0, converter.fahrenheitToCelsius(32.0), 0.001);
        assertEquals(100.0, converter.fahrenheitToCelsius(212.0), 0.001);
        assertEquals(-40.0, converter.fahrenheitToCelsius(-40.0), 0.001);
    }

    @Test
    void celsiusToFahrenheit() {
        // Test with multiple inputs
        assertEquals(32.0, converter.celsiusToFahrenheit(0.0), 0.001);
        assertEquals(212.0, converter.celsiusToFahrenheit(100.0), 0.001);
        assertEquals(-40.0, converter.celsiusToFahrenheit(-40.0), 0.001);
    }

    @Test
    void isExtremeTemperature() {
        // Test extreme temperatures (below -40 or above 50)
        assertTrue(converter.isExtremeTemperature(-41.0));
        assertTrue(converter.isExtremeTemperature(51.0));

        // Test edge cases (exactly -40 and exactly 50 should return false)
        assertFalse(converter.isExtremeTemperature(-40.0));
        assertFalse(converter.isExtremeTemperature(50.0));

        // Test normal temperature
        assertFalse(converter.isExtremeTemperature(20.0));
    }
}