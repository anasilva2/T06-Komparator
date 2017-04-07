package org.komparator.mediator.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.komparator.mediator.ws.InvalidItemId_Exception;
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
	
	private static SupplierClient s1 = null;
	private static SupplierClient s2 = null;
	private static SupplierClient s3 = null;
	
	// one-time initialization and clean-up
	@BeforeClass
	public static void oneTimeSetUp() throws SupplierClientException, BadProductId_Exception, BadProduct_Exception, InvalidItemId_Exception {
		// clear remote service state before all tests
		mediatorClient.clear();

		// fill-in test products
		// (since getProduct is read-only the initialization below
		// can be done once for all tests in this suite)
		s1 = new SupplierClient(supplier1URL,supplier1NAME);
		s2 = new SupplierClient(supplier2URL,supplier2NAME);
		s3 = new SupplierClient(supplier2URL,supplier3NAME);
		
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
    public void getItemsNullTest() throws InvalidItemId_Exception {
		mediatorClient.getItems(null); 
	}
	
	@Test(expected = InvalidItemId_Exception.class)
	public void getItemsEmptyTest() throws InvalidItemId_Exception {
		mediatorClient.getItems("");
	}
	
	@Test(expected = InvalidItemId_Exception.class)
	public void getItemsWhiteSpaceTest() throws InvalidItemId_Exception {
		mediatorClient.getItems(" ");
	}
	
	@Test(expected = InvalidItemId_Exception.class)
	public void getItemsTabTest() throws InvalidItemId_Exception {
		mediatorClient.getItems("\t");
	}
	
	@Test(expected = InvalidItemId_Exception.class)
	public void getItemsNewlineTest() throws InvalidItemId_Exception {
		mediatorClient.getItems("\n");
	}
	
	//main tests
	
	
	@Test
	public void getItemsExistsTest() throws InvalidItemId_Exception {
		List<ItemView> productList = mediatorClient.getItems("X1");
		assertEquals(productList.size(), 3);	
	}
	
	@Test
	public void getItemsExistsTest2() throws InvalidItemId_Exception {
		List<ItemView> productList = mediatorClient.getItems("X1");
		for(ItemView it : productList){
			assertEquals(it.getItemId().getProductId(),"X1");
		}
		assertEquals(productList.get(0).getItemId().getSupplierId(),"T06_Supplier2");
		assertEquals(productList.get(1).getItemId().getSupplierId(),"T06_Supplier1");
		assertEquals(productList.get(2).getItemId().getSupplierId(),"T06_Supplier3");
	}
	
	@Test
	public void getItemsNotExistsTest() throws InvalidItemId_Exception {
		List<ItemView> productList = mediatorClient.getItems("Z4");
		assertEquals(productList.size(),0);
	}

	@Test
	public void getItemsSortedByPriceTest() throws InvalidItemId_Exception {
		List<ItemView> productList = mediatorClient.getItems("X1");
		assertEquals(productList.get(0).getPrice(),1);
		assertEquals(productList.get(1).getPrice(),5);
		assertEquals(productList.get(2).getPrice(),20);
	}
	
	@Test
	public void getItemsLowercaseNotExistsTest() throws InvalidItemId_Exception {
		// item identifiers are case sensitive,
		// so "x1" is not the same as "X1"
		List<ItemView> productList = mediatorClient.getItems("x1"); 
		assertEquals(productList.size(),0);
	}
	
	@Test
	public void getItemsShowItemWithZeroQtyTest() throws InvalidItemId_Exception, BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		s2.buyProduct("X1",1);
		List<ItemView> productList = mediatorClient.getItems("X1"); 
		assertEquals(productList.get(0).getItemId().getSupplierId(),"T06_Supplier2");
		assertEquals(productList.get(0).getPrice(), 1);
	}
	
	@Test
	public void getItemsWithoutNullInfoTest() throws InvalidItemId_Exception, BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		List<ItemView> productList = mediatorClient.getItems("Z3"); 
		assertEquals(productList.size(), 1);
		assertNotNull(productList.get(0).getItemId().getProductId());
		assertNotNull(productList.get(0).getItemId().getSupplierId());
		assertNotNull(productList.get(0).getDesc());
		assertNotNull(productList.get(0).getPrice());
	}	
	
}
	


