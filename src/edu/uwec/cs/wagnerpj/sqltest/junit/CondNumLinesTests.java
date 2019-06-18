/*
 * CondNumLinesCountTests - JUnit test case class to test CondNumLinesCount class
 *
 * Created - Paul J. Wagner, 3-Apr-2019
 */
package edu.uwec.cs.wagnerpj.sqltest.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uwec.cs.wagnerpj.sqltest.sqltests.CondNumLinesCount;

public class CondNumLinesTests extends AbstractTest {
	// data
	CondNumLinesCount condNLC = null;
	
	@Before
	public void setUp() throws Exception {
		condNLC = new CondNumLinesCount();
	}

	@After
	public void tearDown() throws Exception {
		condNLC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query with at least three lines
		assertEquals(condNLC.sqlTest(multiLineQuery, " >= 3"), 10);		
		// valid query on one line
		assertEquals(condNLC.sqlTest(groupByQuery, " == 1"), 10);
		// valid nested query but one line
		assertEquals(condNLC.sqlTest(nestedQuery, " == 1"), 10);
		// null query considered one line
		assertEquals(condNLC.sqlTest(nullQuery, " == 0"), 0);
		// bad query with select but little else has one line
		assertEquals(condNLC.sqlTest(badQuery, " == 1"), 10);
		// bad query with garbage has one line
		assertEquals(condNLC.sqlTest(garbageQuery, " == 1"), 10);
	}

}	// end - class CondAvgCountTests

