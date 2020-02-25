/*
 * CondOrderByCountTests - JUnit test case class to test CondOrderByCount class
 *
 * Created - Paul J. Wagner, 1-Oct-2018
 */
package sqlfe.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sqlfe.sqltests.CondOrderByCount;

public class CondOrderByCountTests extends AbstractTest {
	// data
	CondOrderByCount condOBC = null;
	
	@Before
	public void setUp() throws Exception {
		condOBC = new CondOrderByCount();
	}

	@After
	public void tearDown() throws Exception {
		condOBC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query with one group by
		assertEquals(condOBC.sqlTest(testDAO, orderByQuery, " >= 1").getScore(), 10);
		// valid nested query with no group bys
		assertEquals(condOBC.sqlTest(testDAO, nestedQuery, " == 1").getScore(), 0);
		// null query has no group bys
		assertEquals(condOBC.sqlTest(testDAO, nullQuery, " >= 1").getScore(), 0);
		// bad query with select/improper table has no group bys
		assertEquals(condOBC.sqlTest(testDAO, badQuery, " >= 1").getScore(), 0);
		// bad query with garbage has no group bys
		assertEquals(condOBC.sqlTest(testDAO, garbageQuery, " >= 1").getScore(), 0);
	}

}	// end - class CondFromCountTests
