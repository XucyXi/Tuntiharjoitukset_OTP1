package assignments.inclass1;

import java.time.LocalDateTime;

public record TempRecord(int id, TemperatureUnit fromUnit, TemperatureUnit toUnit,
                         double inputValue, double resultValue, LocalDateTime createdAt) {
}
