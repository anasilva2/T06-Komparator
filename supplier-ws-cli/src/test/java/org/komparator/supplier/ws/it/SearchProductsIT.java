package org.komparator.supplier.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.komparator.supplier.ws.*;

/**
 * Test suite
 */
public class SearchProductsIT extends BaseIT {

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
			product.setQuantity(20);
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
		
		{
			ProductView product = new ProductView();
			product.setId("V5");
			product.setDesc("Volley ball");
			product.setPrice(30);
			product.setQuantity(30);
			client.createProduct(product);
		}
		
		{
			ProductView product = new ProductView();
			product.setId("M5");
			product.setDesc("Mini Ball");
			product.setPrice(5);
			product.setQuantity(1);
			client.createProduct(product);
		}
		
		{
			ProductView product = new ProductView();
			product.setId("Q5");
			product.setDesc("Ball");
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

	// public List<ProductView> searchProducts(String descText) throws
	// BadText_Exception

	// bad input tests

	@Test(expected = BadText_Exception.class)
	public void searchProductNullTest() throws BadText_Exception{
		client.searchProducts(null);
	}
	
	@Test(expected = BadText_Exception.class)
	public void searchProductEmptyTest() throws BadText_Exception{
		client.searchProducts("");
	}
	
	@Test(expected = BadText_Exception.class)
	public void searchProductWhiteSpaceTest() throws BadText_Exception{
		client.searchProducts(" ");
	}
	
	@Test(expected = BadText_Exception.class)
	public void searchProductTabTest() throws BadText_Exception{
		client.searchProducts("\t");
	}
	
	@Test(expected = BadText_Exception.class)
	public void searchProductNewLineTest() throws BadText_Exception{
		client.searchProducts("\n");
	}

	
	// main tests

	@Test
	public void searchProductExistsTest() throws BadText_Exception{
		List<ProductView> listProducts = client.searchProducts("Basketball");
		assertEquals(1,listProducts.size());
		assertEquals(listProducts.get(0).getDesc().contains("Basketball"),true);
	}
	
	@Test
	public void searchProductAnotherExistsTest() throws BadText_Exception{
		List<ProductView> listProducts = client.searchProducts("ball");
		assertEquals(4,listProducts.size());
		for(ProductView prod : listProducts){
			assertEquals(prod.getDesc().contains("ball"),true);
		}
	}
	
	@Test
	public void searchProductAnotherExistsTest2() throws BadText_Exception{
		List<ProductView> listProducts = client.searchProducts("Soccer ball");
		assertEquals(1,listProducts.size());
		for(ProductView prod : listProducts){
			assertEquals(prod.getDesc().contains("Soccer ball"),true);
		}
	}
	
	@Test
	public void searchProductDoesNotExistsTest() throws BadText_Exception{
		List<ProductView> listProducts = client.searchProducts("Handball");
		assertEquals(listProducts.isEmpty(), true);
		assertEquals(0,listProducts.size());
		
	}
	
	@Test
	public void searchProductUpperCaseDoesNotExistsTest() throws BadText_Exception{
		List<ProductView> listProducts = client.searchProducts("BALL");
		assertEquals(listProducts.isEmpty(), true);
		assertEquals(0,listProducts.size());
		
	}
	
	@Test
	public void searchProductWithZeroQuantityTest() throws BadText_Exception, BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception{
		client.buyProduct("M5", 1);
		List<ProductView> listProducts = client.searchProducts("Mini Ball");
		assertEquals(listProducts.size(), 1);
		assertEquals(listProducts.get(0).getDesc(), "Mini Ball");
		
	}

}
