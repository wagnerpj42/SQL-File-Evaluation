/*
 * CondIntersectCountTests - JUnit test case class to test CondIntersectCount class
 *
 * Created - Paul J. Wagner, 08-Aug-2019
 */
package sqlfe.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sqlfe.sqltests.CondIntersectCount;

public class CondIntersectCountTests extends AbstractTest {
	// data
	CondIntersectCount condIC = null;
	
	@Before
	public void setUp() throws Exception {
		condIC = new CondIntersectCount();
	}

	@After
	public void tearDown() throws Exception {
		condIC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query with one intersect
		assertEquals(condIC.sqlTest(testDAO, intersectQuery, " == 1").getScore(), 10);		
		// valid query displaying count with one group by but no intersect
		assertEquals(condIC.sqlTest(testDAO, groupByQuery, " >= 1").getScore(), 0);
		// valid nested query with two selects but no intersects
		assertEquals(condIC.sqlTest(testDAO, nestedQuery, " == 0").getScore(), 10);
		// valid query with intersect at end of line
		assertEquals(condIC.sqlTest(testDAO, intersect2Query, " == 1").getScore(), 10);		
		// null query has no intersects
		assertEquals(condIC.sqlTest(testDAO, nullQuery, " >= 1").getScore(), 0);
		// bad query with select but little else has no intersects
		assertEquals(condIC.sqlTest(testDAO, badQuery, " >= 1").getScore(), 0);
		// bad query with garbage has no intersects
		assertEquals(condIC.sqlTest(testDAO, garbageQuery, " >= 1").getScore(), 0);
	}

}	// end - class CondIntersectCountTests

