/*
 * CondBasicContentTests - JUnit test case class to test CondBasicContent class
 *
 * Created - Paul J. Wagner, 2-Oct-2018
 */
package edu.uwec.cs.wagnerpj.sqltest.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uwec.cs.wagnerpj.sqltest.sqltests.CondBasicContent;

public class CondBasicContentTests extends AbstractTest {
	// data
	CondBasicContent condBC = null;
	
	@Before
	public void setUp() throws Exception {
		condBC = new CondBasicContent();
	}

	@After
	public void tearDown() throws Exception {
		condBC = null;
	}

	@Test
	public void testSqlTest() {
		// valid query with one from meets basic content standard
		assertEquals(condBC.sqlTest(creatureAllQuery, ""), 10);
		// valid nested query with two froms
		assertEquals(condBC.sqlTest(nestedQuery, ""), 10);
		// valid query with one JOIN/ON
		assertEquals(condBC.sqlTest(joinOneQuery, ""), 10);
		// valid query with one comma join and one JOIN/ON
		assertEquals(condBC.sqlTest(joinManyQuery, ""), 10);
		// valid query with table joined to itself with JOIN/ON
		assertEquals(condBC.sqlTest(joinTableSelfQuery, ""), 10);
		// null query has does not meet basic content standard
		assertEquals(condBC.sqlTest(nullQuery, ""), 0);
		// bad query with select/improper column list, no table does not meet basic content standard
		assertEquals(condBC.sqlTest(badQuery, ""), 0);
		// bad query with select/from meets basic content standard
		assertEquals(condBC.sqlTest(badQuery2, ""), 10);
		// bad query with garbage does not meet basic content standard
		assertEquals(condBC.sqlTest(garbageQuery, ""), 0);
	}

}	// end - class CondTableCountTests
