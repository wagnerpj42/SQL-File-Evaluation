/*
 * CondJoinCountTests - JUnit test case class to test CondJoinCount class
 *
 * Created - Paul J. Wagner, 1-Oct-2018
 */
package edu.uwec.cs.wagnerpj.sqltest.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uwec.cs.wagnerpj.sqltest.sqltests.CondJoinCount;

public class CondJoinCountTests extends AbstractTest {
	// data
	CondJoinCount condJC = null;
	
	@Before
	public void setUp() throws Exception {
		condJC = new CondJoinCount();
	}

	@After
	public void tearDown() throws Exception {
		condJC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query with one from
		assertEquals(condJC.sqlTest(creatureAllQuery, " == 0"), 10);
		// valid nested query with two select/froms has no joins
		assertEquals(condJC.sqlTest(nestedQuery, " == 1"), 0);
		// valid query with one JOIN/ON
		assertEquals(condJC.sqlTest(joinOneQuery, " == 1"), 10);
		// valid query with one comma join and one JOIN/ON
		assertEquals(condJC.sqlTest(joinManyQuery, " == 2"), 10);
		// valid query with table joined to itself with JOIN/ON
		assertEquals(condJC.sqlTest(joinTableSelfQuery, " == 1"), 10);
		// null query has no joins
		assertEquals(condJC.sqlTest(nullQuery, " >= 1"), 0);
		// bad query with select/improper column list has no joins
		assertEquals(condJC.sqlTest(badQuery, " == 0"), 10);
		// bad query with garbage has no joins
		assertEquals(condJC.sqlTest(garbageQuery, " >= 1"), 0);
	}

}	// end - class CondTableCountTests
