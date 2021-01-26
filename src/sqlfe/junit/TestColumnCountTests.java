/*
 * TestColumnCountTests - JUnit test case class to test TestColumnCount class
 * 
 * Created - Paul J. Wagner, 30-Oct-2017
 */
package sqlfe.junit;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sqlfe.sqltests.TestColumnCount;

public class TestColumnCountTests extends AbstractTest {
	// data
	TestColumnCount testCC = null;

	@Before
	public void setup() {
		testCC = new TestColumnCount();
		testDAO.connect();
	}

	@Test
	public void testTestColumnCount() {
		// queries of same tables with same number of columns are equal in column count
		assertEquals(testCC.sqlTest(testDAO, creatureAllQuery, creatureAllQuery.toString()).getScore(), 10);
		// queries of different tables with same number of columns are equal in column count
		assertEquals(testCC.sqlTest(testDAO, creatureAllQuery, achievementAllQuery.toString()).getScore(), 10);
		// queries of same table with different column selects are not equal in column count
		assertEquals(testCC.sqlTest(testDAO, creatureAllQuery, creatureOneColQuery.toString()).getScore(), 0);
		// queries of different tables with different column counts are not equal in column count
		assertEquals(testCC.sqlTest(testDAO, creatureAllQuery, jobAllQuery.toString()).getScore(), 0);
		// valid table query and null query are not equal in column count
		assertEquals(testCC.sqlTest(testDAO, creatureAllQuery, nullQuery.toString()).getScore(), 0);
		// valid table query and bad query are not equal in column count
		assertEquals(testCC.sqlTest(testDAO, creatureAllQuery, badQuery.toString()).getScore(), 0);
	}
	
	@After
	public void teardown () {
		testDAO.disconnect();
		testCC = null;
	}
	
}	// end - test case class TestColumnCountTests
