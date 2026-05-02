package com.oop.gymquest.screens.register;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.DatabaseHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class RegisterController {

    // FIX: Add this line so RegisterApplication can find the symbol
    public static RegisterController instance;

    @FXML private TextField firstnameField, lastnameField, emailField;
    @FXML private PasswordField passField, confirmPassField;
    @FXML private TextField passFieldVisible, confirmPassFieldVisible;
    @FXML private ImageView passIcon, confirmIcon;
    @FXML private Label statusLabel;
    @FXML private Button memberBtn, trainerBtn;

    private String selectedType = "member";
    private boolean isPassVisible = false, isConfirmVisible = false;

    // Load images from resources
    private final Image SEE_IMG = new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/see-password.png"));
    private final Image NOT_SEE_IMG = new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/not-see-password.png"));

    public RegisterController() {
        instance = this;
    }

    @FXML
    public void initialize() {
        selectMember();
    }

    @FXML private void selectMember() {
        setType("member", memberBtn);
    }

    @FXML private void selectTrainer() {
        setType("trainer", trainerBtn);
    }

    private void setType(String type, Button b) {
        this.selectedType = type;

        String active = "-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 12 25; -fx-font-weight: bold;";
        String inactive = "-fx-background-color: white; -fx-text-fill: #64748b; -fx-background-radius: 15; -fx-border-color: #e2e8f0; -fx-border-radius: 15; -fx-padding: 12 25;";

        memberBtn.setStyle(inactive);
        trainerBtn.setStyle(inactive);

        b.setStyle(active);
    }

    /**
     * Password Visibility Toggle (Image-based)
     */
    @FXML
    private void togglePassword() {
        isPassVisible = !isPassVisible;
        handleToggle(passField, passFieldVisible, passIcon, isPassVisible);
    }

    @FXML
    private void toggleConfirmPassword() {
        isConfirmVisible = !isConfirmVisible;
        handleToggle(confirmPassField, confirmPassFieldVisible, confirmIcon, isConfirmVisible);
    }

    private void handleToggle(PasswordField p, TextField t, ImageView i, boolean visible) {
        if (visible) {
            t.setText(p.getText());
            t.setVisible(true);
            p.setVisible(false);
            i.setImage(NOT_SEE_IMG);
        } else {
            p.setText(t.getText());
            p.setVisible(true);
            t.setVisible(false);
            i.setImage(SEE_IMG);
        }
    }

    /**
     * Registration Logic with Confirm Password Check
     */
    @FXML
    public void handleRegister() {
        String fname = firstnameField.getText();
        String lname = lastnameField.getText();
        String email = emailField.getText();

        // Get passwords from whichever field is currently active
        String p1 = isPassVisible ? passFieldVisible.getText() : passField.getText();
        String p2 = isConfirmVisible ? confirmPassFieldVisible.getText() : confirmPassField.getText();

        // 1. Check for empty fields
        if (fname.isEmpty() || lname.isEmpty() || email.isEmpty() || p1.isEmpty()) {
            showError("Please fill all fields");
            return;
        }

        // 2. Confirm Password Match Check
        if (!p1.equals(p2)) {
            showError("Passwords do not match!");
            return;
        }

        // 3. Database Attempt
        if (DatabaseHandler.registerUser(email, p1, fname, lname, selectedType)) {
            statusLabel.setText("Account created successfully!");
            statusLabel.setStyle("-fx-text-fill: #10b981;");
            MainApp.instance.changeScene("login.fxml", "GymQuest - Login");
        } else {
            showError("Registration failed. Username may already exist.");
        }
    }

    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
    }

    @FXML
    private void goToLogin() {
        MainApp.instance.changeScene("login.fxml", "GymQuest - Login");
    }
}