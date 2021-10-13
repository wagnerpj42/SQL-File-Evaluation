/*
 * Class DataAccessObject
 * 
 * Created by Paul J. Wagner, 22-SEP-2017
 * 
 * Notes: needs ojdbc7.jar on build path/project as of 11/2019
 */
package sqlfe.general;

import java.sql.*;

import sqlfe.util.Utilities;

public class OracleDataAccessObject implements IDAO {

	// data
	private Connection conn = null;			// JDBC connection
	private ResultSet rset = null;			// result set for queries
	private int returnValue;				// return value for other commands
	private boolean returnBoolean;			// return boolean value
	private String hostName;				// DBMS host
	private String portString;				// DBMS port
	private String idName;					// DBMS system id
	private String username;				// DBMS user name
	private String password;				// DBMS user password
	private boolean forTesting;				// whether DAO used for JUnit testing or regular evaluation
	
	// --- constructor
	public OracleDataAccessObject (String hostName, String portString, String idName, String username, String password, boolean forTesting) {
		this.hostName = hostName;
		this.portString = portString;
		this.idName = idName;
		this.username = username;
		this.password = password;
		this.forTesting = forTesting;
	}
		
	// --- connect - connect to the Oracle database
	public Connection connect() {
		// --- 1) get the Class object for the driver 
		try {
		   Class.forName ("oracle.jdbc.OracleDriver");
		}
		catch (ClassNotFoundException e) {
			if (forTesting) {
				System.out.println("Could not get class object for Driver, check if Oracle JDBC Connector file is on project build path");
			} else {
				Utilities.threadSafeOutput("\nCould not get class object for Driver, check if MySQL JDBC Connector file is on project build path\n");	// for JavaFX GUI thread safety
			}
		}

		// --- 2) connect to database
		String connectString = "jdbc:oracle:thin:@" + hostName.trim() + ":" + portString.trim() + ":" + idName.trim();
		try {
		   conn = DriverManager.getConnection(connectString, username, password);
		}
		catch (SQLException sqle) {
			if (forTesting) {
				System.out.println("Could not make connection to database, " + sqle.getMessage());
			} else {
				Utilities.threadSafeOutput("\nCould not make connection to database, " + sqle.getMessage() + "\n");		// for JavaFX GUI thread safety
			}
		}
		return conn;
	}	// end - method connect
	
	// --- executeSQLQuery - execute an SQL query
	public ResultSet executeSQLQuery (String sqlQuery) {
		// --- 3a) execute SQL query
		Statement stmt = null;		// SQL statement object
		rset = null;				// initialize result set
		try	{
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rset = stmt.executeQuery(sqlQuery);
		}
		catch (SQLException e) {
			//System.err.println("Could not execute SQL statement: >" + sqlQuery + "<");
			//System.err.println(e.getMessage());
		}
		return rset;
	}	// end - method executeSQLQuery

	// --- executeSQLQueryPrepared - execute an SQL query with Prepared Statement
	public ResultSet executeSQLQueryPrepared (String sqlQuery) {
		// --- 3a) execute SQL query
		PreparedStatement pStmt = null;		// SQL prepared statement object
		rset = null;						// initialize result set
		try	{
		   pStmt = conn.prepareStatement(sqlQuery);
		   rset = pStmt.executeQuery();
		}
		catch (SQLException sqle) {
			//System.err.println("Could not execute SQL statement: >" + sqlQuery + "<, " + sqle.getMessage());
		}
		return rset;
	}	// end - method executeSQLQueryPrepared
	
	// --- executeSQLNonQuery - execute an SQL command that is not a query
	public int executeSQLNonQuery (String sqlCommand) {
		// --- 3b) execute SQL non-query command
		Statement stmt = null;		// SQL statement object
		//returnValue = -1;			// initialize return value
		returnBoolean = false;		// initialize return boolean
		try	{
		   stmt = conn.createStatement();
		   returnValue = stmt.executeUpdate(sqlCommand);
		}
		catch (SQLException sqle) {
			System.err.println("Could not execute SQL command: >" + sqlCommand + "<, " + sqle.getMessage());
		}
		returnValue = (returnBoolean == true ? 1 : 0);
		return returnValue;
	}	// end - method executeSQLNonQuery
	
	// --- processResultSet - process the result set
	public ResultSetMetaDataSummary processResultSet (ResultSet resSet) {
		// --- 4) process result set
		ResultSetMetaData rsmd = null;
		ResultSetMetaDataSummary summary = new ResultSetMetaDataSummary();
		int rowCount = -1;					// row count (all following vars for summary)
		int columnCount = -1;				// column count 
		String columnSet = "";				// column name set as string
		String resultString = "";			// result string 
		
		try {
			rsmd = resSet.getMetaData();
			
			// column processing
			columnCount = rsmd.getColumnCount();
			for (int index = 1; index <= columnCount; index++) {
				columnSet += (rsmd.getColumnName(index) + " ");
			}
			columnSet = Utilities.sortString(columnSet);
			
			// row processing
			rowCount = 0;
			while (resSet.next()) {
				for (int index = 1; index <= columnCount; index++) {
					resultString += resSet.getString(index) + "  ";
				}
				resultString += "\n";
				rowCount++;
			}
			
			// put results into summary object
			summary.setNumCols(columnCount);
			summary.setColumnSet(columnSet);
			summary.setNumRows(rowCount);
			summary.setResultString(resultString);
		}
		catch (SQLException sqle) {
			//System.err.println("Error in processing result set");
		}
		catch (NullPointerException npe) {
			//System.err.println("DAO, processResultSet() - no result set generated");
		}
		return summary;
	}	// end - method processResultSet
	
	// --- disconnect - disconnect from the Oracle database
	public Connection disconnect () {
		// --- 5) disconnect from database
		try {
			if (conn != null) {
				conn.close();
			}
			if (rset != null) {
				rset = null;
			}
		}
		catch (SQLException sqle) {
			//System.err.println ("Error in closing database connection");
		}
		finally {
			if (conn != null) {
				try {
					conn.close();
				}
				catch (SQLException sqle) {
					conn = null;
				}
			}
			if (rset != null) {
				try {
					rset = null;
				}
				catch (Exception e) {
					rset = null;
				}
			}
		}
		return conn;
	}	// end - method disconnect

}	// end - class DataAccessObject
