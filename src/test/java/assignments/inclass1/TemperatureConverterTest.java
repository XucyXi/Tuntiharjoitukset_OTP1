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
        assertEquals(0.0, converter.fahrenheitToCelsius(32.0), 0.001);
        assertEquals(100.0, converter.fahrenheitToCelsius(212.0), 0.001);
        assertEquals(-40.0, converter.fahrenheitToCelsius(-40.0), 0.001);
    }

    @Test
    void celsiusToFahrenheit() {
        assertEquals(32.0, converter.celsiusToFahrenheit(0.0), 0.001);
        assertEquals(212.0, converter.celsiusToFahrenheit(100.0), 0.001);
        assertEquals(-40.0, converter.celsiusToFahrenheit(-40.0), 0.001);
    }

    @Test
    void isExtremeTemperature() {
        assertTrue(converter.isExtremeTemperature(-41.0));
        assertTrue(converter.isExtremeTemperature(51.0));
        assertFalse(converter.isExtremeTemperature(-40.0));
        assertFalse(converter.isExtremeTemperature(50.0));
        assertFalse(converter.isExtremeTemperature(20.0));
    }

    @Test
    void testKelvinToCelsius() {
        assertEquals(26.85, converter.kelvinToCelsius(300.0), 0.001);
        assertEquals(-273.15, converter.kelvinToCelsius(0.0), 0.001);
        assertEquals(0.0, converter.kelvinToCelsius(273.15), 0.001);
    }

    @Test
    void celsiusToKelvin() {
        assertEquals(273.15, converter.celsiusToKelvin(0.0), 0.001);
        assertEquals(0.0, converter.celsiusToKelvin(-273.15), 0.001);
    }

    @Test
    void convertBetweenUnits() {
        assertEquals(212.0, converter.convert("Celsius", "Fahrenheit", 100.0), 0.001);
        assertEquals(373.15, converter.convert("Fahrenheit", "Kelvin", 212.0), 0.001);
        assertEquals(26.85, converter.convert("Kelvin", "Celsius", 300.0), 0.001);
        assertEquals(5.0, converter.convert("Celsius", "Celsius", 5.0), 0.001);
    }

    @Test
    void convertUnknownUnitThrows() {
        assertThrows(IllegalArgumentException.class, () -> converter.convert("Foo", "Celsius", 1));
        assertThrows(IllegalArgumentException.class, () -> converter.convert("Celsius", "Bar", 1));
    }
}
