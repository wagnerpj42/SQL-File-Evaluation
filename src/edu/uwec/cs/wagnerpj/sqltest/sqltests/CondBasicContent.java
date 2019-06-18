/*
 * CondBasicContent - class to evaluate condition for query having very basic content
 * 
 * Created - Paul J. Wagner, 2-Oct-2018
 */
package edu.uwec.cs.wagnerpj.sqltest.sqltests;

import edu.uwec.cs.wagnerpj.sqltest.general.Query;
import edu.uwec.cs.wagnerpj.sqltest.util.Utilities;

public class CondBasicContent implements ISQLTest {
	public int sqlTest (Query givenQuery, String condition) {
		int result;						// result on scale 0 to 10
		int thisSelectCt = -1;			// select count in this query
		int thisFromCt = -1;			// from count in this query
		
		// count number of selects in query
		thisSelectCt = Utilities.countMatches(givenQuery.toString(), "SELECT");
		//System.out.println("CondBasicContent-select count is: " + thisCommaCt);
		
		// count number of froms in query
		thisFromCt = Utilities.countMatches(givenQuery.toString(), "FROM");
		//System.out.println("CondBasicContent-from count is : " + thisFromCt);
		
		// compare and generate result
		result = (thisSelectCt >= 1 && thisFromCt >= 1) ? 10 : 0;
		
		return result;
	}	// end - condBasicContent
	
	public String getName() {
		return "CondBasicContent";
	}
	
	public String getDesc() {
		return "Answer has at least SELECT and FROM";
	}
	
}	// end - class CondBasicContent
