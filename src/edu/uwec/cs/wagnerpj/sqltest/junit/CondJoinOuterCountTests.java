/*
 * CondJoinCountOuterTests - JUnit test case class to test CondJoinOuterCount class
 *
 * Created - Paul J. Wagner, 20-Feb-2019
 */
package edu.uwec.cs.wagnerpj.sqltest.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uwec.cs.wagnerpj.sqltest.sqltests.CondJoinOuterCount;

public class CondJoinOuterCountTests extends AbstractTest {
	// data
	CondJoinOuterCount condJOC = null;
	
	@Before
	public void setUp() throws Exception {
		condJOC = new CondJoinOuterCount();
	}

	@After
	public void tearDown() throws Exception {
		condJOC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query with one from
		assertEquals(condJOC.sqlTest(creatureAllQuery, " == 0"), 10);
		// valid nested query with two select/froms has no joins
		assertEquals(condJOC.sqlTest(nestedQuery, " == 1"), 0);
		// valid query with one JOIN/ON, no OUTER JOIN/ON
		assertEquals(condJOC.sqlTest(joinOneQuery, " == 1"), 0);
		// valid query with one comma join and one JOIN/ON, no OUTER JOIN
		assertEquals(condJOC.sqlTest(joinManyQuery, " == 1"), 0);
		// valid query with table joined to itself with JOIN/ON but no OUTER JOIN
		assertEquals(condJOC.sqlTest(joinTableSelfQuery, " == 1"), 0);
		// valid query with one OUTER JOIN
		assertEquals(condJOC.sqlTest(joinOuterQuery, " == 1"), 10);
		// valid query with one OUTER JOIN, but uses LEFT JOIN instead of LEFT OUTER JOIN
		assertEquals(condJOC.sqlTest(joinOuter2Query, " == 1"), 10);
		// null query has no outer joins
		assertEquals(condJOC.sqlTest(nullQuery, " >= 1"), 0);
		// bad query with select/improper column list has no outer joins
		assertEquals(condJOC.sqlTest(badQuery, " == 0"), 10);
		// bad query with garbage has no outer joins
		assertEquals(condJOC.sqlTest(garbageQuery, " >= 1"), 0);
	}

}	// end - class CondTableCountTests
