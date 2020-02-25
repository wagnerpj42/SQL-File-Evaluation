/*
 * CondAvgCountTests - JUnit test case class to test CondAvgCount class
 *
 * Created - Paul J. Wagner, 31-Mar-2019
 */
package sqlfe.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sqlfe.sqltests.CondAvgCount;

public class CondAvgCountTests extends AbstractTest {
	// data
	CondAvgCount condAC = null;
	
	@Before
	public void setUp() throws Exception {
		condAC = new CondAvgCount();
	}

	@After
	public void tearDown() throws Exception {
		condAC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query with one avg
		assertEquals(condAC.sqlTest(testDAO, avgQuery, " == 1").getScore(), 10);		
		// valid query displaying count with one group by but no avg
		assertEquals(condAC.sqlTest(testDAO, groupByQuery, " >= 1").getScore(), 0);
		// valid nested query with two selects but no avgs
		assertEquals(condAC.sqlTest(testDAO, nestedQuery, " == 0").getScore(), 10);
		// null query has no avgs
		assertEquals(condAC.sqlTest(testDAO, nullQuery, " >= 1").getScore(), 0);
		// bad query with select but little else has no avgs
		assertEquals(condAC.sqlTest(testDAO, badQuery, " >= 1").getScore(), 0);
		// bad query with garbage has no avgs
		assertEquals(condAC.sqlTest(testDAO, garbageQuery, " >= 1").getScore(), 0);
	}

}	// end - class CondAvgCountTests

