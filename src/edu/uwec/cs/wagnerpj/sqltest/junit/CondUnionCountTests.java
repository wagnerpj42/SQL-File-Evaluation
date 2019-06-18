/*
 * CondUnionCountTests - JUnit test case class to test CondUnionCount class
 *
 * Created - Paul J. Wagner, 3-Apr-2019
 */
package edu.uwec.cs.wagnerpj.sqltest.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uwec.cs.wagnerpj.sqltest.sqltests.CondUnionCount;

public class CondUnionCountTests extends AbstractTest {
	// data
	CondUnionCount condUC = null;
	
	@Before
	public void setUp() throws Exception {
		condUC = new CondUnionCount();
	}

	@After
	public void tearDown() throws Exception {
		condUC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query with one union
		assertEquals(condUC.sqlTest(unionQuery, " == 1"), 10);		
		// valid query displaying count with one group by but no unions
		assertEquals(condUC.sqlTest(groupByQuery, " >= 1"), 0);
		// valid nested query with two selects but no unions
		assertEquals(condUC.sqlTest(nestedQuery, " == 0"), 10);
		// null query has no unions
		assertEquals(condUC.sqlTest(nullQuery, " >= 1"), 0);
		// bad query with select but little else has no unions
		assertEquals(condUC.sqlTest(badQuery, " >= 1"), 0);
		// bad query with garbage has no unions
		assertEquals(condUC.sqlTest(garbageQuery, " >= 1"), 0);
	}

}	// end - class CondAvgCountTests

