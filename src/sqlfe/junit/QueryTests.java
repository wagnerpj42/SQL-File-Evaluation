/*
 * QueryTests - JUnit test case class for testing Query class
 * 
 * Created - Paul J. Wagner, 18-Oct-2017
 */
package sqlfe.junit;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sqlfe.general.Query;

public class QueryTests extends AbstractTest {
	
	@Before
	public void setup() {
		// nothing to do at this time
	}

	@Test
	public void testQuery() {
		assertNotNull(creatureAllQuery);
		assertNotNull(new Query("SELECT * FROM Creature"));
	}
	
	@After
	public void teardown () {
		// nothing to do at this time
	}
	
}	// end - test case class QueryTests
