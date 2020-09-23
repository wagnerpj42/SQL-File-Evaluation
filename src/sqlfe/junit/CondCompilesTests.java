/*
 * CondCompilesTests - JUnit test case class to test CondCompiles class
 *
 * Created - Paul J. Wagner, 1-Oct-2018
 */
package sqlfe.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sqlfe.sqltests.CondCompiles;

public class CondCompilesTests extends AbstractTest {
	// data
	CondCompiles condC = null;
	
	@Before
	public void setUp() throws Exception {
		condC = new CondCompiles();
		testDAO.connect();
	}

	@After
	public void tearDown() throws Exception {
		testDAO.disconnect();
		condC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query - compiles
		assertEquals(condC.sqlTest(testDAO, creatureAllQuery, "").getScore(), 10);
		// valid nested query - compiles
		assertEquals(condC.sqlTest(testDAO, nestedQuery, "").getScore(), 10);
		// null query - doesn't compile
		assertEquals(condC.sqlTest(testDAO, nullQuery, "").getScore(), 0);
		// bad query with select/improper table - doesn't compile
		assertEquals(condC.sqlTest(testDAO, badQuery, "").getScore(), 0);
		// bad query with garbage - doesn't compile
		assertEquals(condC.sqlTest(testDAO, garbageQuery, "").getScore(), 0);
	}

}	// end - class CondFromCountTests
