package assignments.inclass1;

public class TemperatureConverter {

    public double convert(String fromUnit, String toUnit, double value) {
        double celsiusValue = switch (fromUnit) {
            case "°C" -> value;
            case "°F" -> (value - 32) * 5.0 / 9.0;
            case "K" -> value - 273.15;
            default -> throw new IllegalArgumentException("Unknown unit: " + fromUnit);
        };

        return switch (toUnit) {
            case "°C" -> celsiusValue;
            case "°F" -> (celsiusValue * 9.0 / 5.0) + 32;
            case "K" -> celsiusValue + 273.15;
            default -> throw new IllegalArgumentException("Unknown unit: " + toUnit);
        };
    }

    public boolean isExtremeTemperature(double celsiusValue) {
        return celsiusValue < -50 || celsiusValue > 50;
    }
}