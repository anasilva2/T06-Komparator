package org.komparator.mediator.ws.it;

import java.io.IOException;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.komparator.mediator.ws.cli.MediatorClient;
import org.komparator.supplier.ws.cli.SupplierClient;

public class BaseIT {

	private static final String TEST_PROP_FILE = "/test.properties";
	protected static Properties testProps;

	protected static MediatorClient mediatorClient;

	protected static String supplier1URL;
	protected static String supplier1NAME;
	protected static String supplier2URL;
	protected static String supplier2NAME;
	protected static String supplier3URL;
	protected static String supplier3NAME;
	
	private static final int NR_SUPPLIERS = 3;
	protected static SupplierClient[] supplierClients = new SupplierClient[NR_SUPPLIERS];


	@BeforeClass
	public static void oneTimeSetup() throws Exception {
		testProps = new Properties();
		try {
			testProps.load(BaseIT.class.getResourceAsStream(TEST_PROP_FILE));
			System.out.println("Loaded test properties:");
			System.out.println(testProps);
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
			System.out.println(msg);
			throw e;
		}

		String uddiEnabled = testProps.getProperty("uddi.enabled");
		String uddiURL = testProps.getProperty("uddi.url");
		String wsName = testProps.getProperty("ws.name");
		String wsURL = testProps.getProperty("ws.url");
		supplier1URL = testProps.getProperty("supplier1.url");
		supplier1NAME = testProps.getProperty("supplier1.name");
		supplier2URL = testProps.getProperty("supplier2.url");
		supplier2NAME = testProps.getProperty("supplier2.name");
		supplier3URL = testProps.getProperty("supplier3.url");
		supplier3NAME = testProps.getProperty("supplier3.name");



		if ("true".equalsIgnoreCase(uddiEnabled)) {
			mediatorClient = new MediatorClient(uddiURL, wsName);
		} else {
			mediatorClient = new MediatorClient(wsURL);
		}
		
		supplierClients[0] = new SupplierClient(supplier1URL,supplier1NAME);
		supplierClients[1] = new SupplierClient(supplier2URL,supplier2NAME);
		supplierClients[2] = new SupplierClient(supplier3URL,supplier3NAME);

	}

	@AfterClass
	public static void cleanup() {
		for(int i = 0; i < NR_SUPPLIERS; i++)
			supplierClients[i] = null;
	}

}
