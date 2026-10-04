package assignments.inclass1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TemperatureUnitDAO {

    public List<TemperatureUnit> findAll() throws SQLException {
        List<TemperatureUnit> units = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, name, symbol FROM temperature_unit ORDER BY id")) {
            while (rs.next()) {
                units.add(new TemperatureUnit(rs.getInt("id"), rs.getString("name"), rs.getString("symbol")));
            }
        }
        return units;
    }

    public Optional<TemperatureUnit> findByName(String name) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id, name, symbol FROM temperature_unit WHERE name = ?")) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new TemperatureUnit(rs.getInt("id"), rs.getString("name"), rs.getString("symbol")));
                }
            }
        }
        return Optional.empty();
    }
}
