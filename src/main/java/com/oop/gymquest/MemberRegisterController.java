package com.oop.gymquest;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MemberRegisterController {

    @FXML private TextField nameTextField;
    @FXML private TextField emailTextField;
    @FXML private ComboBox<String> comboPlan;
    @FXML private TextArea goalTextField;


    @FXML
    public void initialize() {
       comboPlan.getItems().addAll("Basic", "Gold", "VIP");
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (validateInputs()) {
            saveMember();
        }

        // temporary
        String name = nameTextField.getText().trim();
        String plan = comboPlan.getValue();

        System.out.println("Saving Member...");
        System.out.println("Name: " + name + ", Plan: " + plan);

    }

    @FXML
    private void handleCancel(ActionEvent event) {
        clearForm();
        Stage stage = (Stage) nameTextField.getScene().getWindow();
        stage.close();
    }

    private boolean validateInputs(){

        String name = nameTextField.getText().trim();
        String email = emailTextField.getText().trim();
        String plan = comboPlan.getValue();
        String goal = goalTextField.getText().trim();

        // check required fields
        if(name.isEmpty()){
            showAlert("Validation Error", "Name is required.");
            return false;
        }else if(name.length() < 2){
            showAlert("Validation Error", "Name must be at least 2 characters long.");
            return false;
        }

        if (email.isEmpty()) {
            showAlert("Validation Error", "Email is required!");
            return false;
        }else if(!email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
            showAlert("Validation Error", "Please enter a valid email address.");
            return false;
        }

        if (plan == null || plan.isEmpty()) {
            showAlert("Validation Error", "Please select a membership plan.");
            return false;
        }

        if (goal.isEmpty()) {
            showAlert("Validation Error", "Please enter your fitness goal.");
            return false;
        }

        System.out.println("Registration successful");
        return true;
    }

    private void saveMember() {
        String name = nameTextField.getText().trim();
        String email = emailTextField.getText().trim();
        String plan = comboPlan.getValue();
        String goal = goalTextField.getText().trim();

        System.out.println("Saving Member...");
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Plan: " + plan);
        System.out.println("Goal: " + goal);

        // add database

    }

    private void clearForm() {
        nameTextField.clear();
        emailTextField.clear();
        comboPlan.setValue(null);
        goalTextField.clear();
        nameTextField.requestFocus();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);


        if (title.equals("Error")) {
            alert.setAlertType(Alert.AlertType.ERROR);
        }

        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}