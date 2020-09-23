/*
 * TestResultSetEqualContentTests - JUnit test case class to test TestResultSetEqualContent class
 * 
 * Created - Paul J. Wagner, 03-Oct-2018
 */
package sqlfe.junit;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sqlfe.sqltests.TestResultSetEqualContent;

public class TestResultSetEqualContentTests extends AbstractTest {
	// data
	TestResultSetEqualContent testRSEC = null;
	
	@Before
	public void setup () {
		testRSEC = new TestResultSetEqualContent();
		testDAO.connect();
	}
	
	@Test
	public void testResultSetEqualContents() {
		// two instances of same valid query are equal
		assertEquals(testRSEC.sqlTest(testDAO, creatureAllQuery, creatureAllQuery.toString()).getScore(), 10);
		// valid queries of same table with different column selects are partially equal
		assertEquals(testRSEC.sqlTest(testDAO, creatureAllQuery, creatureOneColQuery.toString()).getScore(), 7);
		// valid queries of same table but different case are equal
		assertEquals(testRSEC.sqlTest(testDAO, creatureAllQuery, creatureAllQueryLC.toString()).getScore(), 10);
		// valid queries of same table using * and all columns separately are equal
		assertEquals(testRSEC.sqlTest(testDAO, creatureAllQuery, creatureAllQueryBC.toString()).getScore(), 10);
		// valid queries of same table with different orderings of columns are equal
		assertEquals(testRSEC.sqlTest(testDAO, creatureAllQueryBC, creatureAllQueryBC2.toString()).getScore(), 10);
		// valid queries of different tables are not equal
		assertEquals(testRSEC.sqlTest(testDAO, creatureAllQuery, achievementAllQuery.toString()).getScore(), 0);
		// two null queries are not equal
		assertEquals(testRSEC.sqlTest(testDAO, nullQuery, nullQuery.toString()).getScore(), 0);
		// null query and good query are not equal 
		assertEquals(testRSEC.sqlTest(testDAO, nullQuery, creatureAllQuery.toString()).getScore(), 0);
		// non-compiling query and valid query are not equal
		assertEquals(testRSEC.sqlTest(testDAO, badQuery, creatureAllQuery.toString()).getScore(), 0);
		// capitalized and non-capitalized versions of same query are equal
		assertEquals(testRSEC.sqlTest(testDAO, creatureAllQuery, creatureAllQueryLC.toString()).getScore(), 10);
	}
	
	@After
	public void teardown () {
		testDAO.disconnect();
		testRSEC = null;
	}
	
}	// end - test case class TestResultSetEqualContentTests
