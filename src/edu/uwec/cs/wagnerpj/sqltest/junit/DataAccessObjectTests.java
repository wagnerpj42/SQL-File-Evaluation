/*
 * DataAccessObjectTests - JUnit test case class to test DataAccessObject class
 *
 * Created - Paul J. Wagner, 18-Oct-2017
 */
package edu.uwec.cs.wagnerpj.sqltest.junit;

import java.sql.*;
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uwec.cs.wagnerpj.sqltest.general.DataAccessObject;
import edu.uwec.cs.wagnerpj.sqltest.general.ResultSetMetaDataSummary;

public class DataAccessObjectTests extends AbstractTest {
	// data
	private DataAccessObject dao;			// data access object
	private Connection conn;				// database connection
	private ResultSet  rset;				// query result set
	private ResultSetMetaDataSummary summary; // result set metadata (rows and columns)
	private int status;						// return value from some dao methods
											// test query
	//private Query creatureAllQuery = new Query("SELECT * FROM Creature");
	//private Query creatureZeroQuery = new Query("SELECT c_id FROM Creature WHERE c_id = 100");
	
	@Before
	public void setup() {
		dao = new DataAccessObject();
	}

	@Test
	public void testConnect() {
		conn = dao.connect();
		assertNotNull(conn);
	}

	@Test
	public void testExecuteSQLQuery() {
		dao.connect();
		// result set with >0 rows is not null
		rset = dao.executeSQLQuery(creatureAllQuery.toString());
		assertNotNull(rset);
		// result set with zero rows is still not null
		rset = dao.executeSQLQuery(creatureZeroQuery.toString());
		assertNotNull(rset);		
		dao.disconnect();
	}

	@Test
	public void testExecuteSQLQueryPrepared() {
		dao.connect();
		rset = dao.executeSQLQuery(creatureAllQuery.toString());
		summary = dao.processResultSet(rset);
		assertEquals(4, summary.getNumCols());
		assertEquals(8, summary.getNumRows());
		dao.disconnect();
	}
	
	@Test
	public void testExecuteSQLNonQuery() {
		dao.connect();
		status = dao.executeSQLNonQuery("COMMIT");
		assertEquals(status, 0);
		dao.disconnect();
	}
	
	@Test
	public void testProcessResultSet() {
		dao.connect();
		rset = dao.executeSQLQuery(creatureAllQuery.toString());
		
		dao.disconnect();
	}

	@Test
	public void testDisconnect() {
		conn = dao.disconnect();
		assertNull(conn);
	}

	@After
	public void teardown() {
		dao.disconnect();
		conn = null;		
	}
	
}	// end - test case class DataAccessObjectTests
