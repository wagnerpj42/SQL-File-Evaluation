/*
 * CondMaxCountTests - JUnit test case class to test CondMaxCount class
 *
 * Created - Paul J. Wagner, 31-Mar-2019
 */
package sqlfe.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sqlfe.sqltests.CondMaxCount;

public class CondMaxCountTests extends AbstractTest {
	// data
	CondMaxCount condMC = null;
	
	@Before
	public void setUp() throws Exception {
		condMC = new CondMaxCount();
	}

	@After
	public void tearDown() throws Exception {
		condMC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query with one max
		assertEquals(condMC.sqlTest(testDAO, maxQuery, " == 1").getScore(), 10);		
		// valid query displaying count with one group by but no max
		assertEquals(condMC.sqlTest(testDAO, groupByQuery, " >= 1").getScore(), 0);
		// valid nested query with two selects but no maxs
		assertEquals(condMC.sqlTest(testDAO, nestedQuery, " == 0").getScore(), 10);
		// null query has no maxs
		assertEquals(condMC.sqlTest(testDAO, nullQuery, " >= 1").getScore(), 0);
		// bad query with select but little else has no maxs
		assertEquals(condMC.sqlTest(testDAO, badQuery, " >= 1").getScore(), 0);
		// bad query with garbage has no maxs
		assertEquals(condMC.sqlTest(testDAO, garbageQuery, " >= 1").getScore(), 0);
	}

}	// end - class CondMaxCountTests

