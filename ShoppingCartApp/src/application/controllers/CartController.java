package application.controllers;

import java.sql.SQLException;
import java.util.Optional;

import application.Main;
import application.database.JdbcDA;
import application.models.Cart;
import application.models.Item;
import application.models.ItemInCart;
import application.models.Model;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

public class CartController {

    @FXML
    private ComboBox<Item> itemComboBox;

    @FXML
    private Label quantityLabel;

    @FXML
    private Label unitLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label purchaseUnitLabel;

    @FXML
    private TableView<ItemInCart> cartTable;

    @FXML
    private TableColumn<ItemInCart, String> itemNameColumn;

    @FXML
    private TableColumn<ItemInCart, Integer> unitColumn;

    @FXML
    private TableColumn<ItemInCart, Double> priceColumn;

    @FXML
    private TextArea summary;

    @FXML
    private Label totalLabel;

    @FXML
    private Slider unitSlider;
    
    @FXML
    private Button chatBox;

    // Declare instances
    private Main mainApp; // Create a mainApp instance
    private Model model;
    private Cart cart;
    private ItemInCart itemInCart; // Make the cart items global
    private ObservableList<Cart> savedCarts = FXCollections.observableArrayList(); // Create an ObservableList to store the saved carts
    
    public ObservableList<Cart> getSavedCarts() {
	    return savedCarts;
	}

    // Link with main
    public void setMainApp(Main mainApp) {
    	this.mainApp = mainApp;
    }

