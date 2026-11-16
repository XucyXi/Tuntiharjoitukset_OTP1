package assignments.inclass1;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private final TemperatureConverter converter = new TemperatureConverter();
    private final TemperatureUnitDAO unitDAO = new TemperatureUnitDAO();
    private final TempRecordDAO recordDAO = new TempRecordDAO();
    private final ObservableList<TempRecord> history = FXCollections.observableArrayList();

    private ResourceBundle bundle = ResourceBundle.getBundle("MessagesBundle", Locale.of("en", "US"));
    private String currentDbLang = "en";

    private ComboBox<TemperatureUnit> fromCombo;
    private ComboBox<TemperatureUnit> toCombo;

    @Override
    public void start(Stage stage) throws SQLException {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        ComboBox<String> langBox = new ComboBox<>(FXCollections.observableArrayList(
                "English (en_US)", "Finnish (fi_FI)", "Swedish (sv_SE)", "Japanese (ja_JP)", "Arabic (ar_SA)"
        ));
        langBox.getSelectionModel().select(0);

        TextField input = new TextField();
        fromCombo = new ComboBox<>();
        toCombo = new ComboBox<>();
        updateDropdowns("en");

        Button convert = new Button();
        Label result = new Label();
        Label extremeLabel = new Label();
        Label historyLabel = new Label();

        TableView<TempRecord> table = new TableView<>(history);

        TableColumn<TempRecord, String> colFrom = column("", r -> r.fromUnit().symbol());
        TableColumn<TempRecord, String> colTo = column("", r -> r.toUnit().symbol());
        TableColumn<TempRecord, String> colInput = column("", r -> String.format("%.2f", r.inputValue()));
        TableColumn<TempRecord, String> colResult = column("", r -> String.format("%.2f", r.resultValue()));
        TableColumn<TempRecord, String> colTime = column("", r -> r.createdAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));

        table.getColumns().addAll(colFrom, colTo, colInput, colResult, colTime);

        VBox root = new VBox(10,
                new HBox(10, new Label("Language:"), langBox),
                new HBox(10, input, fromCombo, toCombo, convert),
                result,
                extremeLabel,
                historyLabel,
                table
        );
        root.setPadding(new Insets(15));
        root.setStyle("-fx-font-family: 'Meiryo UI', 'Yu Gothic UI', 'Segoe UI', sans-serif;");

        Runnable updateUI = () -> {
            stage.setTitle(bundle.getString("app.title"));
            input.setPromptText(bundle.getString("prompt.temp"));
            convert.setText(bundle.getString("button.convert"));
            historyLabel.setText(bundle.getString("label.history"));
            colFrom.setText(bundle.getString("col.from"));
            colTo.setText(bundle.getString("col.to"));
            colInput.setText(bundle.getString("col.input"));
            colResult.setText(bundle.getString("col.result"));
            colTime.setText(bundle.getString("col.time"));

            try {
                updateDropdowns(currentDbLang);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Tietokantavirhe pudotusvalikkoja päivitettäessä", e);
            }
        };

        langBox.setOnAction(e -> {
            Locale loc = switch (langBox.getSelectionModel().getSelectedIndex()) {
                case 1 -> { currentDbLang = "fi"; yield Locale.of("fi", "FI"); }
                case 2 -> { currentDbLang = "sv"; yield Locale.of("sv", "SE"); }
                case 3 -> { currentDbLang = "ja"; yield Locale.of("ja", "JP"); }
                case 4 -> { currentDbLang = "ar"; yield Locale.of("ar", "SA"); }
                default -> { currentDbLang = "en"; yield Locale.of("en", "US"); }
            };

            bundle = ResourceBundle.getBundle("MessagesBundle", loc);
            updateUI.run();

            if (loc.getLanguage().equals("ar")) {
                root.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            } else {
                root.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
            }
        });

        convert.setOnAction(e -> {
            try {
                double value = Double.parseDouble(input.getText().trim().replace(',', '.'));
                TemperatureUnit f = fromCombo.getValue();
                TemperatureUnit t = toCombo.getValue();

                double res = converter.convert(f.symbol(), t.symbol(), value);

                // Tallennetaan tietokantaan
                recordDAO.insert(f, t, value, res);

                result.setText(String.format("%.2f %s = %.2f %s", value, f.symbol(), res, t.symbol()));

                double celsiusVal = converter.convert(f.symbol(), "°C", value);
                String extremeText = converter.isExtremeTemperature(celsiusVal)
                        ? MessageFormat.format(bundle.getString("result.extreme.yes"), String.format("%.2f", celsiusVal))
                        : MessageFormat.format(bundle.getString("result.extreme.no"), String.format("%.2f", celsiusVal));
                extremeLabel.setText(extremeText);

                // Päivitetään historia taulukkoon
                history.setAll(recordDAO.findAll());

            } catch (NumberFormatException ex) {
                result.setText(bundle.getString("error.invalid"));
            } catch (SQLException ex) {
                result.setText(bundle.getString("error.db") + " " + ex.getMessage());
            }
        });

        updateUI.run();

        try {
            history.setAll(recordDAO.findAll());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Virhe ladattaessa historiaa", e);
        }

        stage.setScene(new Scene(root, 650, 500));
        stage.show();
    }

    private void updateDropdowns(String lang) throws SQLException {
        int fromIndex = fromCombo.getSelectionModel().getSelectedIndex();
        int toIndex = toCombo.getSelectionModel().getSelectedIndex();

        ObservableList<TemperatureUnit> localizedUnits = FXCollections.observableArrayList(unitDAO.findAllLocalized(lang));
        fromCombo.setItems(localizedUnits);
        toCombo.setItems(localizedUnits);

        if (fromIndex >= 0) fromCombo.getSelectionModel().select(fromIndex);
        else fromCombo.getSelectionModel().select(0);

        if (toIndex >= 0) toCombo.getSelectionModel().select(toIndex);
        else toCombo.getSelectionModel().select(1);
    }

    private TableColumn<TempRecord, String> column(String title, java.util.function.Function<TempRecord, String> f) {
        TableColumn<TempRecord, String> col = new TableColumn<>(title);
        col.setCellValueFactory(cd -> new SimpleStringProperty(f.apply(cd.getValue())));
        return col;
    }

    public static void main(String[] args) {
        launch(args);
    }
}