/*
 * TestTableListEqualsTests - JUnit test case class to test TestTableListEqual class
 * 
 * Created - Paul J. Wagner, 21-Sep-2020
 */
package sqlfe.junit;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sqlfe.sqltests.TestTableListEqual;

public class TestTableListEqualTests extends AbstractTest {
	// data
	TestTableListEqual testTLE = null;

	@Before
	public void setup() {
		testTLE = new TestTableListEqual();
		testDAO.connect();
	}

	@Test
	public void testTableListEqual() {
		// queries of same tables with same number of columns have same table list
		assertEquals(testTLE.sqlTest(testDAO, creatureAllQuery, creatureAllQuery.toString()).getScore(), 10);
		// queries of same tables with * and columns listed have same table list
		assertEquals(testTLE.sqlTest(testDAO, creatureAllQuery, creatureAllQueryBC.toString()).getScore(), 10);
		// queries of same tables with same columns but in different orders have same table list
		assertEquals(testTLE.sqlTest(testDAO, creatureAllQueryBC, creatureAllQueryBC2.toString()).getScore(), 10);
		// queries of different tables with same number of columns have different table lists
		assertEquals(testTLE.sqlTest(testDAO, creatureAllQuery, achievementAllQuery.toString()).getScore(), 0);
		// queries of same table with different column selects have same table list
		assertEquals(testTLE.sqlTest(testDAO, creatureAllQuery, creatureOneColQuery.toString()).getScore(), 10);
		// queries of different tables with different column counts have different table lists
		assertEquals(testTLE.sqlTest(testDAO, creatureAllQuery, skillAllQuery.toString()).getScore(), 0);
		// queries of two tables with nested select and join have same table lists
		assertEquals(testTLE.sqlTest(testDAO, nestedQuery, joinOneQuery.toString()).getScore(), 10);
		// valid table query and null query have different table lists
		assertEquals(testTLE.sqlTest(testDAO, creatureAllQuery, nullQuery.toString()).getScore(), 0);
		// valid table query and bad query have different table lists
		assertEquals(testTLE.sqlTest(testDAO, creatureAllQuery, badQuery.toString()).getScore(), 0);
	}
	
	@After
	public void teardown () {
		testDAO.disconnect();
		testTLE = null;
	}
	
}	// end - test case class TestTableListEqualTests
