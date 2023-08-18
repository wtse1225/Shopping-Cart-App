package application.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cart implements Serializable {
	private List<ItemInCart> cartItems;
	private static int nextId = 1;
    private int cartId;
    private double totalPrice;

	    public Cart() {
	        cartItems = new ArrayList<>();
	    }
	    
	    public Cart(int cartId, double totalPrice) {
	    	this.cartId = cartId;
	    	this.totalPrice = totalPrice;
	    }
	    
	    public Cart(int cartId, List<ItemInCart> cartItems) {
	        this.cartId = cartId;
	        this.cartItems = cartItems;
	    }

	    public List<ItemInCart> getCartItems() {
	        return cartItems;
	    }

	    public void setCartItems(List<ItemInCart> cartItems) {
	        this.cartItems = cartItems;
	    }

	    public void addToCart(ItemInCart item) {
	        cartItems.add(item);
	    }

	    public void removeFromCart(ItemInCart item) {
	        cartItems.remove(item);
	    }

	    public void clearCart() {
	        cartItems.clear();
	    }

	    public boolean hasCartId() {
	        return cartId != 0;
	    }

	    public void setCartId() {
	        this.cartId = nextId++;
	    }
	    
	    public static void setNextId(int nextId) {
	        Cart.nextId = nextId;
	    }
	    
	 // Static method to get the largest cartId from a list of carts
	    public static int getLargestCartId(List<Cart> carts) {
	        int largestCartId = 0;
	        for (Cart cart : carts) {
	            if (cart.getCartId() > largestCartId) {
	                largestCartId = cart.getCartId();
	            }
	        }
	        return largestCartId;
	    }

	    public int getCartId() {
	        return cartId;
	    }

	    public double getTotalPrice() {
	    	//double total = 0.0;
	    	totalPrice = 0.0;
	    	
	    	for (ItemInCart item : cartItems) {
	    		totalPrice += item.getQuantity() * item.getPrice();
	    	}
	    	
	    	//totalPrice = total;
	    	return totalPrice;
	    }

		@Override
		public String toString() {
			return "Cart [cartItems=" + cartItems + ", cartId=" + cartId + ", getTotalPrice()=" + getTotalPrice() + "]";
		}

}
