/*
 * CondAndCountTests - JUnit test case class to test CondAndCount class
 *
 * Created - Paul J. Wagner, 3-Apr-2019
 */
package edu.uwec.cs.wagnerpj.sqltest.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uwec.cs.wagnerpj.sqltest.sqltests.CondAndCount;

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
		assertEquals(condAC.sqlTest(creatureAndQuery, " == 1"), 10);		
		// valid query with one group by but no ands
		assertEquals(condAC.sqlTest(groupByQuery, " >= 1"), 0);
		// valid nested query with two selects but no ands
		assertEquals(condAC.sqlTest(nestedQuery, " == 0"), 10);
		// null query has no ands
		assertEquals(condAC.sqlTest(nullQuery, " >= 1"), 0);
		// bad query with select but little else has no ands
		assertEquals(condAC.sqlTest(badQuery, " >= 1"), 0);
		// bad query with garbage has no ands
		assertEquals(condAC.sqlTest(garbageQuery, " >= 1"), 0);
	}

}	// end - class CondAvgCountTests

