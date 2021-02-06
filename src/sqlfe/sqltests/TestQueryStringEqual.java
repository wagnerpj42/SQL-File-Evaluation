/*
 * TestQueryStringEqual - class to test for query string equality
 * 
 * Created - Paul J. Wagner, 10/30/2017, refactored out of Query class
 */
package sqlfe.sqltests;

import sqlfe.general.*;

public class TestQueryStringEqual implements ISQLTest {
	// default constructor
	public TestQueryStringEqual() {
		
	}
	
	// sqlTest - from interface
	public TestResult sqlTest (IDAO dao, Query givenQuery, String desiredQueryString) {
		int result;			// result on scale 0 to 10
		
		if (givenQuery.toString().toLowerCase().equals(desiredQueryString.toLowerCase() )) {
			result = 10;
		}
		else {
			result = 0;
		}
		return new TestResult(result);
	}

	// getName - from interface
	public String getName() {
		return ("TestQueryStringEqual");
	}
	
	public String getDesc() {
		return "Answer has same query string as desired query";
	}
}	// end - class TestQueryStringEqual
