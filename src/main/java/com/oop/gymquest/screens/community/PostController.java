package com.oop.gymquest.screens.community;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.DatabaseHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class PostController {
    @FXML private TextArea contentArea;
    @FXML private TextField milestoneField;

    @FXML
    private void handlePost() {
        String content = contentArea.getText().trim();
        String milestone = milestoneField.getText().trim();

        if (content.isEmpty()) return;

        DatabaseHandler.createPost(MainApp.instance.currentUser.getUserId(), content, milestone);
        close();
    }

    @FXML private void handleCancel() { close(); }
    private void close() { ((Stage) contentArea.getScene().getWindow()).close(); }
}