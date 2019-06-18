/*
 * CondJoinCrossCountTests - JUnit test case class to test CondJoinCrossCount class
 *
 * Created - Paul J. Wagner, 21-Feb-2019
 */
package edu.uwec.cs.wagnerpj.sqltest.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uwec.cs.wagnerpj.sqltest.sqltests.CondJoinCrossCount;

public class CondJoinCrossCountTests extends AbstractTest {
	// data
	CondJoinCrossCount condJCC = null;
	
	@Before
	public void setUp() throws Exception {
		condJCC = new CondJoinCrossCount();
	}

	@After
	public void tearDown() throws Exception {
		condJCC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query with no cross joins
		assertEquals(condJCC.sqlTest(creatureAllQuery, " == 0"), 10);
		// valid nested query with two select/froms has no cross joins
		assertEquals(condJCC.sqlTest(nestedQuery, " == 1"), 0);
		// valid query with one JOIN/ON, but no cross joins
		assertEquals(condJCC.sqlTest(joinOneQuery, " == 1"), 0);
		// valid query with one comma join and one JOIN/ON, no CROSS JOIN
		assertEquals(condJCC.sqlTest(joinManyQuery, " == 1"), 0);
		// valid query with table joined to itself with JOIN/ON but no cross join
		assertEquals(condJCC.sqlTest(joinTableSelfQuery, " == 1"), 0);
		// valid query with one CROSS JOIN
		assertEquals(condJCC.sqlTest(joinCrossQuery, " == 1"), 10);
		// valid query with one cross join (lower case)
		assertEquals(condJCC.sqlTest(joinCrossQuery, " == 1"), 10);
		// null query has no cross joins
		assertEquals(condJCC.sqlTest(nullQuery, " >= 1"), 0);
		// bad query with select/improper column list has no cross joins
		assertEquals(condJCC.sqlTest(badQuery, " == 0"), 10);
		// bad query with garbage has no cross joins
		assertEquals(condJCC.sqlTest(garbageQuery, " >= 1"), 0);
	}

}	// end - class CondTableCountTests
