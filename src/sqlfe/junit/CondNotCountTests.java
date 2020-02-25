/*
 * CondNotCountTests - JUnit test case class to test CondNotCount class
 *
 * Created - Paul J. Wagner, 8-Aug-2019
 */
package sqlfe.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sqlfe.sqltests.CondNotCount;

public class CondNotCountTests extends AbstractTest {
	// data
	CondNotCount condNC = null;
	
	@Before
	public void setUp() throws Exception {
		condNC = new CondNotCount();
	}

	@After
	public void tearDown() throws Exception {
		condNC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query with one not
		assertEquals(condNC.sqlTest(testDAO, creatureNotQuery, " == 1").getScore(), 10);		
		// valid query with one group by but no nots
		assertEquals(condNC.sqlTest(testDAO, groupByQuery, " >= 1").getScore(), 0);
		// valid nested query with two selects but no nots
		assertEquals(condNC.sqlTest(testDAO, nestedQuery, " == 0").getScore(), 10);
		// null query has no nots
		assertEquals(condNC.sqlTest(testDAO, nullQuery, " >= 1").getScore(), 0);
		// bad query with select but little else has no nots
		assertEquals(condNC.sqlTest(testDAO, badQuery, " >= 1").getScore(), 0);
		// bad query with garbage has no nots
		assertEquals(condNC.sqlTest(testDAO, garbageQuery, " >= 1").getScore(), 0);
	}

}	// end - class CondAvgCountTests

