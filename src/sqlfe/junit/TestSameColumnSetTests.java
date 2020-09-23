/*
 * TestSameColumnSetTests - JUnit test case class to test TestSameColumnSet class
 * 
 * Created - Paul J. Wagner, 01-Nov-2017
 */
package sqlfe.junit;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sqlfe.sqltests.TestSameColumnSet;

public class TestSameColumnSetTests extends AbstractTest {
	// data
	TestSameColumnSet testSCS = null;

	@Before
	public void setup() {
		testSCS = new TestSameColumnSet();
		testDAO.connect();
	}

	@Test
	public void testSameColumnSet() {
		// queries of same tables with same number of columns are equal
		assertEquals(testSCS.sqlTest(testDAO, creatureAllQuery, creatureAllQuery.toString()).getScore(), 10);
		// queries of same tables with * and columns listed are equal
		assertEquals(testSCS.sqlTest(testDAO, creatureAllQuery, creatureAllQueryBC.toString()).getScore(), 10);
		// queries of same tables with same columns but in different orders are equal
		assertEquals(testSCS.sqlTest(testDAO, creatureAllQueryBC, creatureAllQueryBC2.toString()).getScore(), 10);
		// queries of different tables with same number of columns are not equal
		assertEquals(testSCS.sqlTest(testDAO, creatureAllQuery, achievementAllQuery.toString()).getScore(), 0);
		// queries of same table with different column selects are not equal in column count
		assertEquals(testSCS.sqlTest(testDAO, creatureAllQuery, creatureOneColQuery.toString()).getScore(), 0);
		// queries of different tables with different column counts are not equal in column count
		assertEquals(testSCS.sqlTest(testDAO, creatureAllQuery, skillAllQuery.toString()).getScore(), 0);
		// valid table query and null query are not equal in column count
		assertEquals(testSCS.sqlTest(testDAO, creatureAllQuery, nullQuery.toString()).getScore(), 0);
		// valid table query and bad query are not equal in column count
		assertEquals(testSCS.sqlTest(testDAO, creatureAllQuery, badQuery.toString()).getScore(), 0);
	}
	
	@After
	public void teardown () {
		testDAO.disconnect();
		testSCS = null;
	}
	
}	// end - test case class TestSameColumnSetTests
