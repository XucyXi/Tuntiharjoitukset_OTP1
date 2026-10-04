package assignments.inclass1;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TempRecordDAOTest extends DBTestBase {

    private final TemperatureUnitDAO unitDAO = new TemperatureUnitDAO();
    private final TempRecordDAO recordDAO = new TempRecordDAO();

    @Test
    void insertAndFindAllJoinsUnits() throws SQLException {
        TemperatureUnit c = unitDAO.findByName("Celsius").orElseThrow();
        TemperatureUnit f = unitDAO.findByName("Fahrenheit").orElseThrow();

        int id = recordDAO.insert(c, f, 100.0, 212.0);
        assertTrue(id > 0);

        List<TempRecord> records = recordDAO.findAll();
        assertEquals(1, records.size());
        TempRecord r = records.get(0);
        assertEquals(id, r.id());
        assertEquals("Celsius", r.fromUnit().name());
        assertEquals("Fahrenheit", r.toUnit().name());
        assertEquals(100.0, r.inputValue(), 0.001);
        assertEquals(212.0, r.resultValue(), 0.001);
        assertNotNull(r.createdAt());
    }

    @Test
    void newestRecordComesFirst() throws SQLException {
        TemperatureUnit c = unitDAO.findByName("Celsius").orElseThrow();
        TemperatureUnit k = unitDAO.findByName("Kelvin").orElseThrow();
        recordDAO.insert(c, k, 0, 273.15);
        int second = recordDAO.insert(k, c, 300, 26.85);

        assertEquals(second, recordDAO.findAll().get(0).id());
    }

    @Test
    void deleteAllEmptiesTable() throws SQLException {
        TemperatureUnit c = unitDAO.findByName("Celsius").orElseThrow();
        recordDAO.insert(c, c, 1, 1);
        recordDAO.deleteAll();
        assertTrue(recordDAO.findAll().isEmpty());
    }
}
