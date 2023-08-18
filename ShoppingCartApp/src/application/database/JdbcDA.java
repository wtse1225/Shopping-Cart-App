package application.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import application.models.Cart;
import application.models.ItemInCart;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class JdbcDA {
	// Boiler plate
	private static final String DB_URL="jdbc:mysql://localhost:3306/loginandreg?useSSL=false";
	private static final String DB_USERNAME = "root";
	private static final String DB_PASSWORD = "Aa12345.";
	// CRUD strings
	private static final String INSERT_QRY = "insert into registers (full_name,email_id,password) values (?,?,?)";
	private static final String SELECT_QRY = "select * from registers where email_id = ? AND password = ?";
	private static final String EMAIL_QRY = "select * from registers where email_id = ?";
	private static final String SELECT_CART = "select * from cart";
	private static String userEmail;
	
	public void insertRecord(String name, String email, String password) {
		// PreparedStatement(INSERT) + connection to DB
		try(Connection connect = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
			PreparedStatement ps = connect.prepareStatement(INSERT_QRY)) {
			
			// Set column values
			ps.setString(1, name);
			ps.setString(2, email);
			ps.setString(3, password);
			
			// Execute commit
			int res = ps.executeUpdate();
			
			if(res > 0)
			{
				System.out.println(res + " row(s) inserted in the Registers table");
			}
			
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public boolean validation(String email, String password) {
		// PreparedStatement(QUERY) + connection to DB
		try(Connection connect = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
			PreparedStatement ps = connect.prepareStatement(SELECT_QRY)) {
			
			// Set query values
			ps.setString(1, email);
			ps.setString(2, password);
			
			// Execute query
			ResultSet rs = ps.executeQuery();
			
			// Check if there is record in the next line(not header)
			while(rs.next()) {
				userEmail = email;
				return true;
			}
			
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		// Found nothing
		return false;
	}
	
	public boolean checkEmail(String email) {
		// PreparedStatement(QUERY) + connection to DB
		try(Connection connect = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
			PreparedStatement ps = connect.prepareStatement(EMAIL_QRY)) {
			
			// Set query values
			ps.setString(1, email);
			
			// Execute query
			ResultSet rs = ps.executeQuery();
			
			// Check if there is record in the next line(not header)
			while(rs.next()) {
				return true;
			}
			
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		// Found nothing
		return false;
	}
	
	public void insertCart(Cart cart) {
		// PreparedStatement(INSERT) + connection to DB
		try(Connection connect = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
			PreparedStatement psSearch = connect.prepareStatement("select * from cart where cartId = ?");
			PreparedStatement psUpdate = connect.prepareStatement("update cart SET totalPrice = ? WHERE cartId = ?");
			PreparedStatement ps = connect.prepareStatement("insert into cart (cartId,totalPrice) values (?,?)"); 
			PreparedStatement psDeleteItems = connect.prepareStatement("DELETE FROM cartitem WHERE cartId = ?");){
			
			psSearch.setInt(1, cart.getCartId());
			ResultSet rsSearch = psSearch.executeQuery();
			
			if(rsSearch.next()) {
				psUpdate.setInt(2, cart.getCartId());
				psUpdate.setDouble(1, cart.getTotalPrice());				
				
				// Execute commit
				int res = psUpdate.executeUpdate();	
				if(res > 0)
				{
					System.out.println(res + " row(s) updated in the Cart table");
				}
			} 
			else {			
				// Set column values
				ps.setInt(1, cart.getCartId());
				ps.setDouble(2, cart.getTotalPrice());
	
				// Execute commit
				int res = ps.executeUpdate();	
				if(res > 0)
				{
					System.out.println(res + " row(s) inserted in the Cart table");
				}
			}
			
			// delete items associated with the cart before inserting the updated items
	        psDeleteItems.setInt(1, cart.getCartId());
	        psDeleteItems.executeUpdate();

	        // Insert the updated items
	        for (ItemInCart item : cart.getCartItems()) {
	            insertItem(item, cart.getCartId());
	        }

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public void removeCart(Cart cart) {
		try(Connection connect = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
			PreparedStatement psSearch = connect.prepareStatement("select * from cart where cartId = ?");
			PreparedStatement psDeleteItem = connect.prepareStatement("DELETE FROM cartItem WHERE cartId = ?");
			PreparedStatement psDeleteCart = connect.prepareStatement("DELETE FROM cart WHERE cartId = ?");) {
				
			psSearch.setInt(1, cart.getCartId());
			ResultSet rsSearch = psSearch.executeQuery();
			
			if(rsSearch.next()) {
				psDeleteItem.setInt(1, cart.getCartId());
				psDeleteCart.setInt(1, cart.getCartId());
				psDeleteItem.executeUpdate();
				int res = psDeleteCart.executeUpdate();	
				if(res > 0)	{
					System.out.println(res + " cart(s) deleted from the DB");
				}
			}
				
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public void insertItem(ItemInCart itemInCart, int cartId) {
		// PreparedStatement(INSERT) + connection to DB
	    try (Connection connect = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
	        PreparedStatement psSearch = connect.prepareStatement("SELECT * FROM cartitem WHERE cartId = ? AND cartItemId = ?");
	        PreparedStatement psUpdate = connect.prepareStatement("UPDATE cartitem SET purchaseQuantity = ?, price = ? WHERE cartId = ? AND itemName = ?");
	        PreparedStatement psInsert = connect.prepareStatement("INSERT INTO cartitem (itemName, purchaseQuantity, price, cartId) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
	    	
	    	// Search if the item exists in the cart
	        psSearch.setInt(1, cartId);
	        psSearch.setInt(2, itemInCart.getCartItemId().get());

	        ResultSet rs = psSearch.executeQuery();
	        boolean itemExists = rs.next();

	        if (itemExists) {
	            // If the item exists in the cart, update the details
	            psUpdate.setInt(1, itemInCart.getQuantity());
	            psUpdate.setDouble(2, itemInCart.getPrice());
	            psUpdate.setInt(3, cartId);
	            psUpdate.setString(4, itemInCart.getItemName().get());

	            // Execute update
	            int res = psUpdate.executeUpdate();
	            if (res > 0) {
	                System.out.println(res + " row(s) updated in the CartItem table");
	            }
	        } else {
	            // If the item doesn't exist in the cart, insert a new row
	            psInsert.setString(1, itemInCart.getItemName().get());
	            psInsert.setInt(2, itemInCart.getQuantity());
	            psInsert.setDouble(3, itemInCart.getPrice());
	            psInsert.setInt(4, cartId);

	            // Execute insert
	            int res = psInsert.executeUpdate();

	            // Get the generated cartItemId from the inserted row
	            ResultSet generatedKeys = psInsert.getGeneratedKeys();
	            if (generatedKeys.next()) {
	                int cartItemId = generatedKeys.getInt(1);
	                itemInCart.setCartItemId(cartItemId);
	                itemInCart.getCartItemId().set(cartItemId);
	                System.out.println("Inserted item with cartItemId: " + cartItemId);
	            }

	            if (res > 0) {
	                System.out.println(res + " row(s) inserted in the CartItem table");
	            }
	        }
			
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	// Load carts from the DB and save as ObservableList<Cart>
    public ObservableList<Cart> loadCartsFromDB() {
    	
        ObservableList<Cart> carts = FXCollections.observableArrayList();
        
        try(Connection connect = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    		PreparedStatement ps = connect.prepareStatement(SELECT_CART)) {
        
            ResultSet rs = ps.executeQuery();

            // Iterate through the result set and create Cart objects
            while (rs.next()) {
                int cartId = rs.getInt("cartId");
                // Get other relevant data from the result set and create CartItems if needed                
                List<ItemInCart> cartItems = loadCartItemsFromDB(cartId);

                // Create the Cart object and add it to the list
                Cart cart = new Cart(cartId, cartItems);
                carts.add(cart);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return carts;
        
    }

    // Load cart items for a specific cart from the DB to the List<ItemInCart>
    private List<ItemInCart> loadCartItemsFromDB(int cartId) {
    	
        List<ItemInCart> cartItems = new ArrayList<>();
        
        try (Connection connect = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement ps = connect.prepareStatement("SELECT * FROM cartitem WHERE cartId = ?")) {

             ps.setInt(1, cartId);
             ResultSet rs = ps.executeQuery();

             while (rs.next()) {
            	 String itemName = rs.getString("itemName");
            	 int purchaseQuantity = rs.getInt("purchaseQuantity");
            	 double price = rs.getDouble("price");
            	 int cartItemId = rs.getInt("cartItemId");
            	 
            	 ItemInCart item = new ItemInCart(itemName, purchaseQuantity, price, cartItemId);
            	 cartItems.add(item);
             }

           } catch (SQLException e) {
               e.printStackTrace();
           }

        return cartItems;
    }
    
    public String getName() {
    	String userName="";
    	try (Connection connect = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                PreparedStatement ps = connect.prepareStatement("SELECT full_name FROM registers WHERE email_id = ?")) {
    		
    		ps.setString(1, userEmail);
            ResultSet rs = ps.executeQuery();
    		
    		while (rs.next()) {
    			userName = rs.getString("full_name");
    		}    		
    		
    	} catch (SQLException e) {
            e.printStackTrace();
        }
    	
    	return userName;
    }
}
