/*
 * CondTableCountTests - JUnit test case class to test CondTableCount class
 *
 * Created - Paul J. Wagner, 1-Oct-2018
 */
package sqlfe.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sqlfe.sqltests.CondTableCount;

public class CondTableCountTests extends AbstractTest {
	// data
	CondTableCount condTC = null;
	
	@Before
	public void setUp() throws Exception {
		condTC = new CondTableCount();
	}

	@After
	public void tearDown() throws Exception {
		condTC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query with one from
		assertEquals(condTC.sqlTest(testDAO, creatureAllQuery, " >= 1").getScore(), 10);
		// valid nested query with two froms
		assertEquals(condTC.sqlTest(testDAO, nestedQuery, " == 2").getScore(), 10);
		// valid query with one JOIN/ON
		assertEquals(condTC.sqlTest(testDAO, joinOneQuery, " == 2").getScore(), 10);
		// valid query with one comma join and one JOIN/ON
		assertEquals(condTC.sqlTest(testDAO, joinManyQuery, " == 3").getScore(), 10);
		// valid query with table joined to itself with JOIN/ON
		assertEquals(condTC.sqlTest(testDAO, joinTableSelfQuery, " == 2").getScore(), 10);
		// null query has no tables
		assertEquals(condTC.sqlTest(testDAO, nullQuery, " >= 1").getScore(), 0);
		// bad query with select/improper column list has no tables
		assertEquals(condTC.sqlTest(testDAO, badQuery, " >= 1").getScore(), 0);
		// bad query with garbage has no froms
	}

}	// end - class CondTableCountTests
