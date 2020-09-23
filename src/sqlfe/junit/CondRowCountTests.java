/*
 * CondRowCountTests - JUnit test case class to test CondRowCount class
 *
 * Created - Paul J. Wagner, 20-Feb-2019
 */
package sqlfe.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sqlfe.sqltests.CondRowCount;

public class CondRowCountTests extends AbstractTest {
	// data
	CondRowCount condRC = null;
	
	@Before
	public void setUp() throws Exception {
		condRC = new CondRowCount();
		testDAO.connect();
	}

	@After
	public void tearDown() throws Exception {
		testDAO.disconnect();
		condRC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query of Achievement generates 11 rows
		assertEquals(condRC.sqlTest(testDAO, achievementAllQuery, " == 11").getScore(), 10);
		// valid query of Creature generates 8 rows
		assertEquals(condRC.sqlTest(testDAO, creatureAllQuery, " == 8").getScore(), 10);
		// valid query of Skill generates 8 rows
		assertEquals(condRC.sqlTest(testDAO, skillAllQuery, " == 8").getScore(), 10);
		// null query generates no rows
		assertEquals(condRC.sqlTest(testDAO, nullQuery, " >= 1").getScore(), 0);
		// bad query generates no rows
		assertEquals(condRC.sqlTest(testDAO, badQuery, " >= 1").getScore(), 0);
		// bad query with garbage generates no rows
		assertEquals(condRC.sqlTest(testDAO, garbageQuery, " >= 1").getScore(), 0);
	}

}	// end - class CondSelectCountTests
