package org.komparator.mediator.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.komparator.mediator.ws.InvalidItemId_Exception;
import org.komparator.mediator.ws.InvalidText_Exception;
import org.komparator.mediator.ws.ItemIdView;
import org.komparator.mediator.ws.ItemView;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.BadQuantity_Exception;
import org.komparator.supplier.ws.InsufficientQuantity_Exception;
import org.komparator.supplier.ws.ProductView;
import org.komparator.supplier.ws.cli.SupplierClient;
import org.komparator.supplier.ws.cli.SupplierClientException;


/**
 * Test suite
 */
public class GetItemsIT extends BaseIT {

	// static members
	
	// one-time initialization and clean-up
	@BeforeClass
	public static void oneTimeSetUp() throws SupplierClientException, BadProductId_Exception, BadProduct_Exception, InvalidItemId_Exception {
		// clear remote service state before all tests
		mediatorClient.clear();

		// fill-in test products
		// (since getProduct is read-only the initialization below
		// can be done once for all tests in this suite)
		SupplierClient s1 = new SupplierClient(supplier1URL,supplier1NAME);
		SupplierClient s2 = new SupplierClient(supplier2URL,supplier2NAME);
		SupplierClient s3 = new SupplierClient(supplier2URL,supplier3NAME);
		
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
		 
	}

	@AfterClass
	public static void oneTimeTearDown() {
		// clear remote service state after all tests
		mediatorClient.clear();
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

	// bad input tests

	@Test(expected = InvalidItemId_Exception.class)
    public void getItemNullTest() throws InvalidItemId_Exception {
		mediatorClient.getItems(null); 
	}
	
	@Test(expected = InvalidItemId_Exception.class)
	public void getItemEmptyTest() throws InvalidItemId_Exception {
		mediatorClient.getItems("");
	}
	
	@Test(expected = InvalidItemId_Exception.class)
	public void getItemWhiteSpaceTest() throws InvalidItemId_Exception {
		mediatorClient.getItems(" ");
	}
	
	@Test(expected = InvalidItemId_Exception.class)
	public void getItemTabTest() throws InvalidItemId_Exception {
		mediatorClient.getItems("\t");
	}
	
	@Test(expected = InvalidItemId_Exception.class)
	public void getProductNewlineTest() throws InvalidItemId_Exception {
		mediatorClient.getItems("\n");
	}
	
	//main tests
	
	@Test
	public void getItemExistsTest() throws InvalidItemId_Exception {
		List<ItemView> productList = mediatorClient.getItems("X1");
		assertNotNull(productList);	
	}
	
	@Test
	public void getItemAnotherExistsTest() throws InvalidItemId_Exception {
		List<ItemView> productList = mediatorClient.getItems("X1");
		assertEquals(productList.size(), 3);	
	}
	
	@Test
	public void getItemAnotherExistsTest2() throws InvalidItemId_Exception {
		List<ItemView> productList = mediatorClient.getItems("X1");
		for(ItemView i : productList){
			
		}	
	}
	
	@Test
	public void getItemNotExistsTest() throws InvalidItemId_Exception {
		// when item does not exist, null should be returned
		List<ItemView> product = mediatorClient.getItems("Z4");
		assertEquals(product.size(),0);
	}
	
	
	
	
}
	


