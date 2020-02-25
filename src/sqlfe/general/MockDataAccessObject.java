package sqlfe.general;

import java.sql.Connection;
import java.sql.ResultSet;

public class MockDataAccessObject implements IDAO {
	private Connection conn = null;			// JDBC connection
	private ResultSet rset = null;			// result set for queries
	private int returnValue;				// return value for all other commands
		
	// --- connect - connect to the Oracle database
	public Connection connect() {
		conn = null;
		return conn;
	}	// end - method connect
	
	// --- executeSQLQuery - execute an SQL query
	public ResultSet executeSQLQuery (String sqlQuery) {
		// --- 3a) execute SQL query
		return rset;
	}	// end - method executeSQLQuery

	// --- executeSQLQueryPrepared - execute an SQL query with Prepared Statement
	public ResultSet executeSQLQueryPrepared (String sqlQuery) {
		// --- 3a) execute SQL query
		return rset;
	}	// end - method executeSQLQueryPrepared
	
	// --- executeSQLNonQuery - execute an SQL command that is not a query
	public int executeSQLNonQuery (String sqlCommand) {
		// --- 3b) execute SQL non-query command
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
		rowCount = 7;
		
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
