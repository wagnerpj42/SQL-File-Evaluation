/*
 * TestSameColumnSet - class to test for equality of column set, regardless of order
 * 
 * Created - Paul J. Wagner, 01-Nov-2017
 */
package sqlfe.sqltests;

import java.sql.ResultSet;

import sqlfe.general.*;

public class TestSameColumnSet implements ISQLTest {
	// default constructor
	public TestSameColumnSet() {
		
	}
	
	// sqlTest - from interface
	public TestResult sqlTest (IDAO dao, Query givenQuery, String desiredQueryString) {
		
		int result;						// result from 0 (wrong) to 10 (totally correct)
		String thisColSet = "";			// column set returned from this query
		String desiredColSet = "";		// column set returned from desired query
		ResultSet rset = null;			// result set for SQL statement
										// summary of result set's metadata
		ResultSetMetaDataSummary summary = new ResultSetMetaDataSummary();
										// query object for string
		Query desiredQuery = new Query(desiredQueryString);
	
		// 1) execute given query, get column count for this query
		rset = dao.executeSQLQuery(givenQuery.toString());
		summary = dao.processResultSet(rset);
		thisColSet = summary.getColumnSet();
		
		rset = null;
		summary = null;
			
		// 2) execute desired query, get counts for this query
		rset = dao.executeSQLQuery(desiredQuery.toString());
		summary = dao.processResultSet(rset);					// includes sorting the column names
		desiredColSet = summary.getColumnSet();
		
		rset = null;
		summary = null;
		
		// set result based on whether column sets are equal
		if (thisColSet == null) {
			result = 0;
		}
		else if (thisColSet.equals(desiredColSet)) {
			result = 10;
		}
		else {
			result = 0;
		}
		
		return new TestResult(result);

	}	// end - method sqlTest
	
	// getName - return the test name
	public String getName() {
		return ("TestSameColumnSet");
	}
	
	// getDesc - return the test description
	public String getDesc() {
		return "Answer has same set of column names as desired query";
	}
}	// end - class TestSameColumnSet
