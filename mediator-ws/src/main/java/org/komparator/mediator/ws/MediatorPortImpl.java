package org.komparator.mediator.ws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.jws.WebService;

import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadText_Exception;
import org.komparator.supplier.ws.ProductView;
import org.komparator.supplier.ws.cli.SupplierClient;
import org.komparator.supplier.ws.cli.SupplierClientException;

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

	// end point manager
	private MediatorEndpointManager endpointManager;

	public MediatorPortImpl(MediatorEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
	}

	@Override
	public void clear() {
		
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
			Collection<UDDIRecord> uddi = uddiLookup();
			for(UDDIRecord u : uddi){
				SupplierClient sClient = new SupplierClient(u.getUrl());
				
				if(sClient.getProduct(productId) != null){
					ItemIdView itemIdView = new ItemIdView();
					itemIdView.setProductId(productId);
					itemIdView.setSupplierId(u.getOrgName());
					
					String desc = sClient.getProduct(productId).getDesc();
					int price = sClient.getProduct(productId).getPrice();
					
					ItemView itemV = newItemView(itemIdView,desc,price);
					itemsList.add(itemV);
				}	
			}
		} catch (UDDINamingException e) {
			e.getMessage();
		} catch (SupplierClientException e) {
			e.getMessage();
		} catch (BadProductId_Exception e) {
			throwInvalidItemId("Invalid Item Identifier");
		}
		
		itemsList.sort(comparatorPrices);

		return itemsList;
	}

	@Override
	public List<CartView> listCarts() {
		// TODO Auto-generated method stub
		return null;
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
			Collection<UDDIRecord> items = uddiLookup();
			for(UDDIRecord u : items){
				SupplierClient supplier = new SupplierClient(u.getUrl());
				List<ProductView> productsList = supplier.searchProducts(descText);
				if(!productsList.isEmpty()){
					for(ProductView p : productsList){
						ItemIdView itemIdView = new ItemIdView();
						itemIdView.setProductId(p.getId());
						itemIdView.setSupplierId(u.getOrgName());
						
						String desc = descText;
						int price = p.getPrice();
						
						ItemView item = newItemView(itemIdView,desc,price);
						itemsList.add(item);
					}
				}
			}
		} catch (UDDINamingException e) {
			e.getMessage();
		} catch (SupplierClientException e) {
			e.getMessage();
		} catch (BadText_Exception e) {
			e.getMessage();
		}
		itemsList.sort(comparatorID);
		return itemsList;
	}

	@Override
	public ShoppingResultView buyCart(String cartId, String creditCardNr)
			throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addToCart(String cartId, ItemIdView itemId, int itemQty) throws InvalidCartId_Exception,
			InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String ping(String arg0) {
		
		StringBuilder str = new StringBuilder();
		try {
			Collection<UDDIRecord> uddi = uddiLookup();
			for(UDDIRecord u : uddi){
				SupplierClient sClient = new SupplierClient(u.getUrl());
				str.append(sClient.ping(u.getOrgName())+ "\n");
			}
		} catch (UDDINamingException e) {
			e.getMessage();
		} catch (SupplierClientException e) {
			e.getMessage();
		}
		return str.toString();
	}

	@Override
	public List<ShoppingResultView> shopHistory() {
		// TODO Auto-generated method stub
		return null;
	}

	   
	// Auxiliary operations --------------------------------------------------	
	
	private Comparator<ItemView> comparatorPrices = new Comparator<ItemView>(){

		@Override
		public int compare(ItemView i1, ItemView i2) {
			return i1.getPrice() < i2.getPrice() ? -1 :
				(i1.getPrice() > i2.getPrice()) ? 1 : 0;
		}
		
	};
	
	private Comparator<ItemView> comparatorID = new Comparator<ItemView>(){

		@Override
		public int compare(ItemView i1, ItemView i2) {
			return i1.getItemId().getProductId().compareTo(i2.getItemId().getProductId());
		}
		
	};
	
	private Collection<UDDIRecord> uddiLookup() throws UDDINamingException{
		UDDINaming uddi = endpointManager.getUddiNaming();
		return uddi.listRecords("T06_Supplier%");	
	}
	
	// View helpers -----------------------------------------------------
	
    private ItemView newItemView(ItemIdView itemId, String desc, int price){
    	ItemView itemView = new ItemView();
    	itemView.setItemId(itemId);
    	itemView.setDesc(desc);
    	itemView.setPrice(price);
    	return itemView;
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

}
