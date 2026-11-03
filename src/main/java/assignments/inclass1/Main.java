package assignments.inclass1;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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

public class Main extends Application {

    private final TemperatureConverter converter = new TemperatureConverter();
    private final TemperatureUnitDAO unitDAO = new TemperatureUnitDAO();
    private final TempRecordDAO recordDAO = new TempRecordDAO();
    private final ObservableList<TempRecord> history = FXCollections.observableArrayList();

    private ResourceBundle bundle = ResourceBundle.getBundle("MessagesBundle", Locale.of("en", "US"));

    @Override
    public void start(Stage stage) throws SQLException {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        ObservableList<TemperatureUnit> units = FXCollections.observableArrayList(unitDAO.findAll());

        // Kielivalinta käyttöliittymässä (sisältää arabian)
        ComboBox<String> langBox = new ComboBox<>(FXCollections.observableArrayList(
                "English (en_US)", "Finnish (fi_FI)", "Swedish (sv_SE)", "Japanese (ja_JP)", "Arabic (ar_SA)"
        ));
        langBox.getSelectionModel().select(0);

        TextField input = new TextField();
        ComboBox<TemperatureUnit> from = new ComboBox<>(units);
        ComboBox<TemperatureUnit> to = new ComboBox<>(units);
        from.getSelectionModel().select(0);
        to.getSelectionModel().select(1);
        Button convert = new Button();
        Label result = new Label();
        Label extremeLabel = new Label();
        Label historyLabel = new Label();

        TableView<TempRecord> table = new TableView<>(history);
        TableColumn<TempRecord, String> colFrom = column(bundle.getString("col.from"), r -> r.fromUnit().symbol());
        TableColumn<TempRecord, String> colTo = column(bundle.getString("col.to"), r -> r.toUnit().symbol());
        TableColumn<TempRecord, String> colInput = column(bundle.getString("col.input"), r -> String.format("%.2f", r.inputValue()));
        TableColumn<TempRecord, String> colResult = column(bundle.getString("col.result"), r -> String.format("%.2f", r.resultValue()));
        TableColumn<TempRecord, String> colTime = column(bundle.getString("col.time"), r -> r.createdAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));

        table.getColumns().add(colFrom);
        table.getColumns().add(colTo);
        table.getColumns().add(colInput);
        table.getColumns().add(colResult);
        table.getColumns().add(colTime);

        VBox root = new VBox(10,
                new HBox(10, new Label("Language:"), langBox),
                new HBox(10, input, from, to, convert),
                result,
                extremeLabel,
                historyLabel,
                table
        );
        root.setPadding(new Insets(15));
        root.setStyle("-fx-font-family: 'Meiryo UI', 'Yu Gothic UI', 'Segoe UI', sans-serif;");

        Runnable updateTexts = () -> {
            stage.setTitle(bundle.getString("app.title"));
            input.setPromptText(bundle.getString("prompt.temp"));
            convert.setText(bundle.getString("button.convert"));
            historyLabel.setText(bundle.getString("label.history"));
            colFrom.setText(bundle.getString("col.from"));
            colTo.setText(bundle.getString("col.to"));
            colInput.setText(bundle.getString("col.input"));
            colResult.setText(bundle.getString("col.result"));
            colTime.setText(bundle.getString("col.time"));
        };

        langBox.setOnAction(e -> {
            Locale loc = switch (langBox.getSelectionModel().getSelectedIndex()) {
                case 1 -> Locale.of("fi", "FI");
                case 2 -> Locale.of("sv", "SE");
                case 3 -> Locale.of("ja", "JP");
                case 4 -> Locale.of("ar", "SA");
                default -> Locale.of("en", "US");
            };

            bundle = ResourceBundle.getBundle("MessagesBundle", loc);
            updateTexts.run();

            // Vaihdetaan käyttöliittymän suunta oikealta vasemmalle (RTL) arabiaa varten
            if (loc.getLanguage().equals("ar")) {
                root.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            } else {
                root.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
            }

            System.out.println("Selected language: " + langBox.getValue());
        });

        convert.setOnAction(e -> {
            try {
                double value = Double.parseDouble(input.getText().trim().replace(',', '.'));
                TemperatureUnit f = from.getValue();
                TemperatureUnit t = to.getValue();
                double res = converter.convert(f.name(), t.name(), value);
                recordDAO.insert(f, t, value, res);

                String resText = String.format("%.2f %s = %.2f %s", value, f.symbol(), res, t.symbol());
                result.setText(resText);

                double celsiusVal = converter.convert(f.name(), "Celsius", value);
                String extremeText = converter.isExtremeTemperature(celsiusVal)
                        ? MessageFormat.format(bundle.getString("result.extreme.yes"), String.format("%.2f", celsiusVal))
                        : MessageFormat.format(bundle.getString("result.extreme.no"), String.format("%.2f", celsiusVal));
                extremeLabel.setText(extremeText);

                System.out.println(bundle.getString("prompt.temp") + ": " + value);
                System.out.println(resText);
                System.out.println(extremeText);

                history.setAll(recordDAO.findAll());
            } catch (NumberFormatException ex) {
                result.setText(bundle.getString("error.invalid"));
            } catch (SQLException ex) {
                result.setText(bundle.getString("error.db") + " " + ex.getMessage());
            }
        });

        updateTexts.run();
        history.setAll(recordDAO.findAll());

        stage.setScene(new Scene(root, 650, 500));
        stage.show();
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