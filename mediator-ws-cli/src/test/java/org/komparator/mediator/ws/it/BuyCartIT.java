package org.komparator.mediator.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.komparator.mediator.ws.CartItemView;
import org.komparator.mediator.ws.CartView;
import org.komparator.mediator.ws.EmptyCart_Exception;
import org.komparator.mediator.ws.InvalidCartId_Exception;
import org.komparator.mediator.ws.InvalidCreditCard_Exception;
import org.komparator.mediator.ws.InvalidItemId_Exception;
import org.komparator.mediator.ws.InvalidQuantity_Exception;
import org.komparator.mediator.ws.ItemIdView;
import org.komparator.mediator.ws.NotEnoughItems_Exception;
import org.komparator.mediator.ws.Result;
import org.komparator.mediator.ws.ShoppingResultView;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.ProductView;
import org.komparator.supplier.ws.cli.SupplierClient;
import org.komparator.supplier.ws.cli.SupplierClientException;



/**
 * Test suite
 */
public class BuyCartIT extends BaseIT {

	// static members
	private static String ccNumber1 = "4024007102923926";
	
	
	private static ItemIdView itemId = null;
	private static ItemIdView itemId2 = null;
	private static ItemIdView itemId3 = null;
	private static ItemIdView itemId4 = null;
	
	// one-time initialization and clean-up
	@BeforeClass
	public static void oneTimeSetUp() {
		// clear remote service state before all tests
		

		// fill-in test products
		// (since getProduct is read-only the initialization below
		// can be done once for all tests in this suite)
					
		
	}

	@AfterClass
	public static void oneTimeTearDown() {
		// clear remote service state after all tests
		
	}

	// members

	// initialization and clean-up for each test
	@Before
	public void setUp() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception, BadProductId_Exception, BadProduct_Exception {
		{
			ProductView product = new ProductView();
			product.setId("X1");
			product.setDesc("Basketball");
			product.setPrice(5);
			product.setQuantity(10);
			supplierClients[0].createProduct(product);
		}
	
		{
			ProductView product = new ProductView();
			product.setId("X1");
			product.setDesc("Basketball");
			product.setPrice(20);
			product.setQuantity(10);
			supplierClients[2].createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Y2");
			product.setDesc("Baseball");
			product.setPrice(20);
			product.setQuantity(20);
			supplierClients[0].createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Z3");
			product.setDesc("Soccer ball");
			product.setPrice(30);
			product.setQuantity(30);
			supplierClients[1].createProduct(product);
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
		mediatorClient.addToCart("Cart2", itemId3, 30);
		mediatorClient.addToCart("Cart3", itemId4, 10);
		mediatorClient.addToCart("Cart3", itemId2, 5);
	}

	@After
	public void tearDown() {
		mediatorClient.clear();
		
		supplierClients[0].clear();
		supplierClients[1].clear();
		supplierClients[2].clear();
		
	}

	// tests
	// assertEquals(expected, actual);

	// bad input tests
	@Test(expected = InvalidCartId_Exception.class)
    public void buyCartCartIdNullTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
		mediatorClient.buyCart(null,ccNumber1);
	}
	
	@Test(expected = InvalidCartId_Exception.class)
    public void buyCartCartIdEmptyTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
		mediatorClient.buyCart("",ccNumber1);
	}
	
	@Test(expected = InvalidCartId_Exception.class)
    public void buyCartCartIdWhiteSpaceTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
		mediatorClient.buyCart(" ",ccNumber1);
	}
	
	@Test(expected = InvalidCartId_Exception.class)
    public void buyCartCartIdTabTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
		mediatorClient.buyCart("\t",ccNumber1);
	}
	
	@Test(expected = InvalidCartId_Exception.class)
    public void buyCartCartIdNewLineTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
		mediatorClient.buyCart("\n",ccNumber1);
	}
	
	@Test(expected = InvalidCreditCard_Exception.class)
    public void buyCartInvalidCreditCardTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
		mediatorClient.buyCart("Cart1","1234562");
	}
	
	@Test(expected = InvalidCartId_Exception.class)
    public void buyCartCartDoesNotExistTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
		mediatorClient.buyCart("Cart10",ccNumber1);
	}
	
	//main tests
	@Test
	public void buyCartSucessTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("Cart1", ccNumber1);
		assertEquals(mediatorClient.shopHistory().size(),1);
	}
	
	@Test
	public void buyCartSucess2Test() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		ShoppingResultView s = mediatorClient.buyCart("Cart1", ccNumber1);
		assertEquals(mediatorClient.shopHistory().size(),1);
		assertEquals(s.getPurchasedItems().size(),2);
		assertEquals(s.getId(), "1");
		assertEquals(s.getResult(), Result.COMPLETE);
		assertEquals(s.getTotalPrice(),280);
	}
	
	@Test
	public void buyCartSecondCartTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("Cart1", ccNumber1);
		ShoppingResultView s = mediatorClient.buyCart("Cart2", ccNumber1);
		assertEquals(mediatorClient.shopHistory().size(),2);
		assertEquals(s.getPurchasedItems().size(),1);
		assertEquals(s.getId(), "2");
	}
	
	@Test
	public void buyCartTwiceWithoutQtytoBuyTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("Cart2", ccNumber1);
		ShoppingResultView s = mediatorClient.buyCart("Cart2", ccNumber1);
		assertEquals(s.getPurchasedItems().size(),0);
		assertEquals(s.getDroppedItems().size(),1);
		assertEquals(s.getResult(),Result.EMPTY);
		assertEquals(s.getTotalPrice(),0);
		
	}
	
	@Test
	public void buyCartTwiceWithoutOneItemQtytoBuyTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("Cart3", ccNumber1);
		ShoppingResultView s = mediatorClient.buyCart("Cart3", ccNumber1);
		assertEquals(s.getPurchasedItems().size(),1);
		assertEquals(s.getDroppedItems().size(),1);
		assertEquals(s.getResult(),Result.PARTIAL);
		assertEquals(s.getTotalPrice(),100);
		
	}
	


}