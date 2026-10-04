package assignments.inclass1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** Luo yhteyden H2-tietokantaan ja alustaa taulut (temperature_unit, temp_record). */
public class DBConnection {

    private static String url = System.getProperty("db.url", "jdbc:h2:./data/tempdb");
    private static boolean initialized = false;

    private DBConnection() {
    }

    /** Vaihtaa tietokannan osoitetta (testeissä käytetään muistitietokantaa). */
    public static synchronized void setUrl(String newUrl) {
        url = newUrl;
        initialized = false;
    }

    public static synchronized Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(url, "sa", "");
        if (!initialized) {
            initSchema(connection);
            initialized = true;
        }
        return connection;
    }

    private static void initSchema(Connection connection) throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS temperature_unit ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "name VARCHAR(30) NOT NULL UNIQUE, "
                    + "symbol VARCHAR(5) NOT NULL)");
            st.execute("CREATE TABLE IF NOT EXISTS temp_record ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "from_unit_id INT NOT NULL, "
                    + "to_unit_id INT NOT NULL, "
                    + "input_value DOUBLE NOT NULL, "
                    + "result_value DOUBLE NOT NULL, "
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                    + "FOREIGN KEY (from_unit_id) REFERENCES temperature_unit(id), "
                    + "FOREIGN KEY (to_unit_id) REFERENCES temperature_unit(id))");
            st.execute("MERGE INTO temperature_unit (name, symbol) KEY(name) VALUES ('Celsius', '°C')");
            st.execute("MERGE INTO temperature_unit (name, symbol) KEY(name) VALUES ('Fahrenheit', '°F')");
            st.execute("MERGE INTO temperature_unit (name, symbol) KEY(name) VALUES ('Kelvin', 'K')");
        }
    }
}
