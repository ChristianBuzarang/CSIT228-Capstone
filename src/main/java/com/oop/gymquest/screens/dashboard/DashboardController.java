package com.oop.gymquest.screens.dashboard;

import com.oop.gymquest.app.MainApp;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    // ── FXML injections ────────────────────────────────────────────────────
    @FXML private AreaChart<String, Number> activityChart;

    @FXML private TableView<String[]> attendanceTable;
    @FXML private TableColumn<String[], String> sessionCol;
    @FXML private TableColumn<String[], String> timeCol;

    // ── Data ───────────────────────────────────────────────────────────────
    private static final List<Number> ACTIVITY_DATA =
            List.of(20, 35, 60, 75, 100, 85, 110);

    private static final String[] DAYS =
            {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    private static final String[][] ATTENDANCE_ROWS = {
            {"Power Yoga - 11:30 AM", "10:15 AM"},
            {"Sarah Flow - 2:00 AM",  "10:00 AM"},
            {"Power Yoga - 11:30 AM", "10:15 AM"},
            {"Sarah Flow - 2:00 AM",  "10:30 AM"},
            {"Power Yoga - 11:30 AM", "10:15 AM"},
    };

    // ── Initialise ─────────────────────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupChart();
        setupTable();
    }

    private void setupChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (int i = 0; i < DAYS.length; i++) {
            series.getData().add(new XYChart.Data<>(DAYS[i], ACTIVITY_DATA.get(i)));
        }
        activityChart.getData().add(series);
    }

    private void setupTable() {
        sessionCol.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue()[0]));
        timeCol.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue()[1]));

        ObservableList<String[]> rows = FXCollections.observableArrayList(ATTENDANCE_ROWS);
        attendanceTable.setItems(rows);
    }

    @FXML
    public static void handleAction() {
        MainApp.instance.changeScene("/com/oop/gymquest/dashboardView.fxml", "GymQuest");
    }
}