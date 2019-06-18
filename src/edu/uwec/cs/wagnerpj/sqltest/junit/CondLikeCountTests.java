/*
 * CondLikeCountTests - JUnit test case class to test CondLikeCount class
 *
 * Created - Paul J. Wagner, 3-Apr-2019
 */
package edu.uwec.cs.wagnerpj.sqltest.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uwec.cs.wagnerpj.sqltest.sqltests.CondLikeCount;

public class CondLikeCountTests extends AbstractTest {
	// data
	CondLikeCount condLC = null;
	
	@Before
	public void setUp() throws Exception {
		condLC = new CondLikeCount();
	}

	@After
	public void tearDown() throws Exception {
		condLC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query with one like
		assertEquals(condLC.sqlTest(likeQuery, " == 1"), 10);		
		// valid query displaying count with one group by but no likes
		assertEquals(condLC.sqlTest(groupByQuery, " >= 1"), 0);
		// valid nested query with two selects but no likes
		assertEquals(condLC.sqlTest(nestedQuery, " == 0"), 10);
		// null query has no likes
		assertEquals(condLC.sqlTest(nullQuery, " >= 1"), 0);
		// bad query with select but little else has no likes
		assertEquals(condLC.sqlTest(badQuery, " >= 1"), 0);
		// bad query with garbage has no likes
		assertEquals(condLC.sqlTest(garbageQuery, " >= 1"), 0);
	}

}	// end - class CondAvgCountTests

