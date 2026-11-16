package assignments.inclass1;

public record TemperatureUnit(int id, String symbol, String localizedName) {
    @Override
    public String toString() {
        return localizedName + " (" + symbol + ")";
    }
}