package application.controllers;

import javafx.application.Platform;
import application.database.JdbcDA;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;

public class RegistrationController {

    @FXML
    private Button cancelbutton;

    @FXML
    private TextField confirmPwTextField;

    @FXML
    private TextField emailidTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private Button submitButton;

    @FXML
    void cancelBtn(ActionEvent event) {
    	Platform.exit();
    }

    @FXML
    void submitBtn(ActionEvent event) {
    	Window owner = submitButton.getScene().getWindow();
    	
    	// check empty name
    	if(nameTextField.getText().isEmpty()) {
    		showAlert(Alert.AlertType.ERROR, owner, "Form Error", "Please enter the User Name");
    	}
    	
    	String fullName = nameTextField.getText();
    	String emailId = emailidTextField.getText();
    	String password = passwordTextField.getText();
    	String confirmPassword = confirmPwTextField.getText();
    	
    	// if password == confirmPassowrd
    	if (password.equals(confirmPassword)) {
    		JdbcDA da = new JdbcDA();
    		
    		// check if email exists in the DB
    		if(da.checkEmail(emailId)) {
    			showAlert(Alert.AlertType.ERROR, owner, "Form Error", "User has already registered");
    		}
    		
    		// Insert the new record
        	da.insertRecord(fullName, emailId, password);
        	showAlert(Alert.AlertType.INFORMATION, owner, "Success", "Account registered successfully!");
            
            // Close the registration window
        	Stage stage = (Stage) submitButton.getScene().getWindow();
        	stage.close();
        	
    	} else {
    		showAlert(Alert.AlertType.ERROR, owner, "Form Error", "Password does not match");
    	}    	
    }

    private void showAlert(AlertType error, Window owner, String title, String msg) {
    	Alert alert = new Alert(error);
    	alert.setTitle(title);
    	alert.setHeaderText(null);
    	alert.setContentText(msg);
    	alert.initOwner(owner);
    	alert.showAndWait();		
	}
}

