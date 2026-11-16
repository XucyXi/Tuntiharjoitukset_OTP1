package assignments.inclass1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TemperatureUnitDAO {

    public List<TemperatureUnit> findAllLocalized(String languageCode) throws SQLException {
        List<TemperatureUnit> units = new ArrayList<>();

        // Haetaan käännös valitulla kielellä, jos ei löydy, haetaan enkuksi
        String sql = """
            SELECT u.id, u.symbol, 
                   COALESCE(t1.name, t2.name) as localized_name
            FROM temperature_unit u
            LEFT JOIN unit_translation t1 ON u.id = t1.unit_id AND t1.lang = ?
            LEFT JOIN unit_translation t2 ON u.id = t2.unit_id AND t2.lang = 'en'
            ORDER BY u.id
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, languageCode);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    units.add(new TemperatureUnit(
                            rs.getInt("id"),
                            rs.getString("symbol"),
                            rs.getString("localized_name")
                    ));
                }
            }
        }
        return units;
    }
}