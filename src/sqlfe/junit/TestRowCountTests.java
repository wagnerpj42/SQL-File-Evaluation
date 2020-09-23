/*
 * TestRowCountTests - JUnit test case class to test TestRowCount class
 * 
 * Created - Paul J. Wagner, 18-Oct-2017
 */
package sqlfe.junit;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sqlfe.sqltests.TestRowCount;

public class TestRowCountTests extends AbstractTest {
	// data
	TestRowCount testRC = null;

	@Before
	public void setup() {
		testRC = new TestRowCount();
		testDAO.connect();
	}

	@Test
	public void testTestRowCount() {
		// queries of same table but selecting different # of columns have same row count
		assertEquals(testRC.sqlTest(testDAO, creatureAllQuery, creatureOneColQuery.toString()).getScore(), 10);
		// queries of different tables with different numbers of rows do not have same row count
		assertEquals(testRC.sqlTest(testDAO, creatureAllQuery, achievementAllQuery.toString()).getScore(), 0);
		// queries of different table with same number of rows does have same row count
		assertEquals(testRC.sqlTest(testDAO, achievementAllQuery, aspirationAllQuery.toString()).getScore(), 10);
		// queries generating 1 and 0 creatures, respectively, should generate a partial score
		assertEquals(testRC.sqlTest(testDAO, creatureOneRowQuery, creatureZeroQuery.toString()).getScore(), 5);
		// a valid query and a null query should not have the same row count
		assertEquals(testRC.sqlTest(testDAO, creatureAllQuery, nullQuery.toString()).getScore(), 0);
		// a valid table query and bad query are not equal in row count
		assertEquals(testRC.sqlTest(testDAO, creatureAllQuery, badQuery.toString()).getScore(), 0);
	}
	
	@After
	public void teardown () {
		testDAO.disconnect();
		testRC = null;
	}
	
}	// end - test case class TestRowCountTests
