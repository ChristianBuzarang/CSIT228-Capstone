package com.oop.gymquest.screens.profile;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class AvatarPickerController {
    @FXML private GridPane avatarGrid;

    @FXML public void initialize() {
        avatarGrid.getChildren().forEach(node -> {
            if (node instanceof Button btn) {
                String imageName = (String) btn.getUserData();
                Image img = new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/" + imageName));
                ImageView view = new ImageView(img);
                view.setFitHeight(80);
                view.setFitWidth(80);
                btn.setGraphic(view);
                btn.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-cursor: hand;");
            }
        });
    }

    @FXML private void onAvatarSelected(ActionEvent event) {
        Button clicked = (Button) event.getSource();
        String selectedImage = (String) clicked.getUserData();
        ProfileController.instance.updateProfilePicture(selectedImage);
        handleCancel();
    }

    @FXML private void handleCancel() {
        ((Stage) avatarGrid.getScene().getWindow()).close();
    }
}