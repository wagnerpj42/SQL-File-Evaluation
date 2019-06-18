/*
 * CondDescCountTests - JUnit test case class to test CondDescCount class
 *
 * Created - Paul J. Wagner, 20-Feb-2019
 */
package edu.uwec.cs.wagnerpj.sqltest.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uwec.cs.wagnerpj.sqltest.sqltests.CondDescCount;

public class CondDescCountTests extends AbstractTest {
	// data
	CondDescCount condDC = null;
	
	@Before
	public void setUp() throws Exception {
		condDC = new CondDescCount();
	}

	@After
	public void tearDown() throws Exception {
		condDC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query with one from
		assertEquals(condDC.sqlTest(creatureAllQuery, " == 0"), 10);
		// valid query with one JOIN/ON
		assertEquals(condDC.sqlTest(joinOneQuery, " == 1"), 0);
		// valid query with order by but not desc
		assertEquals(condDC.sqlTest(orderByQuery, " == 1"), 0);
		// valid query with order by and desc
		assertEquals(condDC.sqlTest(orderByDescQuery, " == 1"), 10);
		// valid query with order by and descending
		assertEquals(condDC.sqlTest(orderByDesc2Query, " == 1"), 10);
		// null query has no desc
		assertEquals(condDC.sqlTest(nullQuery, " >= 1"), 0);
		// bad query with select/improper column list has no descs
		assertEquals(condDC.sqlTest(badQuery, " == 0"), 10);
		// bad query with garbage has no descs
		assertEquals(condDC.sqlTest(garbageQuery, " >= 1"), 0);
	}

}	// end - class CondDescCountTests
