package org.komparator.mediator.ws;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.jws.WebService;

import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadQuantity_Exception;
import org.komparator.supplier.ws.BadText_Exception;
import org.komparator.supplier.ws.InsufficientQuantity_Exception;
import org.komparator.supplier.ws.ProductView;
import org.komparator.supplier.ws.cli.SupplierClient;
import org.komparator.supplier.ws.cli.SupplierClientException;

import pt.ulisboa.tecnico.sdis.ws.cli.CreditCardClient;
import pt.ulisboa.tecnico.sdis.ws.cli.CreditCardClientException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

@WebService(
		endpointInterface = "org.komparator.mediator.ws.MediatorPortType", 
		wsdlLocation = "mediator.1_0.wsdl", 
		name = "MediatorWebService", 
		portName = "MediatorPort", 
		targetNamespace = "http://ws.mediator.komparator.org/", 
		serviceName = "MediatorService"
)
public class MediatorPortImpl implements MediatorPortType{
	
	private String suppliers = "T06_Supplier%";
	private String ccURL = "http://ws.sd.rnl.tecnico.ulisboa.pt:8080/cc";
	
	private ConcurrentHashMap<String,CartView> cartList = 
			new ConcurrentHashMap<String,CartView>();
	

	// end point manager
	private MediatorEndpointManager endpointManager;

	public MediatorPortImpl(MediatorEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
	}
	
	@Override
	public void clear() {
		
		List<SupplierClient> suppliersList = getSuppliers();
		
		for(SupplierClient s : suppliersList){
			s.clear();
		}
		
		cartList.clear();
	}

	@Override
	public List<ItemView> getItems(String productId) throws InvalidItemId_Exception {
		List<ItemView> itemsList = new ArrayList<ItemView>();
		
		if(productId == null)
			throwInvalidItemId("Product identifier cannot be null!");
		productId = productId.trim();
		if(productId.length() == 0)
			throwInvalidItemId("Product identifier cannot be empty or whitespace!");
		
		try {
			List<SupplierClient> suppliersList = getSuppliers();
			for(SupplierClient sClient : suppliersList){
				
				if(sClient.getProduct(productId) != null){
					ItemIdView itemIdView = new ItemIdView();
					itemIdView.setProductId(productId);
					itemIdView.setSupplierId(sClient.getWsName());
					
					String desc = sClient.getProduct(productId).getDesc();
					int price = sClient.getProduct(productId).getPrice();
					
					ItemView itemV = newItemView(itemIdView,desc,price);
					itemsList.add(itemV);
				}	
			}
		} catch (BadProductId_Exception e) {
			throwInvalidItemId("Invalid Item Identifier");
		}
		
		itemsList.sort(comparator);

		return itemsList;
	}

	@Override
	public List<CartView> listCarts() {
		List<CartView> list = new ArrayList<CartView>();
		for(String key : cartList.keySet()){
			list.add(cartList.get(key));
		}
		return list;
	}

	@Override
	public List<ItemView> searchItems(String descText) throws InvalidText_Exception {
		List<ItemView> itemsList = new ArrayList<ItemView>();
		
		if(descText == null)
			throwInvalidText("Text Description cannot be null!");
		descText = descText.trim();
		if(descText.length() == 0)
			throwInvalidText("Text Description cannot be null!");
		
		try {
			List<SupplierClient> supplierList = getSuppliers();
			for(SupplierClient supplier : supplierList){
				
				List<ProductView> productsList = supplier.searchProducts(descText);
				if(!productsList.isEmpty()){
					for(ProductView p : productsList){
						ItemIdView itemIdView = new ItemIdView();
						itemIdView.setProductId(p.getId());
						itemIdView.setSupplierId(supplier.getWsName());
						
						String desc = descText;
						int price = p.getPrice();
						
						ItemView item = newItemView(itemIdView,desc,price);
						itemsList.add(item);
					}
				}
			}
		} catch (BadText_Exception e) {
			throwInvalidText("Invalid Text");
		}
		
		itemsList.sort(comparator);
		return itemsList;
	}

