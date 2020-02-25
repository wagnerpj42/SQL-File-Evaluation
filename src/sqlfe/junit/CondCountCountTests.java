/*
 * CondCountCountTests - JUnit test case class to test CondCountCount class
 *
 * Created - Paul J. Wagner, 5-Oct-2018
 */
package sqlfe.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sqlfe.sqltests.CondCountCount;

public class CondCountCountTests extends AbstractTest {
	// data
	CondCountCount condCC = null;
	
	@Before
	public void setUp() throws Exception {
		condCC = new CondCountCount();
	}

	@After
	public void tearDown() throws Exception {
		condCC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query displaying count with one group by has one count
		assertEquals(condCC.sqlTest(testDAO, groupByQuery, " >= 1").getScore(), 10);
		// valid nested query with two selects but no counts, has count equal to zero
		assertEquals(condCC.sqlTest(testDAO, nestedQuery, " == 0").getScore(), 10);
		// null query has no selects
		assertEquals(condCC.sqlTest(testDAO, nullQuery, " >= 1").getScore(), 0);
		// bad query with select but little else has no counts
		assertEquals(condCC.sqlTest(testDAO, badQuery, " >= 1").getScore(), 0);
		// bad query with garbage has no counts
		assertEquals(condCC.sqlTest(testDAO, garbageQuery, " >= 1").getScore(), 0);
	}

}	// end - class CondSelectCountTests
