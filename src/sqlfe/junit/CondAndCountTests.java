/*
 * CondAndCountTests - JUnit test case class to test CondAndCount class
 *
 * Created - Paul J. Wagner, 3-Apr-2019
 */
package sqlfe.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sqlfe.sqltests.CondAndCount;

public class CondAndCountTests extends AbstractTest {
	// data
	CondAndCount condAC = null;
	
	@Before
	public void setUp() throws Exception {
		condAC = new CondAndCount();
	}

	@After
	public void tearDown() throws Exception {
		condAC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query with one and
		assertEquals(condAC.sqlTest(testDAO, creatureAndQuery, " == 1").getScore(), 10);		
		// valid query with one group by but no ands
		assertEquals(condAC.sqlTest(testDAO, groupByQuery, " >= 1").getScore(), 0);
		// valid nested query with two selects but no ands
		assertEquals(condAC.sqlTest(testDAO, nestedQuery, " == 0").getScore(), 10);
		// null query has no ands
		assertEquals(condAC.sqlTest(testDAO, nullQuery, " >= 1").getScore(), 0);
		// bad query with select but little else has no ands
		assertEquals(condAC.sqlTest(testDAO, badQuery, " >= 1").getScore(), 0);
		// bad query with garbage has no ands
		assertEquals(condAC.sqlTest(testDAO, garbageQuery, " >= 1").getScore(), 0);
	}

}	// end - class CondAvgCountTests

