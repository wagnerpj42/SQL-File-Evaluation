/*
 * CondHavingCountTests - JUnit test case class to test CondHavingCount class
 *
 * Created - Paul J. Wagner, 1-Oct-2018
 */
package sqlfe.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sqlfe.sqltests.CondHavingCount;

public class CondHavingCountTests extends AbstractTest {
	// data
	CondHavingCount condHC = null;
	
	@Before
	public void setUp() throws Exception {
		condHC = new CondHavingCount();
	}

	@After
	public void tearDown() throws Exception {
		condHC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query with one having
		assertEquals(condHC.sqlTest(testDAO, havingQuery, " >= 1").getScore(), 10);
		// valid nested query with no havings
		assertEquals(condHC.sqlTest(testDAO, nestedQuery, " == 1").getScore(), 0);
		// null query has no havings
		assertEquals(condHC.sqlTest(testDAO, nullQuery, " >= 1").getScore(), 0);
		// bad query with select/improper table has no havings
		assertEquals(condHC.sqlTest(testDAO, badQuery, " >= 1").getScore(), 0);
		// bad query with garbage has no havings
		assertEquals(condHC.sqlTest(testDAO, garbageQuery, " >= 1").getScore(), 0);
	}

}	// end - class CondHavingCountTests
