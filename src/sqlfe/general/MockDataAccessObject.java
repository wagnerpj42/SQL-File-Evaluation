package sqlfe.general;

import java.sql.Connection;
import java.sql.ResultSet;

public class MockDataAccessObject implements IDAO {
	// data
	private Connection conn = null;			// JDBC connection
	private ResultSet rset = null;			// result set for queries
	private int returnValue;				// return value for other commands
	@SuppressWarnings("unused")
	private boolean returnBoolean;			// return boolean value
	@SuppressWarnings("unused")
	private String hostName;				// DBMS host
	@SuppressWarnings("unused")
	private String portString;				// DBMS port
	@SuppressWarnings("unused")
	private String idName;					// DBMS system id
	@SuppressWarnings("unused")
	private String username;				// DBMS user name
	@SuppressWarnings("unused")
	private String password;				// DBMS user password
	@SuppressWarnings("unused")
	private boolean forTesting;				// whether DAO used for JUnit testing or regular evaluation
	
	// constructor
	public MockDataAccessObject(String hostName, String portString, String idName, String username, String password, boolean forTesting) {
		this.hostName = hostName;
		this.portString = portString;
		this.idName = idName;
		this.username = username;
		this.password = password;
		this.forTesting = forTesting;
	}
	
	// --- connect - connect to the Oracle database
	public Connection connect() {
		conn = null;
		return conn;
	}	// end - method connect
	
	// --- executeSQLQuery - execute an SQL query
	public ResultSet executeSQLQuery (String sqlQuery) {
		// --- 3a) execute SQL query
		rset = null;
		return rset;
	}	// end - method executeSQLQuery

	// --- executeSQLQueryPrepared - execute an SQL query with Prepared Statement
	public ResultSet executeSQLQueryPrepared (String sqlQuery) {
		// --- 3a) execute SQL query
		rset = null;
		return rset;
	}	// end - method executeSQLQueryPrepared
	
	// --- executeSQLNonQuery - execute an SQL command that is not a query
	public int executeSQLNonQuery (String sqlCommand) {
		// --- 3b) execute SQL non-query command
		returnValue = -1;
		return returnValue;
	}	// end - method executeSQLNonQuery
	
	// --- processResultSet - process the result set
	public ResultSetMetaDataSummary processResultSet (ResultSet resSet) {
		// --- 4) process result set
		ResultSetMetaDataSummary summary = new ResultSetMetaDataSummary();
		int rowCount = 0;					// row count (all following vars for summary)
		int columnCount = 0;				// column count 
		String columnSet = "";				// column name set as string
		String resultString = "";			// result string 
		
		// column processing
		columnCount = 4;
		columnSet = "CID   Name    ResTownID    Type";
		
		// row processing
		resultString += ("1 Bannon Person Mpls.\n" +
		                 "2 Neff   Person St.Paul\n"
						);
		rowCount = 2;
		
		// put results into summary object
		summary.setNumCols(columnCount);
		summary.setColumnSet(columnSet);
		summary.setNumRows(rowCount);
		summary.setResultString(resultString);

		return summary;
	}	// end - method processResultSet
	
	// --- disconnect - disconnect from the Oracle database
	public Connection disconnect () {
		// --- 5) disconnect from database
		return null;
	}	// end - method disconnect

}
