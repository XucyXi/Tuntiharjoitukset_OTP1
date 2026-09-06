package assignments.inclass1;

public class TemperatureConverter {

    public double fahrenheitToCelsius(double fahrenheit) {
        // Returns (fahrenheit - 32) * 5/9
        return (fahrenheit - 32.0) * 5.0 / 9.0;
    }

    public double celsiusToFahrenheit(double celsius) {
        // Returns (celsius * 9/5) + 32
        return (celsius * 9.0 / 5.0) + 32.0;
    }

    public boolean isExtremeTemperature(double celsius) {
        // Returns true if below -40°C or above 50°C
        return celsius < -40.0 || celsius > 50.0;
    }
}