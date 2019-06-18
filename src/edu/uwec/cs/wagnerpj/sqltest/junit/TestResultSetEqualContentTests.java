/*
 * TestResultSetEqualContentTests - JUnit test case class to test TestResultSetEqualContent class
 * 
 * Created - Paul J. Wagner, 03-Oct-2018
 */
package edu.uwec.cs.wagnerpj.sqltest.junit;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uwec.cs.wagnerpj.sqltest.sqltests.TestResultSetEqualContent;

public class TestResultSetEqualContentTests extends AbstractTest {
	// data
	TestResultSetEqualContent testRSEC = null;
	
	@Before
	public void setup () {
		testRSEC = new TestResultSetEqualContent();
	}
	
	@Test
	public void testResultSetEqual() {
		// two instances of same valid query are equal
		assertEquals(testRSEC.sqlTest(creatureAllQuery, creatureAllQuery.toString()), 10);
		// valid queries of same table with different column selects are not equal
		assertEquals(testRSEC.sqlTest(creatureAllQuery, creatureOneColQuery.toString()), 0);
		// valid queries of same table but different case are equal
		assertEquals(testRSEC.sqlTest(creatureAllQuery, creatureAllQueryLC.toString()), 10);
		// valid queries of same table using * and all columns separately are equal
		assertEquals(testRSEC.sqlTest(creatureAllQuery, creatureAllQueryBC.toString()), 10);
		// valid queries of same table with different orderings of columns are equal
		assertEquals(testRSEC.sqlTest(creatureAllQueryBC, creatureAllQueryBC2.toString()), 10);
		// valid queries of different tables are not equal
		assertEquals(testRSEC.sqlTest(creatureAllQuery, achievementAllQuery.toString()), 0);
		// two null queries are not equal
		assertEquals(testRSEC.sqlTest(nullQuery, nullQuery.toString()), 0);
		// null query and good query are not equal 
		assertEquals(testRSEC.sqlTest(nullQuery, creatureAllQuery.toString()), 0);
		// non-compiling query and valid query are not equal
		assertEquals(testRSEC.sqlTest(badQuery, creatureAllQuery.toString()), 0);
		// capitalized and non-capitalized versions of same query are equal
		assertEquals(testRSEC.sqlTest(creatureAllQuery, creatureAllQueryLC.toString()), 10);
	}
	
	@After
	public void teardown () {
		testRSEC = null;
	}
	
}	// end - test case class TestResultSetEqualContentTests
