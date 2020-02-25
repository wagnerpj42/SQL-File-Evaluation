/*
 * CondFromCountTests - JUnit test case class to test CondFromCount class
 *
 * Created - Paul J. Wagner, 1-Oct-2018
 */
package sqlfe.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sqlfe.sqltests.CondFromCount;

public class CondFromCountTests extends AbstractTest {
	// data
	CondFromCount condFC = null;
	
	@Before
	public void setUp() throws Exception {
		condFC = new CondFromCount();
	}

	@After
	public void tearDown() throws Exception {
		condFC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query with one from
		assertEquals(condFC.sqlTest(testDAO, creatureAllQuery, " >= 1").getScore(), 10);
		// valid nested query with two froms
		assertEquals(condFC.sqlTest(testDAO, nestedQuery, " == 2").getScore(), 10);
		// valid query with from at end of line
		assertEquals(condFC.sqlTest(testDAO,  fromFormatQuery,  " == 1").getScore(), 10);
		// null query has no froms
		assertEquals(condFC.sqlTest(testDAO, nullQuery, " >= 1").getScore(), 0);
		// bad query with select/improper table has no froms
		assertEquals(condFC.sqlTest(testDAO, badQuery, " >= 1").getScore(), 0);
		// bad query with garbage has no froms
		assertEquals(condFC.sqlTest(testDAO, garbageQuery, " >= 1").getScore(), 0);
	}

}	// end - class CondFromCountTests
