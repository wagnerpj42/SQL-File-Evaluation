/*
 * CondDistinctCountTests - JUnit test case class to test CondDistinctCount class
 *
 * Created - Paul J. Wagner, 1-Oct-2018
 */
package edu.uwec.cs.wagnerpj.sqltest.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uwec.cs.wagnerpj.sqltest.sqltests.CondDistinctCount;

public class CondDistinctCountTests extends AbstractTest {
	// data
	CondDistinctCount condDC = null;
	
	@Before
	public void setUp() throws Exception {
		condDC = new CondDistinctCount();
	}

	@After
	public void tearDown() throws Exception {
		condDC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query with no distinct
		assertEquals(condDC.sqlTest(creatureAllQuery, " >= 1"), 0);
		// valid nested query with one distinct
		assertEquals(condDC.sqlTest(nestedQuery, " == 1"), 10);
		// null query has no distincts
		assertEquals(condDC.sqlTest(nullQuery, " >= 1"), 0);
		// bad query with select/improper table has no distincts
		assertEquals(condDC.sqlTest(badQuery, " >= 1"), 0);
		// bad query with garbage has no distincts
		assertEquals(condDC.sqlTest(garbageQuery, " >= 1"), 0);
	}

}	// end - class CondFromCountTests
