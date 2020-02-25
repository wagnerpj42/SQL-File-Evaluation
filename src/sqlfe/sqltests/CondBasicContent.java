/*
 * CondBasicContent - class to evaluate condition for query having very basic content
 * 
 * Created - Paul J. Wagner, 2-Oct-2018
 */
package sqlfe.sqltests;

import sqlfe.general.IDAO;
import sqlfe.general.Query;
import sqlfe.general.TestResult;
import sqlfe.util.Utilities;

public class CondBasicContent implements ISQLTest {
	public TestResult sqlTest (IDAO dao, Query givenQuery, String condition) {
		int result;						// result on scale 0 to 10
		int thisSelectCt = -1;			// select count in this query
		int thisFromCt = -1;			// from count in this query
		
		// count number of selects in query
		thisSelectCt = Utilities.countMatches(givenQuery.toString(), "SELECT ") +
		               Utilities.countMatches(givenQuery.toString(), "SELECT*") +
					   Utilities.countMatches(givenQuery.toString(), "SELECT\r\n") +
					   Utilities.countMatches(givenQuery.toString(), "SELECT\n");
		//System.out.println("CondBasicContent-select count is: " + thisSelectCt);
		
		// count number of froms in query
		thisFromCt = Utilities.countMatches(givenQuery.toString(), "FROM ") +
		             Utilities.countMatches(givenQuery.toString(), "FROM(") +
		             Utilities.countMatches(givenQuery.toString(), "FROM\r\n") +
        			 Utilities.countMatches(givenQuery.toString(), "FROM\n");
		//System.out.println("CondBasicContent-from count is : " + thisFromCt);
		
		// compare and generate result
		result = (thisSelectCt >= 1 && thisFromCt >= 1) ? 10 : 0;
		
		return new TestResult(result);
	}	// end - condBasicContent
	
	public String getName() {
		return "CondBasicContent";
	}
	
	public String getDesc() {
		return "Answer has at least SELECT and FROM";
	}
	
}	// end - class CondBasicContent
