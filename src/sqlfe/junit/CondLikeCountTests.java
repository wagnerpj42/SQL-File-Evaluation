/*
 * CondLikeCountTests - JUnit test case class to test CondLikeCount class
 *
 * Created - Paul J. Wagner, 3-Apr-2019
 */
package sqlfe.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sqlfe.sqltests.CondLikeCount;

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
		assertEquals(condLC.sqlTest(testDAO, likeQuery, " == 1").getScore(), 10);		
		// valid query displaying count with one group by but no likes
		assertEquals(condLC.sqlTest(testDAO, groupByQuery, " >= 1").getScore(), 0);
		// valid nested query with two selects but no likes
		assertEquals(condLC.sqlTest(testDAO, nestedQuery, " == 0").getScore(), 10);
		// null query has no likes
		assertEquals(condLC.sqlTest(testDAO, nullQuery, " >= 1").getScore(), 0);
		// bad query with select but little else has no likes
		assertEquals(condLC.sqlTest(testDAO, badQuery, " >= 1").getScore(), 0);
		// bad query with garbage has no likes
		assertEquals(condLC.sqlTest(testDAO, garbageQuery, " >= 1").getScore(), 0);
	}

}	// end - class CondAvgCountTests

