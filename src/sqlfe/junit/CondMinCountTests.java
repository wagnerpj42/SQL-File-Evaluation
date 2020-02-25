/*
 * CondMinCountTests - JUnit test case class to test CondMinCount class
 *
 * Created - Paul J. Wagner, 08-Aug-2019
 */
package sqlfe.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sqlfe.sqltests.CondMinCount;

public class CondMinCountTests extends AbstractTest {
	// data
	CondMinCount condMC = null;
	
	@Before
	public void setUp() throws Exception {
		condMC = new CondMinCount();
	}

	@After
	public void tearDown() throws Exception {
		condMC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query with one min
		assertEquals(condMC.sqlTest(testDAO, minQuery, " == 1").getScore(), 10);		
		// valid query displaying count with one group by but no min
		assertEquals(condMC.sqlTest(testDAO, groupByQuery, " >= 1").getScore(), 0);
		// valid nested query with two selects but no mins
		assertEquals(condMC.sqlTest(testDAO, nestedQuery, " == 0").getScore(), 10);
		// null query has no mins
		assertEquals(condMC.sqlTest(testDAO, nullQuery, " >= 1").getScore(), 0);
		// bad query with select but little else has no mins
		assertEquals(condMC.sqlTest(testDAO, badQuery, " >= 1").getScore(), 0);
		// bad query with garbage has no mins
		assertEquals(condMC.sqlTest(testDAO, garbageQuery, " >= 1").getScore(), 0);
	}

}	// end - class CondMinCountTests

