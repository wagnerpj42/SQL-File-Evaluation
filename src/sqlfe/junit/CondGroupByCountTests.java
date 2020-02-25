/*
 * CondGroupByCountTests - JUnit test case class to test CondGroupByCount class
 *
 * Created - Paul J. Wagner, 1-Oct-2018
 */
package sqlfe.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sqlfe.sqltests.CondGroupByCount;

public class CondGroupByCountTests extends AbstractTest {
	// data
	CondGroupByCount condGBC = null;
	
	@Before
	public void setUp() throws Exception {
		condGBC = new CondGroupByCount();
	}

	@After
	public void tearDown() throws Exception {
		condGBC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query with one group by
		assertEquals(condGBC.sqlTest(testDAO, groupByQuery, " == 1").getScore(), 10);
		// valid nested query with no group bys
		assertEquals(condGBC.sqlTest(testDAO, nestedQuery, " == 1").getScore(), 0);
		// null query has no group bys
		assertEquals(condGBC.sqlTest(testDAO, nullQuery, " >= 1").getScore(), 0);
		// bad query with select/improper table has no group bys
		assertEquals(condGBC.sqlTest(testDAO, badQuery, " >= 1").getScore(), 0);
		// bad query with garbage has no group bys
		assertEquals(condGBC.sqlTest(testDAO, garbageQuery, " >= 1").getScore(), 0);
	}

}	// end - class CondFromCountTests
