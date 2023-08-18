package application.controllers;

import application.Main;
import application.models.Cart;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class SavedCartsController {
	// Need a table and columns to display in modal
	@FXML
	private TableView<Cart> savedCartsTable;

	@FXML
    private TableColumn<Cart, String> cartNumColumn;

    @FXML
    private TableColumn<Cart, Double> totalPriceColumn;

    @FXML
    private Button loadBtn;

    // Declare instances
    private Main mainApp;
	private Cart selectedCart; // need a selected cart for further operation
	
	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
	}

	public Cart getSelectedCart () {
		return this.selectedCart;
	}
	
	// Populate the List view with the carts passed from main
	public void initialize(ObservableList<Cart> savedCarts) {
		savedCartsTable.setItems(savedCarts);

        // Table binding
		cartNumColumn.setCellValueFactory(data -> {
			int cartId = data.getValue().getCartId();
			String cartIdString = String.valueOf(cartId);
		    return new SimpleStringProperty(cartIdString);
	        //int rowIndex = savedCartsTable.getItems().indexOf(data.getValue());
	        //return new SimpleStringProperty(Integer.toString(rowIndex + 1));
	    });
	    totalPriceColumn.setCellValueFactory(data -> {
	    	double totalPrice = data.getValue().getTotalPrice();
	    	return new ReadOnlyObjectWrapper<>(totalPrice);
	    });
	}

	@FXML
    void loadCart(ActionEvent event) {
		selectedCart = savedCartsTable.getSelectionModel().getSelectedItem();

	    if (selectedCart != null) {
	        // Pass the selected cart to cartController through main
	        mainApp.loadCart(selectedCart);

	        // Close the modal window
	        Stage stage = (Stage) loadBtn.getScene().getWindow();
	        stage.close();
	    }
    }

	@FXML
    void deleteCart(ActionEvent event) {
		selectedCart = savedCartsTable.getSelectionModel().getSelectedItem();

		if (selectedCart != null) {
			mainApp.getSavedCarts().remove(selectedCart);
			mainApp.getCartController().removeSavedCart(selectedCart);
			mainApp.getJdbcDA().removeCart(selectedCart);
		}
    }
}