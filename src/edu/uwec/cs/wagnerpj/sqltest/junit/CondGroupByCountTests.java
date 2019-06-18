/*
 * CondGroupByCountTests - JUnit test case class to test CondGroupByCount class
 *
 * Created - Paul J. Wagner, 1-Oct-2018
 */
package edu.uwec.cs.wagnerpj.sqltest.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uwec.cs.wagnerpj.sqltest.sqltests.CondGroupByCount;

public class CondGroupByCountTests extends AbstractTest {
	// data
	CondGroupByCount condGBC = null;
	
	@Before
	public void setUp() throws Exception {
		condGBC = new CondGroupByCount();
	}

	@After
	public void tearDown() throws Exception {
		condGBC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query with one group by
		assertEquals(condGBC.sqlTest(groupByQuery, " >= 1"), 10);
		// valid nested query with no group bys
		assertEquals(condGBC.sqlTest(nestedQuery, " == 1"), 0);
		// null query has no group bys
		assertEquals(condGBC.sqlTest(nullQuery, " >= 1"), 0);
		// bad query with select/improper table has no group bys
		assertEquals(condGBC.sqlTest(badQuery, " >= 1"), 0);
		// bad query with garbage has no group bys
		assertEquals(condGBC.sqlTest(garbageQuery, " >= 1"), 0);
	}

}	// end - class CondFromCountTests
