/*
 * CondSumCountTests - JUnit test case class to test CondSumCount class
 *
 * Created - Paul J. Wagner, 07-Aug-2019
 */
package edu.uwec.cs.wagnerpj.sqltest.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uwec.cs.wagnerpj.sqltest.sqltests.CondSumCount;

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
		assertEquals(condSC.sqlTest(sumQuery, " == 1"), 10);		
		// valid query with one group by but no sums
		assertEquals(condSC.sqlTest(groupByQuery, " >= 1"), 0);
		// valid nested query with two selects but no sums
		assertEquals(condSC.sqlTest(nestedQuery, " == 0"), 10);
		// null query has no sums
		assertEquals(condSC.sqlTest(nullQuery, " >= 1"), 0);
		// bad query with select but little else has no sums
		assertEquals(condSC.sqlTest(badQuery, " >= 1"), 0);
		// bad query with garbage has no sums
		assertEquals(condSC.sqlTest(garbageQuery, " >= 1"), 0);
	}

}	// end - class CondAvgCountTests

