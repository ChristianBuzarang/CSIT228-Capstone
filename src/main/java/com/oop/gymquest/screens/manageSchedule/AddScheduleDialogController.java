package com.oop.gymquest.screens.manageSchedule;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.DatabaseHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddScheduleDialogController {
    @FXML
    private TextField typeField;
    @FXML
    private TextField timeField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<String> durationBox;

    private ManageScheduleController parent;

    @FXML
    public void initialize() {
        durationBox.getItems().addAll("30 minutes", "45 minutes", "60 minutes", "90 minutes");
        durationBox.setValue("60 minutes");
    }

    public void setParent(ManageScheduleController p) {
        this.parent = p;
    }

    @FXML
    private void handleSave() {
        String type = typeField.getText();
        String date = (datePicker.getValue() != null) ? datePicker.getValue().toString() : "";
        String time = timeField.getText();
        String dur = durationBox.getValue();

        if (type.isEmpty() || date.isEmpty() || time.isEmpty()) {
            return;
        }

        int tid = MainApp.instance.currentUser.getUserId();
        boolean success = DatabaseHandler.addTrainerSlot(tid, type, date, time, dur);

        if (success) {
            parent.refreshView();
            handleCancel();
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) typeField.getScene().getWindow();
        stage.close();
    }
}