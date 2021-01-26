/*
 * TestResultSetEqual - class to test for result set equality
 * 
 * Created - Paul J. Wagner, 30-Oct-2017
 */
package sqlfe.sqltests;

import java.sql.ResultSet;

import sqlfe.general.*;

public class TestResultSetEqualExactly implements ISQLTest {
	// default constructor
	public TestResultSetEqualExactly() {
		// nothing at this time
	}
	
	// sqlTest - from interface
	public TestResult sqlTest (IDAO dao, Query givenQuery, String desiredQueryString) {
		int result = 0;						// result on scale 0 to 10
		String thisResultString = "";		// result set string returned from this query
		String desiredResultString = "";	// row count returned from desired query
		ResultSet rset = null;				// result set for SQL query
											// summary of result set's metadata
		ResultSetMetaDataSummary summary = new ResultSetMetaDataSummary();
											// build Query object for desired query string
		Query desiredQuery = new Query(desiredQueryString);
		
		// 1) execute given query, get result set string for this query
		try {
			rset = dao.executeSQLQuery(givenQuery.toString());
			summary = dao.processResultSet(rset);
			thisResultString = summary.getResultString();
		} catch (Exception e) {
			System.err.println("Error executing SQL/processing result set for given");
			thisResultString = "given_error";
		}
		
		rset = null;
		summary = null;
		
		// 2) execute desired query, get counts for this query	
		try {
			rset = dao.executeSQLQuery(desiredQuery.toString());
			summary = dao.processResultSet(rset);
			desiredResultString = summary.getResultString();
		} catch (Exception e) {
			System.err.println("Error executing SQL/processing result set for desired");
			desiredResultString = "desired_error";
		}

		rset = null;
		summary = null;
				
		try {
			if (thisResultString.equals(desiredResultString)) {
				result = 10;
			}
			else {
				result = 0;
			}
		}
		catch (NullPointerException npe) {
			//System.err.println("TestResultSetEqualExactly - sqlTest - submitted query does not generate result string");
		}
		
		return new TestResult(result);

	}	// end - method sqlTest
	
	// getName - return the test name
	public String getName() {
		return ("TestResultSetEqualExactly");
	}
	
	// getDesc - return the test description
	public String getDesc() {
		return "Answer has exactly same result set as desired query";
	}
}	// end - class TestResultSetEqualExactly
