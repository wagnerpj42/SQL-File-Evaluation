/*
 * CondWhereCountTests - JUnit test case class to test CondWhereCount class
 *
 * Created - Paul J. Wagner, 31-Mar-2019
 */
package sqlfe.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sqlfe.sqltests.CondWhereCount;

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
		assertEquals(condWC.sqlTest(testDAO, creatureAllQuery, " == 0").getScore(), 10);
		// valid query with one where
		assertEquals(condWC.sqlTest(testDAO, creatureZeroQuery, " == 1").getScore(), 10);
		// valid nested query with one where
		assertEquals(condWC.sqlTest(testDAO, nestedQuery, " == 1").getScore(), 10);
		// null query has no wheres
		assertEquals(condWC.sqlTest(testDAO, nullQuery, " >= 1").getScore(), 0);
		// bad query with select has no wheres
		assertEquals(condWC.sqlTest(testDAO, badQuery, " >= 1").getScore(), 0);
		// bad query with garbage has no wheres
		assertEquals(condWC.sqlTest(testDAO, garbageQuery, " >= 1").getScore(), 0);
	}

}	// end - class CondWhereCountTests
