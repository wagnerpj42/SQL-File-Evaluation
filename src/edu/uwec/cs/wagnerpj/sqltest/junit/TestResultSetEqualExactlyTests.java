/*
 * TestResultSetEqualExactlyTests - JUnit test case class to test TestResultSetEqualExactly class
 * 
 * Created - Paul J. Wagner, 01-Nov-2017
 */
package edu.uwec.cs.wagnerpj.sqltest.junit;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uwec.cs.wagnerpj.sqltest.sqltests.TestResultSetEqualExactly;

public class TestResultSetEqualExactlyTests extends AbstractTest {
	// data
	TestResultSetEqualExactly testRSE = null;
	
	@Before
	public void setup () {
		testRSE = new TestResultSetEqualExactly();
	}
	
	@Test
	public void testResultSetEqual() {
		// two instances of same valid query are equal
		assertEquals(testRSE.sqlTest(testDAO, creatureAllQuery, creatureAllQuery.toString()), 10);
		// queries of same table with different column selects are not equal
		assertEquals(testRSE.sqlTest(testDAO, creatureAllQuery, creatureOneColQuery.toString()), 0);
		// queries of same table but different case are equal
		assertEquals(testRSE.sqlTest(testDAO, creatureAllQuery, creatureAllQueryLC.toString()), 10);
		// queries of same table using * and all columns separately are equal
		assertEquals(testRSE.sqlTest(testDAO, creatureAllQuery, creatureAllQueryBC.toString()), 10);
		// valid queries of different tables are not equal
		assertEquals(testRSE.sqlTest(testDAO, creatureAllQuery, achievementAllQuery.toString()), 0);
		// two null queries are not equal
		assertEquals(testRSE.sqlTest(testDAO, nullQuery, nullQuery.toString()), 0);
		// null query and good query are not equal 
		assertEquals(testRSE.sqlTest(testDAO, nullQuery, creatureAllQuery.toString()), 0);
		// non-compiling query and valid query are not equal
		assertEquals(testRSE.sqlTest(testDAO, badQuery, creatureAllQuery.toString()), 0);
		// capitalized and non-capitalized versions of same query are equal
		assertEquals(testRSE.sqlTest(testDAO, creatureAllQuery, creatureAllQueryLC.toString()), 10);
	}
	
	@After
	public void teardown () {
		testRSE = null;
	}
	
}	// end - test case class TestResultSetEqualExactlyTests
