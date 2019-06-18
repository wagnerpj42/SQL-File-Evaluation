/*
 * CondHavingCountTests - JUnit test case class to test CondHavingCount class
 *
 * Created - Paul J. Wagner, 1-Oct-2018
 */
package edu.uwec.cs.wagnerpj.sqltest.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uwec.cs.wagnerpj.sqltest.sqltests.CondHavingCount;

public class CondHavingCountTests extends AbstractTest {
	// data
	CondHavingCount condHC = null;
	
	@Before
	public void setUp() throws Exception {
		condHC = new CondHavingCount();
	}

	@After
	public void tearDown() throws Exception {
		condHC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query with one having
		assertEquals(condHC.sqlTest(havingQuery, " >= 1"), 10);
		// valid nested query with no havings
		assertEquals(condHC.sqlTest(nestedQuery, " == 1"), 0);
		// null query has no havings
		assertEquals(condHC.sqlTest(nullQuery, " >= 1"), 0);
		// bad query with select/improper table has no havings
		assertEquals(condHC.sqlTest(badQuery, " >= 1"), 0);
		// bad query with garbage has no havings
		assertEquals(condHC.sqlTest(garbageQuery, " >= 1"), 0);
	}

}	// end - class CondHavingCountTests
