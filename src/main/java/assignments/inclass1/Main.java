package assignments.inclass1;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
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

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class Main extends Application {

    private final TemperatureConverter converter = new TemperatureConverter();
    private final TemperatureUnitDAO unitDAO = new TemperatureUnitDAO();
    private final TempRecordDAO recordDAO = new TempRecordDAO();
    private final ObservableList<TempRecord> history = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) throws SQLException {
        ObservableList<TemperatureUnit> units = FXCollections.observableArrayList(unitDAO.findAll());

        TextField input = new TextField();
        input.setPromptText("Arvo");
        ComboBox<TemperatureUnit> from = new ComboBox<>(units);
        ComboBox<TemperatureUnit> to = new ComboBox<>(units);
        from.getSelectionModel().select(0);
        to.getSelectionModel().select(1);
        Button convert = new Button("Muunna");
        Label result = new Label();

        TableView<TempRecord> table = new TableView<>(history);
        table.getColumns().add(column("Mistä", r -> r.fromUnit().symbol()));
        table.getColumns().add(column("Mihin", r -> r.toUnit().symbol()));
        table.getColumns().add(column("Syöte", r -> String.format("%.2f", r.inputValue())));
        table.getColumns().add(column("Tulos", r -> String.format("%.2f", r.resultValue())));
        table.getColumns().add(column("Aika", r -> r.createdAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))));

        convert.setOnAction(e -> {
            try {
                double value = Double.parseDouble(input.getText().trim().replace(',', '.'));
                TemperatureUnit f = from.getValue();
                TemperatureUnit t = to.getValue();
                double res = converter.convert(f.name(), t.name(), value);
                recordDAO.insert(f, t, value, res);
                result.setText(String.format("%.2f %s = %.2f %s", value, f.symbol(), res, t.symbol()));
                history.setAll(recordDAO.findAll());
            } catch (NumberFormatException ex) {
                result.setText("Syötä kelvollinen luku.");
            } catch (SQLException ex) {
                result.setText("Tietokantavirhe: " + ex.getMessage());
            }
        });

        history.setAll(recordDAO.findAll());

        VBox root = new VBox(10, new HBox(10, input, from, to, convert), result, new Label("Historia"), table);
        root.setPadding(new Insets(15));
        stage.setTitle("Temperature Converter");
        stage.setScene(new Scene(root, 650, 450));
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
