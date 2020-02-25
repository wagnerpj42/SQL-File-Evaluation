/*
 * CondDescCountTests - JUnit test case class to test CondDescCount class
 *
 * Created - Paul J. Wagner, 20-Feb-2019
 */
package sqlfe.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sqlfe.sqltests.CondDescCount;

public class CondDescCountTests extends AbstractTest {
	// data
	CondDescCount condDC = null;
	
	@Before
	public void setUp() throws Exception {
		condDC = new CondDescCount();
	}

	@After
	public void tearDown() throws Exception {
		condDC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query with one from but no DESC
		assertEquals(condDC.sqlTest(testDAO, creatureAllQuery, " == 0").getScore(), 10);
		// valid query with one JOIN/ON but no DESC
		assertEquals(condDC.sqlTest(testDAO, joinOneQuery, " == 1").getScore(), 0);
		// valid query with order by but not desc
		assertEquals(condDC.sqlTest(testDAO, orderByQuery, " == 1").getScore(), 0);
		// valid query with order by and desc
		assertEquals(condDC.sqlTest(testDAO, orderByDescQuery, " == 1").getScore(), 10);
		// valid query with order by and descending
		assertEquals(condDC.sqlTest(testDAO, orderByDesc2Query, " == 1").getScore(), 10);
		// valid query with two desc/descending
		assertEquals(condDC.sqlTest(testDAO, orderByDesc3Query, " == 2").getScore(), 10);		
		// null query has no desc
		assertEquals(condDC.sqlTest(testDAO, nullQuery, " >= 1").getScore(), 0);
		// bad query with select/improper column list has no descs
		assertEquals(condDC.sqlTest(testDAO, badQuery, " == 0").getScore(), 10);
		// bad query with garbage has no descs
		assertEquals(condDC.sqlTest(testDAO, garbageQuery, " >= 1").getScore(), 0);
	}

}	// end - class CondDescCountTests
