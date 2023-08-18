package application.controllers;

import java.io.IOException;

import application.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;


public class RootLayoutController {

	private Main mainApp;

	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
	}

    @FXML
    void handleNew(ActionEvent event) {
    	mainApp.newCart();
    }

    @FXML
    void handleOpen(ActionEvent event) throws IOException {
    	mainApp.showSavedCartsView();
    }

    @FXML
    void handleExit(ActionEvent event) {
    	System.exit(0);
    }
}