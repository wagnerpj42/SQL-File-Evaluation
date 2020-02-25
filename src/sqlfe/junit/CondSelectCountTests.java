/*
 * CondSelectCountTests - JUnit test case class to test CondSelectCount class
 *
 * Created - Paul J. Wagner, 1-Oct-2018
 */
package sqlfe.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sqlfe.sqltests.CondSelectCount;

public class CondSelectCountTests extends AbstractTest {
	// data
	CondSelectCount condSC = null;
	
	@Before
	public void setUp() throws Exception {
		condSC = new CondSelectCount();
	}

	@After
	public void tearDown() throws Exception {
		condSC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query with one select
		assertEquals(condSC.sqlTest(testDAO, creatureAllQuery, " >= 1").getScore(), 10);
		// valid nested query with two selects
		assertEquals(condSC.sqlTest(testDAO, nestedQuery, " == 2").getScore(), 10);
		// complex nested query has one top select and three sub-selects
		assertEquals(condSC.sqlTest(testDAO,  complexNestedQuery,  " == 4").getScore(), 10);
		// null query has no selects
		assertEquals(condSC.sqlTest(testDAO, nullQuery, " >= 1").getScore(), 0);
		// bad query with select has one select
		assertEquals(condSC.sqlTest(testDAO, badQuery, " >= 1").getScore(), 10);
		// bad query with garbage has no selects
		assertEquals(condSC.sqlTest(testDAO, garbageQuery, " >= 1").getScore(), 0);
	}

}	// end - class CondSelectCountTests
