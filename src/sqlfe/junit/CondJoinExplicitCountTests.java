/*
 * CondJoinCountExplicitTests - JUnit test case class to test CondJoinExplicitCount class
 *
 * Created - Paul J. Wagner, 20-Feb-2019
 */
package sqlfe.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sqlfe.sqltests.CondJoinExplicitCount;

public class CondJoinExplicitCountTests extends AbstractTest {
	// data
	CondJoinExplicitCount condJEC = null;
	
	@Before
	public void setUp() throws Exception {
		condJEC = new CondJoinExplicitCount();
	}

	@After
	public void tearDown() throws Exception {
		condJEC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query with one from
		assertEquals(condJEC.sqlTest(testDAO, creatureAllQuery, " == 0").getScore(), 10);
		// valid nested query with two select/froms has no joins
		assertEquals(condJEC.sqlTest(testDAO, nestedQuery, " == 1").getScore(), 0);
		// valid query with one JOIN/ON
		assertEquals(condJEC.sqlTest(testDAO, joinOneQuery, " == 1").getScore(), 10);
		// valid query with one comma join and one JOIN/ON
		assertEquals(condJEC.sqlTest(testDAO, joinManyQuery, " == 1").getScore(), 10);
		// valid query with table joined to itself with JOIN/ON
		assertEquals(condJEC.sqlTest(testDAO, joinTableSelfQuery, " == 1").getScore(), 10);
		// null query has no joins
		assertEquals(condJEC.sqlTest(testDAO, nullQuery, " >= 1").getScore(), 0);
		// bad query with select/improper column list has no joins
		assertEquals(condJEC.sqlTest(testDAO, badQuery, " == 0").getScore(), 10);
		// bad query with garbage has no joins
		assertEquals(condJEC.sqlTest(testDAO, garbageQuery, " >= 1").getScore(), 0);
	}

}	// end - class CondTableCountTests
