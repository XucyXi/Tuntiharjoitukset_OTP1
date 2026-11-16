package assignments.inclass1;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** Luo yhteyden H2-tietokantaan ja alustaa taulut lukemalla init.sql -tiedoston. */
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
        // Haetaan init.sql resources-kansiosta
        try (InputStream is = DBConnection.class.getClassLoader().getResourceAsStream("init.sql");
             Statement st = connection.createStatement()) {

            if (is == null) {
                System.err.println("Tietokantaa ei voitu alustaa: init.sql -tiedostoa ei löytynyt!");
                return;
            }

            // Luetaan tiedoston sisältö merkkijonoksi UTF-8 muodossa (tärkeää japanin ja arabian kannalta)
            String sql = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            // Ajetaan koko SQL-skripti kerralla
            st.execute(sql);
            System.out.println("Tietokanta alustettu onnistuneesti init.sql -tiedostosta.");

        } catch (Exception e) {
            System.err.println("Virhe tietokannan alustuksessa: " + e.getMessage());
            e.printStackTrace();
        }
    }
}