	@Override
	public ShoppingResultView buyCart(String cartId, String creditCardNr)
			throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		
		if(cartId == null)
			throwInvalidCartId("Cart Identifier cannot be null!");
		cartId = cartId.trim();
		if(cartId.length() == 0)
			throwInvalidCartId("Cart Identifier cannot be empty or whitespace!");
		
		if(creditCardNr == null)
			throwInvalidCreditCard("Credit Card cannot be null!");
		creditCardNr = creditCardNr.trim();
		if(creditCardNr.length() == 0)
			throwInvalidCreditCard("Credit Card cannot be empty or whitespace!");
		if(cartList.get(cartId).getItems().isEmpty())
			throwEmptyCart("The cart selected is empty!");
		
		boolean creditCardValidation = false;
		try {
			CreditCardClient c = new CreditCardClient(ccURL);
			creditCardValidation = c.validateNumber(creditCardNr);
		} catch (CreditCardClientException e) {
			throwInvalidCreditCard("Invalid Credit Card");
		}
		
		if(creditCardValidation){
			CartView cart = cartList.get(cartId);
			List<SupplierClient> supplierList = getSuppliers();
			for(CartItemView i : cart.getItems()){
				for(SupplierClient supplier : supplierList){
					if(i.getItem().getItemId().getSupplierId().equals(supplier.getWsName())){
						
						String itemId = i.getItem().getItemId().getProductId();
						int quantity = i.getQuantity();
						
						try {
							supplier.buyProduct(itemId, quantity);
						} catch (BadProductId_Exception | BadQuantity_Exception | InsufficientQuantity_Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
		
		return null;
	}

	@Override
	public void addToCart(String cartId, ItemIdView itemId, int itemQty) throws InvalidCartId_Exception,
			InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		
		if(cartId == null)
			throwInvalidCartId("Cart Identifier cannot be null!");
		cartId = cartId.trim();
		if(cartId.length() == 0)
			throwInvalidCartId("Cart Identifier cannot be empty or whitespace!");
		
		if(itemId == null)
			throwInvalidItemId("Item Id cannot be null");
		
		if(itemQty <= 0)
			throwInvalidQuantity("Item Quantity must be a positive number!");
		
		/** Ask the supplier of the product if there is enough quantity */
		List<SupplierClient> supplierList = getSuppliers();
		
		for(SupplierClient s : supplierList){
			if(s.getWsName().equals(itemId.getSupplierId())){
				try {
					if(s.getProduct(itemId.getProductId()).getQuantity() < itemQty)
						throwNotEnoughItems("Not Enough Items from " + s.getWsName() + " !");
				} catch (BadProductId_Exception e) {
					throwInvalidItemId("Invalid Item Identifer");
				}
			}
		}
		
		List<ItemView> itemList = getItems(itemId.getProductId());
		CartItemView cartItem = null;
		
		for(ItemView i : itemList){
			if(i.getItemId().getSupplierId().equals(itemId.getSupplierId())){
				cartItem = newCartItemView(i,itemQty);
			}
		}
		
		
		if(cartList.containsKey(cartId)){
			
			List<CartItemView> cart = cartList.get(cartId).getItems();
			
			for(CartItemView c : cart){
				String prodId = c.getItem().getItemId().getProductId();
				String prodSupplier = c.getItem().getItemId().getSupplierId();
				if(prodId.equals(itemId.getProductId()) && prodSupplier.equals(itemId.getSupplierId())){
					int quantity = c.getQuantity() + itemQty;
					c.setQuantity(quantity);
				}
			}
			
		}else{
			
			CartView cart = new CartView();
			cart.setCartId(cartId);
			cart.getItems().add(cartItem);
			cartList.put(cartId, cart);
			
		}
			
	}


	@Override
	public String ping(String arg0) {
		
		StringBuilder str = new StringBuilder();

		List<SupplierClient> supplierList = getSuppliers();
		for(SupplierClient sClient : supplierList){
			str.append(sClient.ping(sClient.getWsName())+ "\n");
		}

		return str.toString();
	}

	@Override
	public List<ShoppingResultView> shopHistory() {
		// TODO Auto-generated method stub
		return null;
	}

	   
	// Auxiliary operations --------------------------------------------------	

	private Comparator<ItemView> comparator = new Comparator<ItemView>(){

		@Override
		public int compare(ItemView i1, ItemView i2) {
			
			String s1 = i1.getItemId().getProductId();
			String s2 = i2.getItemId().getProductId();
			
			int comp = s1.compareTo(s2);
			
			if(comp != 0)
				return comp;
			else{
				Integer price1 = i1.getPrice();
				Integer price2 = i2.getPrice();
				return price1.compareTo(price2);
			}
		}
	};
	
	private List<SupplierClient> getSuppliers(){
		List<SupplierClient> supplierList = new ArrayList<SupplierClient>();
		UDDINaming uddi = endpointManager.getUddiNaming();
		try {
			for(UDDIRecord u : uddi.listRecords(suppliers)){
				SupplierClient sClient = new SupplierClient(u.getUrl(),u.getOrgName());
				supplierList.add(sClient);
			}
			
		} catch (UDDINamingException e) {
			e.getMessage();
		} catch (SupplierClientException e) {
			e.getMessage();
		}
		
		return supplierList;
	}
	
	// View helpers -----------------------------------------------------
	
    private ItemView newItemView(ItemIdView itemId, String desc, int price){
    	ItemView itemView = new ItemView();
    	itemView.setItemId(itemId);
    	itemView.setDesc(desc);
    	itemView.setPrice(price);
    	return itemView;
    }
    
    private CartItemView newCartItemView(ItemView item, int qty){
    	CartItemView cartItemView = new CartItemView();
    	cartItemView.setItem(item);
    	cartItemView.setQuantity(qty);
    	return cartItemView;
    }

    
	// Exception helpers -----------------------------------------------------

    /** Helper method to throw new InvalidItemId exception */
	private void throwInvalidItemId(final String message) throws InvalidItemId_Exception {
		InvalidItemId faultInfo = new InvalidItemId();
		faultInfo.message = message;
		throw new InvalidItemId_Exception(message, faultInfo);
	}
	
	/** Helper method to throw new InvalidItemId exception */
	private void throwInvalidText(final String message) throws InvalidText_Exception {
		InvalidText faultInfo = new InvalidText();
		faultInfo.message = message;
		throw new InvalidText_Exception(message, faultInfo);
	}
	
	/** Helper method to throw new CartId exception */
	private void throwInvalidCartId(final String message) throws InvalidCartId_Exception {
		InvalidCartId faultInfo = new InvalidCartId();
		faultInfo.message = message;
		throw new InvalidCartId_Exception(message, faultInfo);
	}
	
	/** Helper method to throw new InvalidQuantity exception */
	private void throwInvalidQuantity(final String message) throws InvalidQuantity_Exception {
		InvalidQuantity faultInfo = new InvalidQuantity();
		faultInfo.message = message;
		throw new InvalidQuantity_Exception(message, faultInfo);
	}
	
	/** Helper method to throw new NotEnoughItems exception */
	private void throwNotEnoughItems(final String message) throws NotEnoughItems_Exception {
		NotEnoughItems faultInfo = new NotEnoughItems();
		faultInfo.message = message;
		throw new NotEnoughItems_Exception(message, faultInfo);
	}
	
	/** Helper method to throw new Credit Card exception */
	private void throwInvalidCreditCard(final String message) throws InvalidCreditCard_Exception {
		InvalidCreditCard faultInfo = new InvalidCreditCard();
		faultInfo.message = message;
		throw new InvalidCreditCard_Exception(message, faultInfo);
	}
	
	/** Helper method to throw new Empty Cart exception */
	private void throwEmptyCart(final String message) throws EmptyCart_Exception {
		EmptyCart faultInfo = new EmptyCart();
		faultInfo.message = message;
		throw new EmptyCart_Exception(message, faultInfo);
	}

}
