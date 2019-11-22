/*
 * TestTableListEqual - class to test for table list equality
 * 
 * Created - Paul J. Wagner, 19-Sep-2018
 */
package edu.uwec.cs.wagnerpj.sqltest.sqltests;

import java.sql.ResultSet;
//import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import edu.uwec.cs.wagnerpj.sqltest.general.*;

public class TestTableListEqual implements ISQLTest {
	// default constructor
	public TestTableListEqual() {
		
	}

	// sqlTest - from interface
	public int sqlTest (IDAO dao, Query givenQuery, String desiredQueryString) {
		
		int result;						// result on scale 0 to 10
		int givenTableCt = 0;			// table count from given query
		String givenTableList = "";		// table list generated from given query
		int desiredTableCt = 0;			// table count from desired query
		String desiredTableList = "";	// table list generated from desired query
										// query object for string
		Query desiredQuery = new Query(desiredQueryString);
		
		givenTableList = getTableList(dao, givenQuery).trim();
		//System.out.println("givenTableList is: >" +  givenTableList + "<");
		givenTableCt = givenTableList.split(" ").length;
		//System.out.println("givenTableCt is: " + givenTableCt);
		
		// TODO: this second view with same name sometimes not being created
		desiredTableList = getTableList(dao, desiredQuery).trim();
		//System.out.println("desiredTableList is: >" +  desiredTableList + "<");
		desiredTableCt = desiredTableList.split(" ").length;
		//System.out.println("desiredTableCt is: " + desiredTableCt);	
		
		// TODO: need to check for table list content, not just count
		
		if (givenTableCt == desiredTableCt) {
			result = 10;
		}
		else {
			result = 0;
		};
		
		return result;

	}	// end - method sqlTest
	
	// getName - from interface
	public String getName() {
		return ("TestTableListEqual");
	}
	
	public String getDesc() {
		return "Answer has same table list as desired query results";
	}
	
	// getTableList - auxiliary method, return a sorted table list for a Query
	public String getTableList(IDAO dao, Query query) {
		ResultSet rs = null;				// result set for view query
		//ResultSetMetaDataSummary summary = new ResultSetMetaDataSummary();
		String viewString = ("create view temp_view as " + query.toString());
		//System.out.println("view string is: >" + viewString + "<");	
		String result = "";					// result list as string
		//int queryResult = 0;				// query result for view creation

		dao.connect();							// connect to DAO

		dao.executeSQLNonQuery(viewString);		// create a temporary view for this query
		
		String viewTablesQuery = "select referenced_name\n" + 
				"  from SYS.USER_DEPENDENCIES\n" + 
				" where type = 'VIEW'\n" + 
				"   AND REFERENCED_TYPE IN ('VIEW', 'TABLE', 'SYNONYM')\n" + 
				"   AND name = upper('temp_view')\n" + 
				"order by referenced_name";
		rs = dao.executeSQLQuery(viewTablesQuery);
		
		// process result set to get list of tables, and assign list to result string
		result = processTableResultSet(rs);
		
		// cleanup
		dao.executeSQLNonQuery("drop view temp_view");
		dao.executeSQLNonQuery("commit");
		dao.disconnect();
		
		return result;
	}	// end - method getTableList
	
	// --- processTableResultSet - process the result set with a set of table names
	String processTableResultSet (ResultSet rset) {
		// --- process a result set containing one column
		String resultString = "";

		try {
			while (rset.next()) {
				resultString += rset.getString(1) + " ";
			}     // --- end - while
		}
		catch (SQLException sqle) {
			System.out.println("error in processing result set");
		}	
		return resultString;
	}	// end - method processResultSet
	
}	// end - class TestTableListEqual
