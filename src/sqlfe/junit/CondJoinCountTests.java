/*
 * CondJoinCountTests - JUnit test case class to test CondJoinCount class
 *
 * Created - Paul J. Wagner, 1-Oct-2018
 */
package sqlfe.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sqlfe.sqltests.CondJoinCount;

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
		assertEquals(condJC.sqlTest(testDAO, creatureAllQuery, " == 0").getScore(), 10);
		// valid nested query with two select/froms has no joins
		assertEquals(condJC.sqlTest(testDAO, nestedQuery, " == 1").getScore(), 0);
		// valid query with one JOIN/ON
		assertEquals(condJC.sqlTest(testDAO, joinOneQuery, " == 1").getScore(), 10);
		// valid query with one comma join and one JOIN/ON
		assertEquals(condJC.sqlTest(testDAO, joinManyQuery, " == 2").getScore(), 10);
		// valid query with table joined to itself with JOIN/ON
		assertEquals(condJC.sqlTest(testDAO, joinTableSelfQuery, " == 1").getScore(), 10);
		// null query has no joins
		assertEquals(condJC.sqlTest(testDAO, nullQuery, " >= 1").getScore(), 0);
		// bad query with select/improper column list has no joins
		assertEquals(condJC.sqlTest(testDAO, badQuery, " == 0").getScore(), 10);
		// bad query with garbage has no joins
		assertEquals(condJC.sqlTest(testDAO, garbageQuery, " >= 1").getScore(), 0);
	}

}	// end - class CondTableCountTests
