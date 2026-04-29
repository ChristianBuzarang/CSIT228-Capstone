package com.gymquest.view;

import com.gymquest.controller.MainController;
import com.gymquest.model.AppState;
import com.gymquest.util.UIHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginView {

    private final StackPane root;
    private final AppState state;
    private boolean isRegister = false;
    private AppState.UserType selectedType = AppState.UserType.MEMBER;

    public LoginView(AppState state) {
        this.state = state;
        root = new StackPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #3b82f6, #60a5fa);");
        buildUI();
    }

    private void buildUI() {
        VBox card = new VBox(20);
        card.setPadding(new Insets(40));
        card.setMaxWidth(420);
        card.setStyle(
            "-fx-background-color: white; -fx-background-radius: 24;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 24, 0, 0, 8);"
        );
        card.setAlignment(Pos.TOP_CENTER);

        // Mascot emoji placeholder
        Label mascot = new Label("🏋️");
        mascot.setStyle("-fx-font-size: 64px;");

        Label appTitle = new Label("GymQuest");
        appTitle.setFont(Font.font("System", FontWeight.BOLD, 32));
        appTitle.setStyle("-fx-text-fill: #3b82f6;");

        Label tagline = new Label("Welcome back!");
        tagline.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");

        VBox form = buildForm();

        card.getChildren().addAll(mascot, appTitle, tagline, form);
        root.getChildren().add(card);
        StackPane.setAlignment(card, Pos.CENTER);
    }

    private VBox buildForm() {
        VBox form = new VBox(16);
        form.setMaxWidth(340);

        // Account type toggle (only shown for register)
        HBox typeRow = new HBox(8);
        typeRow.setAlignment(Pos.CENTER);
        Button memberBtn = typeBtn("Member", AppState.UserType.MEMBER, typeRow);
        Button trainerBtn = typeBtn("Trainer", AppState.UserType.TRAINER, typeRow);
        Button adminBtn = typeBtn("Admin", AppState.UserType.ADMIN, typeRow);
        typeRow.getChildren().addAll(memberBtn, trainerBtn, adminBtn);
        typeRow.setVisible(false);
        typeRow.setManaged(false);

        // Fields
        TextField usernameField = styledField("Enter your username", false);
        PasswordField passwordField = new PasswordField();
        styleField(passwordField, "Enter your password");
        TextField emailField = styledField("Enter your email", false);
        emailField.setVisible(false);
        emailField.setManaged(false);

        // Submit
        Button submitBtn = UIHelper.primaryBtn("Sign In");
        submitBtn.setMaxWidth(Double.MAX_VALUE);
        submitBtn.setStyle(submitBtn.getStyle() + " -fx-font-size: 15px; -fx-padding: 14 20;");

        // Toggle link
        Hyperlink toggleLink = new Hyperlink("Don't have an account? Register");
        toggleLink.setStyle("-fx-text-fill: #3b82f6;");

        Label typeLabel = new Label("Account Type");
        typeLabel.setStyle("-fx-text-fill: #1e3a5f;");
        Label userLabel = new Label("Username");
        userLabel.setStyle("-fx-text-fill: #1e3a5f;");
        Label passLabel = new Label("Password");
        passLabel.setStyle("-fx-text-fill: #1e3a5f;");
        Label emailLabel = new Label("Email");
        emailLabel.setStyle("-fx-text-fill: #1e3a5f;");
        emailLabel.setVisible(false);
        emailLabel.setManaged(false);

        toggleLink.setOnAction(e -> {
            isRegister = !isRegister;
            typeRow.setVisible(isRegister);
            typeRow.setManaged(isRegister);
            emailField.setVisible(isRegister);
            emailField.setManaged(isRegister);
            emailLabel.setVisible(isRegister);
            emailLabel.setManaged(isRegister);
            submitBtn.setText(isRegister ? "Create Account" : "Sign In");
            toggleLink.setText(isRegister
                ? "Already have an account? Sign in"
                : "Don't have an account? Register");
        });

        submitBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            if (username.isEmpty()) {
                showAlert("Please enter a username.");
                return;
            }
            state.login(selectedType, username);
            MainController.showMainScreen();
        });

        form.getChildren().addAll(
            typeLabel, typeRow,
            userLabel, usernameField,
            passLabel, passwordField,
            emailLabel, emailField,
            submitBtn, toggleLink
        );
        form.setAlignment(Pos.CENTER_LEFT);
        return form;
    }

    private Button typeBtn(String text, AppState.UserType type, HBox container) {
        Button b = new Button(text);
        boolean active = type == selectedType;
        styleTypeBtn(b, active);
        b.setOnAction(e -> {
            selectedType = type;
            container.getChildren().forEach(n -> {
                if (n instanceof Button btn) {
                    styleTypeBtn(btn, btn == b);
                }
            });
        });
        return b;
    }

    private void styleTypeBtn(Button b, boolean active) {
        b.setStyle(
            (active
                ? "-fx-background-color: #3b82f6; -fx-text-fill: white;"
                : "-fx-background-color: transparent; -fx-text-fill: #64748b;" +
                  "-fx-border-color: #bae6fd; -fx-border-width: 2;") +
            " -fx-padding: 8 16; -fx-background-radius: 10; -fx-border-radius: 10;" +
            " -fx-cursor: hand; -fx-font-size: 13px;"
        );
    }

    private TextField styledField(String prompt, boolean password) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        styleField(f, prompt);
        return f;
    }

    private void styleField(TextInputControl f, String prompt) {
        f.setPromptText(prompt);
        f.setStyle(
            "-fx-padding: 12 16; -fx-border-color: #bae6fd; -fx-border-width: 2;" +
            "-fx-background-radius: 12; -fx-border-radius: 12; -fx-font-size: 14px;"
        );
        f.setMaxWidth(Double.MAX_VALUE);
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public StackPane getRoot() { return root; }
}
