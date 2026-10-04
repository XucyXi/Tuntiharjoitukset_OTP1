package assignments.inclass1;

public class TemperatureConverter {

    public double fahrenheitToCelsius(double fahrenheit) {
        return (fahrenheit - 32.0) * 5.0 / 9.0;
    }

    public double celsiusToFahrenheit(double celsius) {
        return (celsius * 9.0 / 5.0) + 32.0;
    }

    public boolean isExtremeTemperature(double celsius) {
        return celsius < -40.0 || celsius > 50.0;
    }

    public double kelvinToCelsius(double kelvin) {
        return kelvin - 273.15;
    }

    public double celsiusToKelvin(double celsius) {
        return celsius + 273.15;
    }

    /** Muuntaa yksiköstä toiseen (Celsius, Fahrenheit, Kelvin) Celsiuksen kautta. */
    public double convert(String from, String to, double value) {
        double celsius = switch (from) {
            case "Celsius" -> value;
            case "Fahrenheit" -> fahrenheitToCelsius(value);
            case "Kelvin" -> kelvinToCelsius(value);
            default -> throw new IllegalArgumentException("Unknown unit: " + from);
        };
        return switch (to) {
            case "Celsius" -> celsius;
            case "Fahrenheit" -> celsiusToFahrenheit(celsius);
            case "Kelvin" -> celsiusToKelvin(celsius);
            default -> throw new IllegalArgumentException("Unknown unit: " + to);
        };
    }
}
