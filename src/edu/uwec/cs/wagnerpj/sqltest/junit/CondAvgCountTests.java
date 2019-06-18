/*
 * CondAvgCountTests - JUnit test case class to test CondAvgCount class
 *
 * Created - Paul J. Wagner, 31-Mar-2019
 */
package edu.uwec.cs.wagnerpj.sqltest.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uwec.cs.wagnerpj.sqltest.sqltests.CondAvgCount;

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
		assertEquals(condAC.sqlTest(avgQuery, " == 1"), 10);		
		// valid query displaying count with one group by but no avg
		assertEquals(condAC.sqlTest(groupByQuery, " >= 1"), 0);
		// valid nested query with two selects but no avgs
		assertEquals(condAC.sqlTest(nestedQuery, " == 0"), 10);
		// null query has no avgs
		assertEquals(condAC.sqlTest(nullQuery, " >= 1"), 0);
		// bad query with select but little else has no avgs
		assertEquals(condAC.sqlTest(badQuery, " >= 1"), 0);
		// bad query with garbage has no avgs
		assertEquals(condAC.sqlTest(garbageQuery, " >= 1"), 0);
	}

}	// end - class CondAvgCountTests

