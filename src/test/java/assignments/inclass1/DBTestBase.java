package assignments.inclass1;

import org.junit.jupiter.api.BeforeEach;

import java.util.UUID;

/** Antaa jokaiselle testille oman tyhjän muistitietokannan. */
abstract class DBTestBase {

    @BeforeEach
    void useFreshInMemoryDb() {
        DBConnection.setUrl("jdbc:h2:mem:" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
    }
}
