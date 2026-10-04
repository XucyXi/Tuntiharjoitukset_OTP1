package assignments.inclass1;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TemperatureUnitDAOTest extends DBTestBase {

    private final TemperatureUnitDAO dao = new TemperatureUnitDAO();

    @Test
    void findAllReturnsThreeUnits() throws SQLException {
        List<TemperatureUnit> units = dao.findAll();
        assertEquals(3, units.size());
        assertEquals("Celsius", units.get(0).name());
    }

    @Test
    void findByNameFindsExistingUnit() throws SQLException {
        Optional<TemperatureUnit> kelvin = dao.findByName("Kelvin");
        assertTrue(kelvin.isPresent());
        assertEquals("K", kelvin.get().symbol());
    }

    @Test
    void findByNameReturnsEmptyForUnknown() throws SQLException {
        assertTrue(dao.findByName("Rankine").isEmpty());
    }

    @Test
    void toStringShowsNameAndSymbol() {
        assertEquals("Celsius (°C)", new TemperatureUnit(1, "Celsius", "°C").toString());
    }
}
