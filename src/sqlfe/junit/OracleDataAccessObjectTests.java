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

//import sqlfe.general.OracleDataAccessObject;
import sqlfe.general.ResultSetMetaDataSummary;

public class OracleDataAccessObjectTests extends AbstractTest { 
	// data
	//private OracleDataAccessObject testDAO;	// data access object, inherited from AbstractTest
	private Connection conn;					// database connection
	private ResultSet  rset;					// query result set
	private ResultSetMetaDataSummary summary; 	// result set metadata (rows and columns)
	private int status;							// return value from some dao methods
	
	@Before
	public void setup() {
		// testDAO is initialized in the AbstractTest constructor
	}

	@Test
	public void testConnect() {
		conn = testDAO.connect();
		assertNotNull(conn);
	}

	@Test
	public void testExecuteSQLQuery() {
		testDAO.connect();
		// result set with >0 rows is not null
		rset = testDAO.executeSQLQuery(creatureAllQuery.toString());
		assertNotNull(rset);
		// result set with zero rows is still not null
		rset = testDAO.executeSQLQuery(creatureZeroQuery.toString());
		assertNotNull(rset);		
		testDAO.disconnect();
	}

	@Test
	public void testExecuteSQLQueryPrepared() {
		testDAO.connect();
		rset = testDAO.executeSQLQuery(creatureAllQuery.toString());
		summary = testDAO.processResultSet(rset);
		assertEquals(4, summary.getNumCols());
		assertEquals(8, summary.getNumRows());
		testDAO.disconnect();
	}
	
	@Test
	public void testExecuteSQLNonQuery() {
		testDAO.connect();
		status = testDAO.executeSQLNonQuery("COMMIT");
		assertEquals(status, 0);
		testDAO.disconnect();
	}
	
	@Test
	public void testProcessResultSet() {
		testDAO.connect();
		rset = testDAO.executeSQLQuery(creatureAllQuery.toString());
		
		testDAO.disconnect();
	}

	@Test
	public void testDisconnect() {
		conn = testDAO.disconnect();
		assertNull(conn);
	}

	@After
	public void teardown() {
		testDAO.disconnect();
		conn = null;		
	}
	
}	// end - test case class DataAccessObjectTests
