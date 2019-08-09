/*
 * CondOrCountTests - JUnit test case class to test CondOrCount class
 *
 * Created - Paul J. Wagner, 08-Aug-2019
 */
package edu.uwec.cs.wagnerpj.sqltest.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uwec.cs.wagnerpj.sqltest.sqltests.CondOrCount;

public class CondOrCountTests extends AbstractTest {
	// data
	CondOrCount condOC = null;
	
	@Before
	public void setUp() throws Exception {
		condOC = new CondOrCount();
	}

	@After
	public void tearDown() throws Exception {
		condOC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query with one or
		assertEquals(condOC.sqlTest(creatureOrQuery, " == 1"), 10);		
		// valid query with one group by but no ors
		assertEquals(condOC.sqlTest(groupByQuery, " >= 1"), 0);
		// valid nested query with two selects but no ors
		assertEquals(condOC.sqlTest(nestedQuery, " == 0"), 10);
		// null query has no ors
		assertEquals(condOC.sqlTest(nullQuery, " >= 1"), 0);
		// bad query with select but little else has no ors
		assertEquals(condOC.sqlTest(badQuery, " >= 1"), 0);
		// bad query with garbage has no ors
		assertEquals(condOC.sqlTest(garbageQuery, " >= 1"), 0);
	}

}	// end - class CondAvgCountTests

