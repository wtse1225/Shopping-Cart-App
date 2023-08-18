package application.models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Model {
	// Create an observableList to hold items
	private ObservableList<Item> obItemList;

	// A constructor that initializes by oberservableArrayList
	public Model() {
		obItemList = FXCollections.observableArrayList();
	}

	public ObservableList<Item> getObItemList() {
        return obItemList;
    }

	// A stream loader that use the "BufferedReader" to read csv
	public void loadData() {
		String str;

		try {
			BufferedReader br = new BufferedReader(new FileReader("src/resources/ItemsMaster.csv"));

			while ((str = br.readLine()) != null) {
				// split the line by ,
				String[] itemDetail = str.split(",");

				String itemName = itemDetail[0];
				String unit = itemDetail[1];
				double quantity = Double.parseDouble(itemDetail[2]);
				double price = Double.parseDouble(itemDetail[3]);

				// Send the read data to the Item class for initialization
				Item item = new Item(itemName, unit, quantity, price);

				// Add the loaded item object back to the Item Observable List
				obItemList.add(item);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