    // Init with main, setup combo box and slider
    public void init(Main mainApp) {
    	this.mainApp = mainApp;
    	cart = new Cart(); // Initialize a new cart class to hold the items
    	model = new Model(); // Initialize a new model class to load data
    	model.loadData(); // Load available item detail for combo boxes

    	// Get the ComboBox data loaded
    	itemComboBox.setItems(model.getObItemList());

    	// Set up an addListener to display the selected ComboBox info to the labels
        itemComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                quantityLabel.setText(String.format("%.2f", newValue.getQuantity()));
                unitLabel.setText(newValue.getUnit());
                priceLabel.setText(String.format("%.2f", newValue.getPrice()));
            } else {
                unitLabel.setText("");
            }
        });

        // Set up an addListener to display the purchase quantity
        unitSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            purchaseUnitLabel.setText(String.valueOf(newValue.intValue()));
        });
        
     // Set up the cell value factories for the table columns
        itemNameColumn.setCellValueFactory(data -> data.getValue().getItemName());
        unitColumn.setCellValueFactory(data -> data.getValue().quantityProperty().asObject());
        priceColumn.setCellValueFactory(data -> {
            ItemInCart item = data.getValue();
            Item selectedItemInCart = model.getObItemList().stream()
                    .filter(it -> it.getItemName().equals(item.getItemName().get()))
                    .findFirst()
                    .orElse(null);
            if (selectedItemInCart != null) {
                double purchasePrice = selectedItemInCart.getPrice() * item.getQuantity();
                return new SimpleDoubleProperty(purchasePrice).asObject();
            } else {
                return new SimpleDoubleProperty(0.0).asObject();
            }
        });

        updateTotal(); // refresh the total amount
        displayDetail(); // display the clicked item in the TextArea
    }

    /************* Methods for calculate and display ****************/
    // Method to calculate and update the total value
    private void updateTotal() {
        double total = 0.0;
        for (ItemInCart item : cartTable.getItems()) {
            Item selectedItem = model.getObItemList()
                    .stream()
                    .filter(it -> it.getItemName().equals(item.getItemName().get()))
                    .findFirst()
                    .orElse(null);
            if (selectedItem != null) {
                total += selectedItem.getPrice() * item.getQuantity();
            }
        }
        totalLabel.setText(String.format("%.2f", total));
    }

    // TextArea display method
    public void displayDetail() {
    	cartTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Item selectedItem = model.getObItemList().stream()
                        .filter(item -> item.getItemName().equals(newSelection.getItemName().get()))
                        .findFirst()
                        .orElse(null);

                String itemDetails = "Item: " + newSelection.getItemName().get() + "\n"
                		+ "Unit: " + selectedItem.getUnit() + "\n"
                		+ "Unit price: " + selectedItem.getPrice() + "\n"
                		+ "Quantity: " + newSelection.getQuantity() + "\n"
                		+ "Purchase price: " + String.format("%.2f", selectedItem.getPrice() * newSelection.getQuantity());
                summary.setText(itemDetails);

                // Set the top grid elements back to the added item states
                if (selectedItem != null) {
                    itemComboBox.setValue(selectedItem);
                    unitSlider.setValue(newSelection.getQuantity());
                }
            }
        });
    }

    /************* Event Handlers (buttons) ****************/
    // Method to add items to the current cart
    @FXML
    void addBtn(ActionEvent event) {
    	Item selectedItem = itemComboBox.getValue(); // Get ComboBox item details
        int quantity = (int) unitSlider.getValue();  // Get the slider value

        // Add the selected item to the cart
        if (selectedItem != null && quantity > 0) {
            itemInCart = new ItemInCart(selectedItem.getItemName(), quantity, selectedItem.getPrice());
            cartTable.getItems().add(itemInCart);
            updateTotal();
        }

        // Display added items in the table view
        itemNameColumn.setCellValueFactory(data -> data.getValue().getItemName());
        unitColumn.setCellValueFactory(data -> data.getValue().quantityProperty().asObject());
        priceColumn.setCellValueFactory(data -> {
        	ItemInCart item = data.getValue();
            Item selectedItemInCart = model.getObItemList().stream()
                    .filter(it -> it.getItemName().equals(item.getItemName().get()))
                    .findFirst()
                    .orElse(null);
            if (selectedItemInCart != null) {
                double purchasePrice = selectedItemInCart.getPrice() * item.getQuantity();
                return new SimpleDoubleProperty(purchasePrice).asObject();
            } else {
                return new SimpleDoubleProperty(0.0).asObject();
            }
        });
        cart.addToCart(itemInCart);
        cartTable.refresh();
    }

    // Allow the user to edit the quantity of the added items
    @FXML
    void editBtn(ActionEvent event) {
    	ItemInCart selectedCartItem = cartTable.getSelectionModel().getSelectedItem();

        if (selectedCartItem != null) {
            // Get the updated values from the top grid
            String editedItemName = itemComboBox.getValue().getItemName();
            int editedQuantity = (int) unitSlider.getValue();

            // Update the selected cart item with the edited values
            selectedCartItem.getItemName().set(editedItemName);
            selectedCartItem.setQuantity(editedQuantity);

            // Refresh the cartTable to update the cell values
            cartTable.refresh();

            // Refresh the TextArea with the updated information
            String itemDetails = "Item: " + selectedCartItem.getItemName().get() + "\n"
            		+ "Unit: " + itemComboBox.getValue().getUnit() + "\n"
            		+ "Unit price: " + itemComboBox.getValue().getPrice() + "\n"
                    + "Quantity: " + selectedCartItem.getQuantity() + "\n"
                    + "Purchase price: " + String.format("%.2f", selectedCartItem.getQuantity() * itemComboBox.getValue().getPrice());
            summary.setText(itemDetails);

            // Update the total amount after editing the item quantity
            updateTotal();
            itemInCart = selectedCartItem;
        }
    }

    // Remove the items in the current cart
    @FXML
    void removeBtn(ActionEvent event) {
    	ItemInCart selectedCartItem = cartTable.getSelectionModel().getSelectedItem();    	
    	int selectedIndex = cartTable.getSelectionModel().getSelectedIndex();

        if (selectedIndex >= 0) {
            cartTable.getItems().remove(selectedIndex);
            cart.removeFromCart(selectedCartItem);
            summary.setText("");

            updateTotal();
        }
    }

    // Method to save the current cart to the observableList<Cart>, and file output stream
    @FXML
    void saveCart(ActionEvent event) throws SQLException {
    	if (cart.getCartItems().isEmpty()) {
            showAlert("Error", "Empty Cart", "Cannot save an empty cart.");
            return;
        }

    	// check if the current cart is an existing cart in the observableList, load the cart if yes
    	for (Cart savedCart : savedCarts) {
            if (savedCart.getCartId() == cart.getCartId()) {
                // Update the existing cart with the current cart's items
                savedCart.setCartItems(cart.getCartItems());
                break;
            }
        }

    	// if this is to save a new cart, add the current cart to the observableList and assign cart ID
        if (!cart.hasCartId()) {
            savedCarts.add(cart); // Add the cart to the savedCarts list
            cart.setCartId(); // Assign a cart ID to the current cart
            cart.getTotalPrice();
        }

        // Save the current cart to the DB
        saveToDB();
        
        // print number of items added in the console
        int numOfItems = cart.getCartItems().size();
        System.out.println(numOfItems + " item(s) added to the cart");

        // Important: pass the processed saved cart back to main's observableList<Cart>
        mainApp.getSavedCarts().setAll(savedCarts);

    }

    @FXML
    public void newCart(ActionEvent event) {
    	int largestCartId = Cart.getLargestCartId(savedCarts);
        Cart.setNextId(largestCartId + 1);

        cart = new Cart();
        cartTable.getItems().clear();
        updateTotal();
    }

    @FXML
    void checkOut(ActionEvent event) {
		if (cart.getCartItems().size() > 0) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Check out");
			alert.setContentText("Done with shooping and ready to check out?");
			Optional<ButtonType> done = alert.showAndWait();

			if (done.get() == ButtonType.OK) {
				mainApp.getSavedCarts().remove(cart); // remove the checked out cart from main
				savedCarts.remove(cart);  //remove the checked out cart from the observableList here
				mainApp.getJdbcDA().removeCart(cart); // remove from the DB
				cartTable.getItems().clear(); // clear the checked out cart's item in the table
				summary.setText(""); // clear the summary text area
				updateTotal();
				showAlert("Success", "Cart is ready to check out", "Please proceed to the payment method");
			}
		}
    }

    /************* Helper functions ****************/

    private void saveToDB() throws SQLException {
    	if (cart.hasCartId()&& cart.getCartItems().size() > 0) {    		
    		int cartId = cart.getCartId();    		
    		// Open the gate
    		JdbcDA da = new JdbcDA();    		
    		
    		// Insert the cart data to the parent table first
    		da.insertCart(cart);
    		
			// Insert the cart items to the child table
			for (ItemInCart item : cartTable.getItems()) {
				da.insertItem(item, cartId);
			}
			showAlert("Success", "Cart saved", "Cart has been saved.");
    	}
    }

	public void loadSavedCart(Cart selectedCart) {
		// Clear the existing cart items
	    cartTable.getItems().clear();

	    // Add the items from the selected cart to the table
	    cartTable.getItems().addAll(selectedCart.getCartItems());

	    // Refresh the total and other calculations
	    updateTotal();
	    cart = selectedCart; // Update the cart object with the selected cart
	}

	public void removeSavedCart(Cart cart) {
		savedCarts.remove(cart);
		cart.clearCart();
		summary.setText("");
	}
	
	 @FXML
	    void chatBox(ActionEvent event) {
		 mainApp.showChatBox();
	    }

	// Alert window setup
		private void showAlert(String title, String header, String message) {
	        Alert alert = new Alert(AlertType.INFORMATION);
	        alert.setTitle(title);
	        alert.setHeaderText(header);
	        alert.setContentText(message);
	        alert.showAndWait();
	    }
}