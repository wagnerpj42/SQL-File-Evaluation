/*
 * TestRowCount - class to test for query row count equality
 * 
 * Created - Paul J. Wagner, 10/30/2017, refactored out of Query class
 */
package sqlfe.sqltests;

import java.sql.ResultSet;

import sqlfe.general.*;

public class TestRowCount implements ISQLTest {
	// default constructor
	public TestRowCount() {
		
	}
	
	// sqlTest - from interface
	public TestResult sqlTest (IDAO dao, Query givenQuery, String desiredQueryString) {
		int result;						// result from 0 (wrong) to 10 (exactly right)
		int thisQRowCt = -1;			// row count returned from this query
		int desiredQRowCt = -1;			// row count returned from desired query
		ResultSet rset = null;			// an SQL result set
										// summary of result set's metadata
		ResultSetMetaDataSummary summary = new ResultSetMetaDataSummary();
										// query object for string
		Query desiredQuery = new Query(desiredQueryString);
	
		// 1) execute given query, get counts for this query
		rset = dao.executeSQLQuery(givenQuery.toString());
		summary = dao.processResultSet(rset);
		thisQRowCt = summary.getNumRows();
		
		rset = null;
		summary = null;
		
		// 2) execute desired query, get counts for this query
		rset = dao.executeSQLQuery(desiredQuery.toString());
		summary = dao.processResultSet(rset);
		desiredQRowCt = summary.getNumRows();
		
		rset = null;
		summary = null;
		
		//System.out.println("thisQRowCt: " + thisQRowCt);
		//System.out.println("desiredQRowCt: " + desiredQRowCt);
		
		if (thisQRowCt >= 0 && (thisQRowCt == desiredQRowCt)) {
			result = 10;
		} 
		else if (thisQRowCt >= 0 && (Math.abs(thisQRowCt - desiredQRowCt) == 1)) {
			result = 5;
		}
		else {
			result = 0;
		}
		
		return new TestResult(result);

	}	// end - method sqlTest
	
	// getName - from interface
	public String getName() {
		return ("TestRowCount");
	}
	
	public String getDesc() {
		return "Answer has same row count as desired query";
	}
}	// end - class TestRowCount
