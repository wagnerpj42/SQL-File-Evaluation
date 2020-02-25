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
		dao.connect();

		rset = dao.executeSQLQuery(givenQuery.toString());
		summary = dao.processResultSet(rset);
		thisColSet = summary.getColumnSet();
		
		dao.disconnect();
		rset = null;
		summary = null;
			
		// 2) execute desired query, get counts for this query
		dao.connect();

		rset = dao.executeSQLQuery(desiredQuery.toString());
		summary = dao.processResultSet(rset);
		desiredColSet = summary.getColumnSet();
		
		dao.disconnect();
		rset = null;
		summary = null;
		
		//System.out.println("thisColSet:    >" + thisColSet + "<");
		//System.out.println("desiredColSet: >" + desiredColSet + "<");
		
		// TODO: need to allow for variation in order of column names in set
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
	
	// getName - from interface
	public String getName() {
		return ("TestSameColumnSet");
	}
	
	public String getDesc() {
		return "Answer has same set of column names as desired query";
	}
}	// end - class TestSameColumnSet
