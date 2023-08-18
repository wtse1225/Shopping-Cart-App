package application.models;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Item {
	private SimpleStringProperty itemName;
	private StringProperty unit;
	private DoubleProperty quantity;
	private DoubleProperty price;

	// default constructor
	public Item() {
		this(null, null, null, null);
	}

	// custom constructor (need to initialize using SimpleStringProperty)
	public Item(String itemName, String unit, Double quantity, Double price) {
		this.itemName = new SimpleStringProperty(itemName);
		this.unit = new SimpleStringProperty(unit);
		this.quantity = new SimpleDoubleProperty(quantity);
		this.price = new SimpleDoubleProperty(price);
	}

	// Setters and getters
	public String getItemName() {
		return this.itemName.get();
	}

	public void setItemName(String itemName) {
		this.itemName.set(itemName);
	}

	public String getUnit() {
		return this.unit.get();
	}

	public void setUnit(String unit) {
		this.unit.set(unit);
	}

	public Double getQuantity() {
		return this.quantity.get();
	}

	public void setQuantity(Double quantity) {
		this.quantity.set(quantity);
	}

	public Double getPrice() {
		return this.price.get();
	}

	public void setPrice(Double price) {
		this.price.set(price);
	}

	@Override
	public String toString() {
		return itemName.get();
	}
}
