package assignments.inclass1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TempRecordDAO {

    /** Tallentaa muunnoksen ja palauttaa luodun rivin id:n. */
    public int insert(TemperatureUnit from, TemperatureUnit to, double input, double result) throws SQLException {
        String sql = "INSERT INTO temp_record (from_unit_id, to_unit_id, input_value, result_value) VALUES (?, ?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, from.id());
            ps.setInt(2, to.id());
            ps.setDouble(3, input);
            ps.setDouble(4, result);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    /** Hakee kaikki muunnokset (uusin ensin) liittämällä yksikkötaulun kahdesti. */
    public List<TempRecord> findAll() throws SQLException {
        String sql = "SELECT r.id, r.input_value, r.result_value, r.created_at, "
                + "f.id AS fid, f.name AS fname, f.symbol AS fsym, "
                + "t.id AS tid, t.name AS tname, t.symbol AS tsym "
                + "FROM temp_record r "
                + "JOIN temperature_unit f ON r.from_unit_id = f.id "
                + "JOIN temperature_unit t ON r.to_unit_id = t.id "
                + "ORDER BY r.id DESC";
        List<TempRecord> records = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                records.add(new TempRecord(
                        rs.getInt("id"),
                        new TemperatureUnit(rs.getInt("fid"), rs.getString("fname"), rs.getString("fsym")),
                        new TemperatureUnit(rs.getInt("tid"), rs.getString("tname"), rs.getString("tsym")),
                        rs.getDouble("input_value"),
                        rs.getDouble("result_value"),
                        rs.getTimestamp("created_at").toLocalDateTime()));
            }
        }
        return records;
    }

    public void deleteAll() throws SQLException {
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement()) {
            st.executeUpdate("DELETE FROM temp_record");
        }
    }
}
