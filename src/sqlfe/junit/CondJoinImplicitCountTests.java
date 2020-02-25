/*
 * CondJoinCountImplicitTests - JUnit test case class to test CondJoinImplicitCount class
 *
 * Created - Paul J. Wagner, 19-Feb-2020
 */
package sqlfe.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sqlfe.sqltests.CondJoinImplicitCount;

public class CondJoinImplicitCountTests extends AbstractTest {
	// data
	CondJoinImplicitCount condJIC = null;
	
	@Before
	public void setUp() throws Exception {
		condJIC = new CondJoinImplicitCount();
	}

	@After
	public void tearDown() throws Exception {
		condJIC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query with three comma-separated tables has two implicit joins
		assertEquals(condJIC.sqlTest(testDAO, implicitJoinQuery, " == 2").getScore(), 10);		
		// valid query with one from has no implicit joins
		assertEquals(condJIC.sqlTest(testDAO, creatureAllQuery, " == 0").getScore(), 10);
		// valid nested query with two select/froms has no implicit joins
		assertEquals(condJIC.sqlTest(testDAO, nestedQuery, " > 0").getScore(), 0);
		// valid query with one JOIN/ON has no implicit joins
		assertEquals(condJIC.sqlTest(testDAO, joinOneQuery, " == 0").getScore(), 10);
		// valid query with one comma join and one JOIN/ON
		assertEquals(condJIC.sqlTest(testDAO, joinManyQuery, " == 1").getScore(), 10);
		// valid query with table joined to itself with JOIN/ON has no implicit joins
		assertEquals(condJIC.sqlTest(testDAO, joinTableSelfQuery, " == 0").getScore(), 10);
		// null query has no implicit joins
		assertEquals(condJIC.sqlTest(testDAO, nullQuery, " >= 1").getScore(), 0);
		// bad query with select/improper column list has no implicit joins
		assertEquals(condJIC.sqlTest(testDAO, badQuery, " == 0").getScore(), 10);
		// bad query with garbage has no implicit joins
		assertEquals(condJIC.sqlTest(testDAO, garbageQuery, " >= 1").getScore(), 0);
	}

}	// end - class CondTableCountTests
