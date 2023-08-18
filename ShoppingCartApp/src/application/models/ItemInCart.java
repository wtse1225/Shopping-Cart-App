package application.models;

import java.io.Serializable;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ItemInCart implements Serializable{
	private transient SimpleStringProperty itemName;
    private transient SimpleIntegerProperty purchaseQuantity;
    private transient SimpleDoubleProperty price;
    private SimpleIntegerProperty cartItemId;

	// default constructor
	public ItemInCart() {
		this(null, 0, 0.0);
	}

	// custom constructor (need to initialize using SimpleStringProperty)
	public ItemInCart(String itemName, int purchaseQuantity, double price) {
		this.itemName = new SimpleStringProperty(itemName);
		this.purchaseQuantity = new SimpleIntegerProperty(purchaseQuantity);
		this.price = new SimpleDoubleProperty(price);
		this.cartItemId = new SimpleIntegerProperty();
	}
	
	public ItemInCart(String itemName, int purchaseQuantity, double price, int cartItemId) {
		this.itemName = new SimpleStringProperty(itemName);
		this.purchaseQuantity = new SimpleIntegerProperty(purchaseQuantity);
		this.price = new SimpleDoubleProperty(price);
		this.cartItemId = new SimpleIntegerProperty(cartItemId);
	}

	public SimpleIntegerProperty getCartItemId() {
		return cartItemId;
	}
	
	public void setCartItemId(Integer cartItemId) {
		this.cartItemId.set(cartItemId);;
	}

	// Setters and getters
	public StringProperty getItemName() {
		return this.itemName;
	}

	public StringProperty itemNameProperty() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName.set(itemName);
	}

	public Integer getQuantity() {
		return this.purchaseQuantity.get();
	}

	public IntegerProperty quantityProperty() {
		return purchaseQuantity;
	}

	public void setQuantity(Integer quantity) {
		this.purchaseQuantity.set(quantity);
	}

	public Double getPrice() {
		return this.price.get();
	}

	public void setPrice(Double price) {
		this.price.set(price);
	}
}
