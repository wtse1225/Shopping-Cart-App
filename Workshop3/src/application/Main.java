/**********************************************
Workshop 5
Course: APD545 - Summer
Last Name: Tse
First Name: Wai Hing William
ID: 149 992 216
Section: NAA
This assignment represents my own work in accordance with Seneca Academic Policy.
Signature: Wai Hing William Tse
Date: 7/30/2023
**********************************************/

package application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import application.controllers.CartController;
import application.controllers.LoginController;
import application.controllers.RootLayoutController;
import application.controllers.SavedCartsController;
import application.database.JdbcDA;
import application.models.Cart;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class Main extends Application {
	//Declare instances
	private Stage primaryStage;
	private BorderPane rootLayout;
	private ObservableList<Cart> savedCarts = FXCollections.observableArrayList(); // Hold a list carts updated through UI
	private CartController cartController; // A instance that allows main for easier access from main
	private RootLayoutController rootLayoutController; // Same as above
	private LoginController loginController;
	//Client instances
	private Socket socket;
	private DataOutputStream dout;
	private DataInputStream din;
	private TextField td = new TextField();
	private TextArea ta = new TextArea();
	
	JdbcDA da = new JdbcDA();

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Shopping Cart Application");
		
		// Establish connection with the server
		try {
			socket = new Socket("localhost",8000);
			din = new DataInputStream(socket.getInputStream());			
			dout = new DataOutputStream(socket.getOutputStream());
			
			new Thread(()->run()).start();
			
		}catch(IOException e) {
			e.printStackTrace();
		}

		showLoginForm();			
	}

	public void run() {
		try {
			while(true) {
				String text = din.readUTF();
				ta.appendText(text+"\n");
			}
		}catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/*************getters/setters/helpers****************/
	public ObservableList<Cart> getSavedCarts() {
		return savedCarts;
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public CartController getCartController() {
	    return cartController;
	}

	public void loadCart(Cart selectedCart) {
		cartController.loadSavedCart(selectedCart);
	}

	public void newCart() {
		cartController.newCart(null);
	}
	
	public JdbcDA getJdbcDA() {
		return da;
	}

	/*************Windows/Layout setup****************/
	
	private void showLoginForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/views/Login.fxml"));
            BorderPane root = (BorderPane) loader.load();

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();

            loginController = loader.getController();
            loginController.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }	
	
	// The layout for the top functionalities
	public void initRootLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("views/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			Scene sc = new Scene(rootLayout, 710, 620);
			primaryStage.setScene(sc);

			rootLayoutController = loader.getController();
			rootLayoutController.setMainApp(this); // set the current app instance to connect with rootLayOutController

			primaryStage.show();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	// The main UI for item ordering
	public void showMainView() {
		try {
            FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("views/Sample.fxml"));
			BorderPane showMainView = loader.load();

            rootLayout.setCenter(showMainView); // Beware the original view is now set under the RootLayout
            cartController = loader.getController();
            cartController.init(this);

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	// The modal window that loads the saved carts from savedCartsController
	public void showSavedCartsView() throws IOException {

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("views/SavedCartsLayout.fxml"));
		AnchorPane showSavedCartsView = (AnchorPane) loader.load();

		SavedCartsController savedCartsController = loader.getController(); // Retrieve the controller instance
        savedCartsController.setMainApp(this); // set the current app instance to connect with savedCartsController
        savedCarts = da.loadCartsFromDB(); // Implement the loadCartsFromDB() method to load data from the DB and save a copy to main
        savedCartsController.initialize(savedCarts); // init the savedCartController with current saved carts

		// Create a modal window to load the saved carts
		Stage savedCartsStage = new Stage();
		savedCartsStage.setTitle("Saved Carts");
		savedCartsStage.initModality(Modality.WINDOW_MODAL); // make it a modal display
		savedCartsStage.initOwner(primaryStage); // set the owner to the primary stage

		Scene scene = new Scene(showSavedCartsView);
		savedCartsStage.setScene(scene);
        savedCartsStage.showAndWait();

        // Retrieve the selected cart from SavedCartsController
        Cart selectedCart = savedCartsController.getSelectedCart();
        if (selectedCart != null) {
            // Pass the selected cart to the main view
            loadCart(selectedCart);
        }
	}
	
	public void showChatBox() {
		// Create a modal window to load the saved carts
		Stage chatBox = new Stage();
		chatBox.setTitle("Customer Service");
		chatBox.initModality(Modality.NONE); // make it a modal display
		chatBox.initOwner(primaryStage); // set the owner to the primary stage

		// Client chat box
		ta.setWrapText(true);		
		GridPane gp = new GridPane();
		gp.add(new Label("User name: " + da.getName()), 0, 0);
		gp.add(new Label("Enter text: "), 0, 1);	
		
		gp.add(td, 1, 1);		

		BorderPane bp = new BorderPane();
		bp.setTop(gp);
		bp.setCenter(new ScrollPane(ta));
		
		Scene scene = new Scene(bp,300,300);
		chatBox.setScene(scene);				
		chatBox.show();
		
		// sending the message
		td.setOnAction(e->process());		
	}
	
	// Have to be called outside from the showChatBox()
	private void process() {
		try {
			String s = da.getName() +": "+td.getText().trim();
			
			dout.writeUTF(s);			
			td.setText("");
			ta.appendText(s + "\n");
		}catch(IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {		
		launch(args);
	}
}