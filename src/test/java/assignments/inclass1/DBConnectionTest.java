package assignments.inclass1;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class DBConnectionTest extends DBTestBase {

    @Test
    void connectionIsOpen() throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            assertFalse(c.isClosed());
        }
    }

    @Test
    void schemaAndSeedDataAreCreated() throws SQLException {
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM temperature_unit")) {
            assertTrue(rs.next());
            assertEquals(3, rs.getInt(1));
        }
    }

    @Test
    void recordTableExists() throws SQLException {
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM temp_record")) {
            assertTrue(rs.next());
            assertEquals(0, rs.getInt(1));
        }
    }
}
