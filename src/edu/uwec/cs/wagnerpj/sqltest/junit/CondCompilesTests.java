/*
 * CondCompilesTests - JUnit test case class to test CondCompiles class
 *
 * Created - Paul J. Wagner, 1-Oct-2018
 */
package edu.uwec.cs.wagnerpj.sqltest.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uwec.cs.wagnerpj.sqltest.sqltests.CondCompiles;

public class CondCompilesTests extends AbstractTest {
	// data
	CondCompiles condC = null;
	
	@Before
	public void setUp() throws Exception {
		condC = new CondCompiles();
	}

	@After
	public void tearDown() throws Exception {
		condC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query - compiles
		assertEquals(condC.sqlTest(testDAO, creatureAllQuery, ""), 10);
		// valid nested query - compiles
		assertEquals(condC.sqlTest(testDAO, nestedQuery, ""), 10);
		// null query - doesn't compile
		assertEquals(condC.sqlTest(testDAO, nullQuery, ""), 0);
		// bad query with select/improper table - doesn't compile
		assertEquals(condC.sqlTest(testDAO, badQuery, ""), 0);
		// bad query with garbage - doesn't compile
		assertEquals(condC.sqlTest(testDAO, garbageQuery, ""), 0);
	}

}	// end - class CondFromCountTests
