/*
 * CondWhereCountTests - JUnit test case class to test CondWhereCount class
 *
 * Created - Paul J. Wagner, 31-Mar-2019
 */
package edu.uwec.cs.wagnerpj.sqltest.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uwec.cs.wagnerpj.sqltest.sqltests.CondWhereCount;

public class CondWhereCountTests extends AbstractTest {
	// data
	CondWhereCount condWC = null;
	
	@Before
	public void setUp() throws Exception {
		condWC = new CondWhereCount();
	}

	@After
	public void tearDown() throws Exception {
		condWC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query with no wheres
		assertEquals(condWC.sqlTest(creatureAllQuery, " == 0"), 10);
		// valid query with one where
		assertEquals(condWC.sqlTest(creatureZeroQuery, " == 1"), 10);
		// valid nested query with one where
		assertEquals(condWC.sqlTest(nestedQuery, " == 1"), 10);
		// null query has no wheres
		assertEquals(condWC.sqlTest(nullQuery, " >= 1"), 0);
		// bad query with select has no wheres
		assertEquals(condWC.sqlTest(badQuery, " >= 1"), 0);
		// bad query with garbage has no wheres
		assertEquals(condWC.sqlTest(garbageQuery, " >= 1"), 0);
	}

}	// end - class CondWhereCountTests
