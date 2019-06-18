/*
 * TestColumnCountTests - JUnit test case class to test TestColumnCount class
 * 
 * Created - Paul J. Wagner, 30-Oct-2017
 */
package edu.uwec.cs.wagnerpj.sqltest.junit;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uwec.cs.wagnerpj.sqltest.sqltests.TestColumnCount;

public class TestColumnCountTests extends AbstractTest {
	// data
	TestColumnCount testCC = null;

	@Before
	public void setup() {
		testCC = new TestColumnCount();
	}

	@Test
	public void testTestColumnCount() {
		// TODO: fix all test evaluations to be more precise (not just 10 or 0), 
		//        given that sqlTest sends back integer result in range 0-10.
		
		// queries of same tables with same number of columns are equal in column count
		assertEquals(testCC.sqlTest(creatureAllQuery, creatureAllQuery.toString()), 10);
		// queries of different tables with same number of columns are equal in column count
		assertEquals(testCC.sqlTest(creatureAllQuery, achievementAllQuery.toString()), 10);
		// queries of same table with different column selects are not equal in column count
		assertEquals(testCC.sqlTest(creatureAllQuery, creatureOneColQuery.toString()), 0);
		// queries of different tables with different column counts are not equal in column count
		assertEquals(testCC.sqlTest(creatureAllQuery, jobAllQuery.toString()), 0);
		// valid table query and null query are not equal in column count
		assertEquals(testCC.sqlTest(creatureAllQuery, nullQuery.toString()), 0);
		// valid table query and bad query are not equal in column count
		assertEquals(testCC.sqlTest(creatureAllQuery, badQuery.toString()), 0);
	}
	
	@After
	public void teardown () {
		testCC = null;
	}
	
}	// end - test case class TestColumnCountTests
