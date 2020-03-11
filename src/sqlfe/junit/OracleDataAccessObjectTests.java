/*
 * DataAccessObjectTests - JUnit test case class to test DataAccessObject class
 *
 * Created - Paul J. Wagner, 18-Oct-2017
 */
package sqlfe.junit;

import java.sql.*;
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sqlfe.general.OracleDataAccessObject;
import sqlfe.general.ResultSetMetaDataSummary;

public class OracleDataAccessObjectTests extends AbstractTest { 
	// data
	private OracleDataAccessObject dao;			// local data access object for Oracle testing
	private Connection conn;					// database connection
	private ResultSet  rset;					// query result set
	private ResultSetMetaDataSummary summary; 	// result set metadata (rows and columns)
	private int status;							// return value from some dao methods
	
	@Before
	public void setup() {
		dao = new OracleDataAccessObject("hostname", "sysid", "username", "password");	// must be changed for running unit tests
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
