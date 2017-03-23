package org.komparator.supplier.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.komparator.supplier.ws.*;

/**
 * Test suite
 */
public class BuyProductIT extends BaseIT {

	// static members

	// one-time initialization and clean-up
	@BeforeClass
	public static void oneTimeSetUp() throws BadProductId_Exception, BadProduct_Exception {
		
		client.clear();
		
		{
			ProductView product = new ProductView();
			product.setId("X1");
			product.setDesc("Basketball");
			product.setPrice(10);
			product.setQuantity(10);
			client.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Y2");
			product.setDesc("Baseball");
			product.setPrice(20);
			product.setQuantity(1);
			client.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Z3");
			product.setDesc("Soccer ball");
			product.setPrice(30);
			product.setQuantity(30);
			client.createProduct(product);
		}
		
	}

	@AfterClass
	public static void oneTimeTearDown() {
		
		client.clear();
	}

	// members

	// initialization and clean-up for each test
	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	// tests
	// assertEquals(expected, actual);

	// public String buyProduct(String productId, int quantity)
	// throws BadProductId_Exception, BadQuantity_Exception,
	// InsufficientQuantity_Exception {

	// bad input tests

	@Test(expected = BadProductId_Exception.class)
	public void buyProductNullTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct(null, 2);
	}
	
	@Test(expected = BadProductId_Exception.class)
	public void buyProductEmptyTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("", 2);
	}

	@Test(expected = BadProductId_Exception.class)
	public void buyProductWhitespaceTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct(" ", 2);
	}

	@Test(expected = BadProductId_Exception.class)
	public void buyProductTabTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("\t", 2);
	}

	@Test(expected = BadProductId_Exception.class)
	public void buyProductNewlineTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("\n", 2);
	}
	
	@Test(expected = BadProductId_Exception.class)
	public void buyProductWithWhiteSpaceIdTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("A 4", 2);
	}
	
	@Test(expected = BadQuantity_Exception.class)
	public void buyProductNegativeQuatityTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("Z3", -2);
	}
	
	@Test(expected = BadQuantity_Exception.class)
	public void buyProductZeroQuatityTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("Z3", 0);
	}
	
	@Test(expected = InsufficientQuantity_Exception.class)
	public void buyProductInsufficientQtyTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("Y2", 5);
	}
	
	@Test(expected = BadProductId_Exception.class)
	public void buyProductDoesNotExist() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception{
		client.buyProduct("A5", 2);
	}
	
	@Test(expected = InsufficientQuantity_Exception.class)
	public void buyProductDoesNotHaveQuantityExist() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception{
		client.buyProduct("X1", 10);
		client.buyProduct("X1", 5);
	}
	

	// main tests

	@Test
	public void buyProductSucessTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception{
		String id = client.buyProduct("X1",1);
		assertEquals(id,"1");
		assertEquals(client.getProduct("X1").getQuantity(), 9);
	}
	
	@Test
	public void buyProductTwiceSucessTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception{
		client.buyProduct("X1",1);
		String id = client.buyProduct("X1",1);
		assertEquals(id,"3");
		assertEquals(client.getProduct("X1").getQuantity(), 7);
	}

}
