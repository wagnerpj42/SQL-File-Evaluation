/*
 * TestTableListEqual - class to test for table list equality
 * 
 * Created - Paul J. Wagner, 19-Sep-2018
 */
package sqlfe.sqltests;

import java.sql.ResultSet;
//import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import sqlfe.general.*;

public class TestTableListEqual implements ISQLTest {
	// default constructor
	public TestTableListEqual() {
		
	}

	// sqlTest - from interface
	public TestResult sqlTest (IDAO dao, Query givenQuery, String desiredQueryString) {
		
		int result;						// result on scale 0 to 10
		String givenTableList = "";		// table list generated from given query
		String desiredTableList = "";	// table list generated from desired query
										// query object for string
		Query desiredQuery = new Query(desiredQueryString);
		
		// get table list from student (given) query
		givenTableList = getTableList(dao, givenQuery).trim();
		
		// get table list from desired query
		desiredTableList = getTableList(dao, desiredQuery).trim();

		// set result
		if (givenTableList.equals(desiredTableList)) {
			result = 10;
		}
		else {
			result = 0;
		};
		
		return new TestResult(result);

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
		ResultSet rs = null;						// result set for view query
		//ResultSetMetaDataSummary summary = new ResultSetMetaDataSummary();
		String viewString = ("create view temp_view as " + query.toString());	
		String resultList = "";						// result list as string

		dao.executeSQLNonQuery(viewString);		// create a temporary view for this query, ignore result
		
		// generate list of tables from query through metadata query
		String viewTablesQuery = "select referenced_name\n" + 
				"  from SYS.USER_DEPENDENCIES\n" + 
				" where type = 'VIEW'\n" + 
				"   AND REFERENCED_TYPE IN ('VIEW', 'TABLE', 'SYNONYM')\n" + 
				"   AND name = upper('temp_view')\n" + 
				"order by referenced_name";
		rs = dao.executeSQLQuery(viewTablesQuery);
		
		// process result set to get this list of tables, and assign list to result string
		if (rs != null) {
			resultList = processTableResultSet(rs);
		}
		
		// cleanup
		dao.executeSQLNonQuery("drop view temp_view");
		dao.executeSQLNonQuery("commit");
				
		return resultList;
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
