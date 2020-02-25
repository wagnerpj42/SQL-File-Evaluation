/*
 * CondSumCountTests - JUnit test case class to test CondSumCount class
 *
 * Created - Paul J. Wagner, 07-Aug-2019
 */
package sqlfe.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sqlfe.sqltests.CondSumCount;

public class CondSumCountTests extends AbstractTest {
	// data
	CondSumCount condSC = null;
	
	@Before
	public void setUp() throws Exception {
		condSC = new CondSumCount();
	}

	@After
	public void tearDown() throws Exception {
		condSC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query with one sum
		assertEquals(condSC.sqlTest(testDAO, sumQuery, " == 1").getScore(), 10);		
		// valid query with one group by but no sums
		assertEquals(condSC.sqlTest(testDAO, groupByQuery, " >= 1").getScore(), 0);
		// valid nested query with two selects but no sums
		assertEquals(condSC.sqlTest(testDAO, nestedQuery, " == 0").getScore(), 10);
		// null query has no sums
		assertEquals(condSC.sqlTest(testDAO, nullQuery, " >= 1").getScore(), 0);
		// bad query with select but little else has no sums
		assertEquals(condSC.sqlTest(testDAO, badQuery, " >= 1").getScore(), 0);
		// bad query with garbage has no sums
		assertEquals(condSC.sqlTest(testDAO, garbageQuery, " >= 1").getScore(), 0);
	}

}	// end - class CondAvgCountTests

