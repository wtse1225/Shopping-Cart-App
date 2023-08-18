package application.controllers;

import java.io.IOException;

import application.Main;
import application.database.JdbcDA;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;

public class LoginController {
	
	@FXML
    private TextField emailidTextField;

    @FXML
    private Button loginButton;

    @FXML
    private TextField passwordTextField;

    @FXML
    private Button registerbutton;
    
    private Main mainApp;
    
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    void loginBtn(ActionEvent event) {
    	// Set Window owner for the login window
    	Window owner = loginButton.getScene().getWindow();
    	
    	// Empty input validation
    	if (emailidTextField.getText().isEmpty()) {
    		showAlert(Alert.AlertType.ERROR, owner, "Form Error","Please enter the user email");
    	}
    	
    	// If not empty, get the value from text fields
    	String emailId = emailidTextField.getText();
    	String password = passwordTextField.getText();
    	
    	// Create a object class for accessing to the database
    	JdbcDA da = new JdbcDA();
    	boolean flag = da.validation(emailId, password);
    		
    	if(flag) {
    		showAlert(Alert.AlertType.CONFIRMATION, owner, "Login Successfull", "Login Succeed. Please proceed to the shopping page");
    		
    		// if login is successful, call the main interface
    		mainApp.initRootLayout();
    		mainApp.showMainView();
    	} else {
    		showAlert(Alert.AlertType.ERROR, owner, "Form Error","Incorrect Email ID or Password");
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

	@FXML
    void registerBtn(ActionEvent event) throws IOException {
		// create an instance and load the fxml to it 
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/views/Register.fxml"));
		Parent regLoad = loader.load(); // load the fxml to the Parent class
		
		Stage st = new Stage(); //create stage for the new window
		st.setTitle("Registration Page");
		Scene sc = new Scene(regLoad); // create a container to hold the UI
		st.setScene(sc); // set the scene content into the Stage
		st.show(); // display the UI
	}
}
