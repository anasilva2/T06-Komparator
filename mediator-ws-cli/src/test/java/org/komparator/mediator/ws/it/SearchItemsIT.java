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
public class SearchItemsIT extends BaseIT {

	// static members
	
	
	
	// one-time initialization and clean-up
	@BeforeClass
	public static void oneTimeSetUp() throws SupplierClientException, InvalidItemId_Exception, BadProductId_Exception, BadProduct_Exception {
		// clear remote service state before all tests
		mediatorClient.clear();

		// fill-in test products
		// (since getProduct is read-only the initialization below
		// can be done once for all tests in this suite)
		
		
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
			product.setPrice(7);
			product.setQuantity(1);
			supplierClients[1].createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("T3");
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
			product.setDesc("Soccer Ball");
			product.setPrice(30);
			product.setQuantity(30);
			supplierClients[1].createProduct(product);
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

	@Test(expected = InvalidText_Exception.class)
    public void searchItemNullTest() throws InvalidText_Exception {
		mediatorClient.searchItems(null);
	}
	
	@Test(expected = InvalidText_Exception.class)
    public void searchItemEmptyItemTest() throws InvalidText_Exception {
		mediatorClient.searchItems("");
	}
	
	@Test(expected = InvalidText_Exception.class)
    public void searchItemWhiteSpaceTest() throws InvalidText_Exception {
		mediatorClient.searchItems(" ");
	}
	
	@Test(expected = InvalidText_Exception.class)
    public void searchItemTabTest() throws InvalidText_Exception {
		mediatorClient.searchItems("\t");
	}
	
	@Test(expected = InvalidText_Exception.class)
    public void searchItemNewLineTest() throws InvalidText_Exception {
		mediatorClient.searchItems("\n");
	}
	
	
	// main tests
	@Test
	public void searchItemExistsTest() throws InvalidText_Exception {
		List<ItemView> productList = mediatorClient.searchItems("Baseball");
		assertEquals(1, productList.size());
		assertEquals(productList.get(0).getDesc().contains("Baseball"), true);	
	}
	
	@Test
	public void searchItemAnotherExistsTest() throws InvalidText_Exception {
		List<ItemView> productList = mediatorClient.searchItems("Basketball");
		assertEquals(3, productList.size());
		for(ItemView i: productList){
			assertEquals(i.getDesc().contains("Basketball"), true);
		}
		assertEquals(productList.get(0).getItemId().getSupplierId(), "T06_Supplier3");
		assertEquals(productList.get(1).getItemId().getSupplierId(), "T06_Supplier1");
		assertEquals(productList.get(2).getItemId().getSupplierId(), "T06_Supplier2");
	}
	
	@Test
	public void searchItemAnotherExistsTest2() throws InvalidText_Exception{
		List<ItemView> productList = mediatorClient.searchItems("ball");
		assertEquals(4, productList.size());
		for(ItemView prod : productList){
			assertEquals(prod.getDesc().contains("ball"),true);
		}
	}
	
	@Test
	public void searchItemDoesNotExistsTest1() throws InvalidText_Exception{
		List<ItemView> productList = mediatorClient.searchItems("Soccerball");
		assertEquals(0, productList.size());
	}
	
	@Test
	public void searchItemCaseSensitiveTest() throws InvalidText_Exception{
		List<ItemView> productList = mediatorClient.searchItems("Ball");
		assertEquals(1, productList.size());
		assertEquals(productList.get(0).getDesc().contains("Ball"), true);
	}
	
	@Test
	public void searchItemWithZeroQtyTest() throws InvalidText_Exception, BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception{
		supplierClients[1].buyProduct("X1",1);
		List<ItemView> productList = mediatorClient.searchItems("Basketball");
		assertEquals(3, productList.size());
		for(ItemView i: productList){
			assertEquals(i.getDesc().contains("Basketball"), true);
		}
		
	}
	
	@Test
	public void searchItemSortedByIdandPriceTest() throws InvalidText_Exception, BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception{
		List<ItemView> productList = mediatorClient.searchItems("Basketball");
		assertEquals(3, productList.size());
		
		assertEquals(productList.get(0).getItemId().getProductId(), "T3");
		assertEquals(productList.get(1).getItemId().getProductId(), "X1");
		assertEquals(productList.get(1).getPrice(), 5);
		assertEquals(productList.get(2).getItemId().getProductId(), "X1");
		assertEquals(productList.get(2).getPrice(), 7);
	}
	
	@Test
	public void searchItemWithoutNullInfoTest() throws InvalidText_Exception, BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception{
		List<ItemView> productList = mediatorClient.searchItems("Baseball");
		assertEquals(1, productList.size());
		
		assertNotNull(productList.get(0).getItemId().getProductId());
		assertNotNull(productList.get(0).getItemId().getSupplierId());
		assertNotNull(productList.get(0).getDesc());
		assertNotNull(productList.get(0).getPrice());
	}
	

}