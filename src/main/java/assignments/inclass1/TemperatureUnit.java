package assignments.inclass1;

public record TemperatureUnit(int id, String name, String symbol) {
    @Override
    public String toString() {
        return name + " (" + symbol + ")";
    }
}
