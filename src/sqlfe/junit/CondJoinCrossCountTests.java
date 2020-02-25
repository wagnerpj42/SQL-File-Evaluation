/*
 * CondJoinCrossCountTests - JUnit test case class to test CondJoinCrossCount class
 *
 * Created - Paul J. Wagner, 21-Feb-2019
 */
package sqlfe.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sqlfe.sqltests.CondJoinCrossCount;

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
		assertEquals(condJCC.sqlTest(testDAO, creatureAllQuery, " == 0").getScore(), 10);
		// valid nested query with two select/froms has no cross joins
		assertEquals(condJCC.sqlTest(testDAO, nestedQuery, " == 1").getScore(), 0);
		// valid query with one JOIN/ON, but no cross joins
		assertEquals(condJCC.sqlTest(testDAO, joinOneQuery, " == 1").getScore(), 0);
		// valid query with one comma join and one JOIN/ON, no CROSS JOIN
		assertEquals(condJCC.sqlTest(testDAO, joinManyQuery, " == 1").getScore(), 0);
		// valid query with table joined to itself with JOIN/ON but no cross join
		assertEquals(condJCC.sqlTest(testDAO, joinTableSelfQuery, " == 1").getScore(), 0);
		// valid query with one CROSS JOIN
		assertEquals(condJCC.sqlTest(testDAO, joinCrossQuery, " == 1").getScore(), 10);
		// valid query with one cross join (lower case)
		assertEquals(condJCC.sqlTest(testDAO, joinCrossQuery, " == 1").getScore(), 10);
		// null query has no cross joins
		assertEquals(condJCC.sqlTest(testDAO, nullQuery, " >= 1").getScore(), 0);
		// bad query with select/improper column list has no cross joins
		assertEquals(condJCC.sqlTest(testDAO, badQuery, " == 0").getScore(), 10);
		// bad query with garbage has no cross joins
		assertEquals(condJCC.sqlTest(testDAO, garbageQuery, " >= 1").getScore(), 0);
	}

}	// end - class CondTableCountTests
