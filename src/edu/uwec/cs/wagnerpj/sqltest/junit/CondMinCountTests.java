/*
 * CondMinCountTests - JUnit test case class to test CondMinCount class
 *
 * Created - Paul J. Wagner, 08-Aug-2019
 */
package edu.uwec.cs.wagnerpj.sqltest.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uwec.cs.wagnerpj.sqltest.sqltests.CondMinCount;

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
		assertEquals(condMC.sqlTest(minQuery, " == 1"), 10);		
		// valid query displaying count with one group by but no min
		assertEquals(condMC.sqlTest(groupByQuery, " >= 1"), 0);
		// valid nested query with two selects but no mins
		assertEquals(condMC.sqlTest(nestedQuery, " == 0"), 10);
		// null query has no mins
		assertEquals(condMC.sqlTest(nullQuery, " >= 1"), 0);
		// bad query with select but little else has no mins
		assertEquals(condMC.sqlTest(badQuery, " >= 1"), 0);
		// bad query with garbage has no mins
		assertEquals(condMC.sqlTest(garbageQuery, " >= 1"), 0);
	}

}	// end - class CondMinCountTests

