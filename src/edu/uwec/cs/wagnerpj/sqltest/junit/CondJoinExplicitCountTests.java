/*
 * CondJoinCountExplicitTests - JUnit test case class to test CondJoinExplicitCount class
 *
 * Created - Paul J. Wagner, 20-Feb-2019
 */
package edu.uwec.cs.wagnerpj.sqltest.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uwec.cs.wagnerpj.sqltest.sqltests.CondJoinExplicitCount;

public class CondJoinExplicitCountTests extends AbstractTest {
	// data
	CondJoinExplicitCount condJEC = null;
	
	@Before
	public void setUp() throws Exception {
		condJEC = new CondJoinExplicitCount();
	}

	@After
	public void tearDown() throws Exception {
		condJEC = null;
	}

	@Test
	public void testSqlCond() {
		// valid query with one from
		assertEquals(condJEC.sqlTest(creatureAllQuery, " == 0"), 10);
		// valid nested query with two select/froms has no joins
		assertEquals(condJEC.sqlTest(nestedQuery, " == 1"), 0);
		// valid query with one JOIN/ON
		assertEquals(condJEC.sqlTest(joinOneQuery, " == 1"), 10);
		// valid query with one comma join and one JOIN/ON
		assertEquals(condJEC.sqlTest(joinManyQuery, " == 1"), 10);
		// valid query with table joined to itself with JOIN/ON
		assertEquals(condJEC.sqlTest(joinTableSelfQuery, " == 1"), 10);
		// null query has no joins
		assertEquals(condJEC.sqlTest(nullQuery, " >= 1"), 0);
		// bad query with select/improper column list has no joins
		assertEquals(condJEC.sqlTest(badQuery, " == 0"), 10);
		// bad query with garbage has no joins
		assertEquals(condJEC.sqlTest(garbageQuery, " >= 1"), 0);
	}

}	// end - class CondTableCountTests
