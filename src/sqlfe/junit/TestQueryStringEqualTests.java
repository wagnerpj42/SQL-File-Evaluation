/*
 * TestQueryStringEqualTests - JUnit test case class to test TestQueryStringEqual class
 * 
 * Created - Paul J. Wagner, 18-Oct-2017
 */
package sqlfe.junit;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sqlfe.sqltests.TestQueryStringEqual;

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
		assertEquals(testSE.sqlTest(testDAO, creatureAllQuery, creatureAllQuery.toString()).getScore(), 10);
		// queries of same table using * and all columns separately are not equal
		assertEquals(testSE.sqlTest(testDAO, creatureAllQuery, creatureAllQueryBC.toString()).getScore(), 0);
		// valid queries of different tables are not equal
		assertEquals(testSE.sqlTest(testDAO, creatureAllQuery, achievementAllQuery.toString()).getScore(), 0);
		// two null queries are equal
		assertEquals(testSE.sqlTest(testDAO, nullQuery, nullQuery.toString()).getScore(), 10);
		// null query and good query are not equal 
		assertEquals(testSE.sqlTest(testDAO, nullQuery, creatureAllQuery.toString()).getScore(), 0);
		// non-compiling query and valid query are not equal
		assertEquals(testSE.sqlTest(testDAO, badQuery, creatureAllQuery.toString()).getScore(), 0);
		// capitalized and non-capitalized versions of same query are equal
		assertEquals(testSE.sqlTest(testDAO, creatureAllQuery, creatureAllQueryLC.toString()).getScore(), 10);
	}
	
	@After
	public void teardown () {
		testSE = null;
	}
	
}	// end - test case class TestQueryStringEqualTests
