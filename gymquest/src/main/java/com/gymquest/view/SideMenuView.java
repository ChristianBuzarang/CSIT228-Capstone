package com.gymquest.view;

import com.gymquest.controller.MainController;
import com.gymquest.util.UIHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;

public class SideMenuView {

    private final VBox root;
    private final List<Button> menuButtons = new ArrayList<>();
    private String activePage = "dashboard";

    private static final String[][] ITEMS = {
        {"🏠", "Dashboard", "dashboard"},
        {"💪", "Workouts", "workouts"},
        {"📅", "Book Trainer", "booking"},
        {"👥", "Community", "community"},
        {"👤", "Profile", "profile"}
    };

    public SideMenuView(MainController controller) {
        root = new VBox(4);
        root.setPrefWidth(220);
        root.setPadding(new Insets(24, 12, 24, 12));
        root.setStyle("-fx-background-color: white; -fx-border-color: #bae6fd; -fx-border-width: 0 1 0 0;");

        for (String[] item : ITEMS) {
            Button btn = menuBtn(item[0], item[1], item[2], controller);
            menuButtons.add(btn);
            root.getChildren().add(btn);
        }

        updateActive();
    }

    private Button menuBtn(String icon, String label, String page, MainController controller) {
        HBox content = new HBox(12);
        content.setAlignment(Pos.CENTER_LEFT);

        javafx.scene.control.Label iconLabel = new javafx.scene.control.Label(icon);
        iconLabel.setStyle("-fx-font-size: 18px;");
        javafx.scene.control.Label textLabel = new javafx.scene.control.Label(label);
        textLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #1e3a5f;");

        content.getChildren().addAll(iconLabel, textLabel);

        Button btn = new Button();
        btn.setGraphic(content);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setUserData(page);
        btn.setOnAction(e -> {
            activePage = page;
            updateActive();
            controller.navigateTo(page);
        });

        return btn;
    }

    public void setActivePage(String page) {
        activePage = page;
        updateActive();
    }

    private void updateActive() {
        for (Button btn : menuButtons) {
            boolean active = btn.getUserData().equals(activePage);
            btn.setStyle(
                "-fx-background-color: " + (active ? "#e0f2fe" : "transparent") + ";" +
                "-fx-background-radius: 12; -fx-border-radius: 12;" +
                "-fx-padding: 10 14; -fx-cursor: hand;" +
                (active ? "-fx-border-color: #3b82f6; -fx-border-width: 0 0 0 3;" : "")
            );
            // Update text color
            if (btn.getGraphic() instanceof HBox hbox) {
                hbox.getChildren().forEach(n -> {
                    if (n instanceof javafx.scene.control.Label lbl && !lbl.getText().matches(".*\\p{So}.*")) {
                        lbl.setStyle("-fx-font-size: 14px; -fx-text-fill: " +
                            (active ? "#3b82f6" : "#1e3a5f") + ";" +
                            (active ? "-fx-font-weight: bold;" : ""));
                    }
                });
            }
        }
    }

    public VBox getRoot() { return root; }
}
