/*
 * CondTableCountTests - JUnit test case class to test CondTableCount class
 *
 * Created - Paul J. Wagner, 1-Oct-2018
 */
package edu.uwec.cs.wagnerpj.sqltest.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uwec.cs.wagnerpj.sqltest.sqltests.CondTableCount;

public class CondTableCountTests extends AbstractTest {
	// data
	CondTableCount condTC = null;
	
	@Before
	public void setUp() throws Exception {
		condTC = new CondTableCount();
	}

	@After
	public void tearDown() throws Exception {
		condTC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query with one from
		assertEquals(condTC.sqlTest(creatureAllQuery, " >= 1"), 10);
		// valid nested query with two froms
		assertEquals(condTC.sqlTest(nestedQuery, " == 2"), 10);
		// valid query with one JOIN/ON
		assertEquals(condTC.sqlTest(joinOneQuery, " == 2"), 10);
		// valid query with one comma join and one JOIN/ON
		assertEquals(condTC.sqlTest(joinManyQuery, " == 3"), 10);
		// valid query with table joined to itself with JOIN/ON
		assertEquals(condTC.sqlTest(joinTableSelfQuery, " == 2"), 10);
		// null query has no tables
		assertEquals(condTC.sqlTest(nullQuery, " >= 1"), 0);
		// bad query with select/improper column list has no tables
		assertEquals(condTC.sqlTest(badQuery, " >= 1"), 0);
		// bad query with garbage has no froms
		assertEquals(condTC.sqlTest(garbageQuery, " >= 1"), 0);
	}

}	// end - class CondTableCountTests
