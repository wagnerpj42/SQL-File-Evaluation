/*
 * TestResultSetEqualExactlyTests - JUnit test case class to test TestResultSetEqualExactly class
 * 
 * Created - Paul J. Wagner, 01-Nov-2017
 */
package sqlfe.junit;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sqlfe.sqltests.TestResultSetEqualExactly;

public class TestResultSetEqualExactlyTests extends AbstractTest {
	// data
	TestResultSetEqualExactly testRSE = null;
	
	@Before
	public void setup () {
		testRSE = new TestResultSetEqualExactly();
		testDAO.connect();
	}
	
	@Test
	public void testResultSetEqualExactly() {
		// two instances of same valid query are equal
		assertEquals(testRSE.sqlTest(testDAO, creatureAllQuery, creatureAllQuery.toString()).getScore(), 10);
		// queries of same table with different column selects are not equal
		assertEquals(testRSE.sqlTest(testDAO, creatureAllQuery, creatureOneColQuery.toString()).getScore(), 0);
		// queries of same table but different case are equal
		assertEquals(testRSE.sqlTest(testDAO, creatureAllQuery, creatureAllQueryLC.toString()).getScore(), 10);
		// queries of same table using * and all columns separately are equal
		assertEquals(testRSE.sqlTest(testDAO, creatureAllQuery, creatureAllQueryBC.toString()).getScore(), 10);
		// valid queries of different tables are not equal
		assertEquals(testRSE.sqlTest(testDAO, creatureAllQuery, achievementAllQuery.toString()).getScore(), 0);
		// two null queries are not equal
		assertEquals(testRSE.sqlTest(testDAO, nullQuery, nullQuery.toString()).getScore(), 0);
		// null query and good query are not equal 
		assertEquals(testRSE.sqlTest(testDAO, nullQuery, creatureAllQuery.toString()).getScore(), 0);
		// non-compiling query and valid query are not equal
		assertEquals(testRSE.sqlTest(testDAO, badQuery, creatureAllQuery.toString()).getScore(), 0);
		// capitalized and non-capitalized versions of same query are equal
		assertEquals(testRSE.sqlTest(testDAO, creatureAllQuery, creatureAllQueryLC.toString()).getScore(), 10);
	}
	
	@After
	public void teardown () {
		testDAO.disconnect();
		testRSE = null;
	}
	
}	// end - test case class TestResultSetEqualExactlyTests
