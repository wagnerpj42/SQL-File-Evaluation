/*
 * TestQueryStringEqualTests - JUnit test case class to test TestQueryStringEqual class
 * 
 * Created - Paul J. Wagner, 18-Oct-2017
 */
package edu.uwec.cs.wagnerpj.sqltest.junit;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uwec.cs.wagnerpj.sqltest.sqltests.TestQueryStringEqual;

public class TestQueryStringEqualTests extends AbstractTest {
	// data
	TestQueryStringEqual testSE = null;
	
	@Before
	public void setup () {
		testSE = new TestQueryStringEqual();
	}
	
	@Test
	public void testStringEquals() {
		// two instances of same valid query are equal
		assertEquals(testSE.sqlTest(creatureAllQuery, creatureAllQuery.toString()), 10);
		// queries of same table using * and all columns separately are not equal
		assertEquals(testSE.sqlTest(creatureAllQuery, creatureAllQueryBC.toString()), 0);
		// valid queries of different tables are not equal
		assertEquals(testSE.sqlTest(creatureAllQuery, achievementAllQuery.toString()), 0);
		// two null queries are equal
		assertEquals(testSE.sqlTest(nullQuery, nullQuery.toString()), 10);
		// null query and good query are not equal 
		assertEquals(testSE.sqlTest(nullQuery, creatureAllQuery.toString()), 0);
		// non-compiling query and valid query are not equal
		assertEquals(testSE.sqlTest(badQuery, creatureAllQuery.toString()), 0);
		// capitalized and non-capitalized versions of same query are equal
		assertEquals(testSE.sqlTest(creatureAllQuery, creatureAllQueryLC.toString()), 10);
	}
	
	@After
	public void teardown () {
		testSE = null;
	}
	
}	// end - test case class TestQueryStringEqualTests
