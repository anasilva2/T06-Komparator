package org.komparator.mediator.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotEquals;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.komparator.mediator.ws.CartItemView;
import org.komparator.mediator.ws.CartView;
import org.komparator.mediator.ws.EmptyCart_Exception;
import org.komparator.mediator.ws.InvalidCartId_Exception;
import org.komparator.mediator.ws.InvalidCreditCard_Exception;
import org.komparator.mediator.ws.InvalidItemId_Exception;
import org.komparator.mediator.ws.InvalidQuantity_Exception;
import org.komparator.mediator.ws.ItemIdView;
import org.komparator.mediator.ws.ItemView;
import org.komparator.mediator.ws.NotEnoughItems_Exception;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.ProductView;
import org.komparator.supplier.ws.cli.SupplierClient;
import org.komparator.supplier.ws.cli.SupplierClientException;



/**
 * Test suite
 */
public class AddCartIT extends BaseIT {

	// static members
	private static SupplierClient s1 = null;
	private static SupplierClient s2 = null;
	private static SupplierClient s3 = null;
	
	private static ItemIdView itemId = null;
	private static ItemIdView itemId2 = null;
	private static ItemIdView itemId3 = null;
	private static ItemIdView itemId4 = null;
	
	// one-time initialization and clean-up
	@BeforeClass
	public static void oneTimeSetUp() throws SupplierClientException, BadProductId_Exception, BadProduct_Exception, InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception, EmptyCart_Exception, InvalidCreditCard_Exception {
		// clear remote service state before all tests
		mediatorClient.clear();

		// fill-in test products
		// (since getProduct is read-only the initialization below
		// can be done once for all tests in this suite)
		
		s1 = new SupplierClient(supplier1URL,supplier1NAME);
		s2 = new SupplierClient(supplier2URL,supplier2NAME);
		s3 = new SupplierClient(supplier2URL,supplier3NAME);
				
		
	}

	@AfterClass
	public static void oneTimeTearDown() {
		// clear remote service state after all tests
		mediatorClient.clear();
	}

	// members

	// initialization and clean-up for each test
	@Before
	public void setUp() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception, BadProductId_Exception, BadProduct_Exception, SupplierClientException {


		
		{
			ProductView product = new ProductView();
			product.setId("X1");
			product.setDesc("Basketball");
			product.setPrice(5);
			product.setQuantity(10);
			s1.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("X1");
			product.setDesc("Basketball");
			product.setPrice(1);
			product.setQuantity(1);
			s2.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("X1");
			product.setDesc("Basketball");
			product.setPrice(20);
			product.setQuantity(10);
			s3.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Y2");
			product.setDesc("Baseball");
			product.setPrice(20);
			product.setQuantity(20);
			s1.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Z3");
			product.setDesc("Soccer ball");
			product.setPrice(30);
			product.setQuantity(30);
			s2.createProduct(product);
		}
		

		itemId = new ItemIdView();
		itemId.setProductId("X1");
		itemId.setSupplierId("T06_Supplier3");
		
		itemId2 = new ItemIdView();
		itemId2.setProductId("Y2");
		itemId2.setSupplierId("T06_Supplier1");
		
		itemId3 = new ItemIdView();
		itemId3.setProductId("Z3");
		itemId3.setSupplierId("T06_Supplier2");
		
		itemId4 = new ItemIdView();
		itemId4.setProductId("X1");
		itemId4.setSupplierId("T06_Supplier1");
		
		
		mediatorClient.addToCart("Cart1", itemId, 4);
		mediatorClient.addToCart("Cart1", itemId2, 10);	
	}

	@After
	public void tearDown() {
		mediatorClient.clear();

	}

	// tests
	// assertEquals(expected, actual);

	// bad input tests
	@Test(expected = InvalidCartId_Exception.class)
    public void addCartCartIdNullTest() throws InvalidCartId_Exception, 
    InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		mediatorClient.addToCart(null,itemId,2);
	}
	
	@Test(expected = InvalidCartId_Exception.class)
    public void addCartCardIdEmptyTest() throws InvalidCartId_Exception, 
    InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		mediatorClient.addToCart("",itemId,2);
	}
	
	@Test(expected = InvalidCartId_Exception.class)
    public void addCartCartIdWhiteSpaceTest() throws InvalidCartId_Exception, 
    InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		mediatorClient.addToCart(" ",itemId,2);
	}
	
	@Test(expected = InvalidCartId_Exception.class)
    public void addCartCartIdTabTest() throws InvalidCartId_Exception, 
    InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		mediatorClient.addToCart("\t",itemId,2);
	}
	
	@Test(expected = InvalidCartId_Exception.class)
    public void addCartCartIdNewLineTest() throws InvalidCartId_Exception, 
    InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		mediatorClient.addToCart("\n",itemId,2);
	}
	
	@Test(expected = InvalidItemId_Exception.class)
    public void addCartItemIdViewNullTest() throws InvalidCartId_Exception, 
    InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		mediatorClient.addToCart("Cart1",null,2);
	}
	
	@Test(expected = InvalidQuantity_Exception.class)
    public void addCartZeroQuantityTest() throws InvalidCartId_Exception, 
    InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		mediatorClient.addToCart("Cart1",itemId,0);
	}
	
	@Test(expected = InvalidQuantity_Exception.class)
    public void addCartNegativeQuantityTest() throws InvalidCartId_Exception, 
    InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		mediatorClient.addToCart("Cart1",itemId,-5);
	}
	
	@Test(expected = NotEnoughItems_Exception.class)
    public void addCartNotEnoughItemsTest() throws InvalidCartId_Exception, 
    InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		mediatorClient.addToCart("Cart1",itemId,15);
	}
	
	//main tests
	
	@Test
	public void addCartWithAnExistingCartTest() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
		//Adds a new item to an existing cart
		mediatorClient.addToCart("Cart1", itemId3, 2);
		assertEquals(mediatorClient.listCarts().size(),1);
		assertEquals(mediatorClient.listCarts().get(0).getCartId(), "Cart1");
		assertEquals(mediatorClient.listCarts().get(0).getItems().size(),3);
		assertEquals(mediatorClient.listCarts().get(0).getItems().get(2)
				.getItem().getItemId().getProductId(), "Z3");
	}

	@Test
	public void addCartNewCartCreatedTest() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
		//Adds a new Cart to the CartList
		mediatorClient.addToCart("Cart2", itemId, 2);
		assertEquals(mediatorClient.listCarts().size(),2);
	}
	
	
	@Test
	public void addCartWithAnExistingItemInCartTest() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
		//Increases the quantity of an existing item in a cart
		mediatorClient.addToCart("Cart1", itemId, 6);
		assertEquals(mediatorClient.listCarts().get(0).getItems().size(),2);
		assertEquals(mediatorClient.listCarts().get(0).getItems().get(0)
				.getItem().getItemId().getProductId(), "X1");
		assertEquals(mediatorClient.listCarts().get(0).getItems().get(0)
				.getQuantity(), 10);
	}
	
	@Test
	public void addCartWithAnExistingItemIdCartTest() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
		//Adds to the the cart an item with an existing id but different suppliers
		mediatorClient.addToCart("Cart1", itemId4, 6);
		assertEquals(mediatorClient.listCarts().get(0).getItems().size(),3);
		assertNotEquals(mediatorClient.listCarts().get(0).getItems().get(0)
				.getItem().getItemId().getSupplierId(),mediatorClient.listCarts().get(0).getItems().get(2)
				.getItem().getItemId().getSupplierId());
	}

}