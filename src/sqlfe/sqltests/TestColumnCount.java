/*
 * TestColumnCount - class to test for query column count equality
 * 
 * Created - Paul J. Wagner, 30-Oct-2017
 */
package sqlfe.sqltests;

import java.sql.ResultSet;

import sqlfe.general.*;

public class TestColumnCount implements ISQLTest {
	// default constructor
	public TestColumnCount() {
		
	}
	
	// sqlTest - from interface
	public TestResult sqlTest (IDAO dao, Query givenQuery, String desiredQueryString) {
		
		int result;						// result on scale 0 to 10
		int thisQColCt = -1;				// column count returned from this query
		int desiredQColCt = -1;			// column count returned from desired query
		ResultSet rset = null;			// result set for SQL query
										// summary of result set's metadata
		ResultSetMetaDataSummary summary = new ResultSetMetaDataSummary();
										// query object for query string
		Query desiredQuery = new Query(desiredQueryString);
	
		// 1) execute given query, get column count for this query's result set
		//dao.connect();

		rset = dao.executeSQLQuery(givenQuery.toString());
		summary = dao.processResultSet(rset);
		thisQColCt = summary.getNumCols();
		
		//dao.disconnect();
		rset = null;
		summary = null;
			
		// 2) execute desired query, get counts for this query
		//dao.connect();

		rset = dao.executeSQLQuery(desiredQuery.toString());
		summary = dao.processResultSet(rset);
		desiredQColCt = summary.getNumCols();
		
		//dao.disconnect();
		rset = null;
		summary = null;
		
		if (thisQColCt >= 0 && (thisQColCt == desiredQColCt)) {
			result = 10;
		}
		else {
			result = 0;
		};
		
		return new TestResult(result);

	}	// end - method sqlTest
	
	// getName - from interface
	public String getName() {
		return ("TestColumnCount");
	}
	
	public String getDesc() {
		return "Answer has same column count as desired result query";
	}
}	// end - class TestColumnCount
