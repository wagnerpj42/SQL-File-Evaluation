/*
 * CondDescCount - class to evaluate condition for query 'desc(ending)' count
 * 
 * Created - Paul J. Wagner, 20-Feb-2019
 */
package sqlfe.sqltests;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import sqlfe.general.IDAO;
import sqlfe.general.Query;
import sqlfe.general.TestResult;
import sqlfe.util.Utilities;

public class CondDescCount implements ISQLTest {
	public TestResult sqlTest (IDAO dao, Query givenQuery, String condition) {
		int result;						// result on scale 0 to 10
		int thisDescCt = -1;			// DESC count from this query
		boolean compResult = false;		// result of condition evaluation
		
		// count number of DESC or DESCENDING in query
		thisDescCt = Utilities.countMatches(givenQuery.toString(), "DESCENDING ") +
					 Utilities.countMatches(givenQuery.toString(), "DESCENDING,") +
					 Utilities.countMatches(givenQuery.toString(), "DESCENDING)") +
		             Utilities.countMatches(givenQuery.toString(), "DESC ") +
				     Utilities.countMatches(givenQuery.toString(), "DESC,") +
				     Utilities.countMatches(givenQuery.toString(), "DESC)") +
	     			 Utilities.countMatches(givenQuery.toString(), "DESC\r\n") +
	     			 Utilities.countMatches(givenQuery.toString(), "DESC\n");
		// check if DESC at end of query which wouldn't match any of the above
		int queryLength = givenQuery.toString().length();
		if (queryLength >= "desc".length() && givenQuery.toString().toLowerCase().substring(queryLength - "desc".length()).equals("desc")) {
			thisDescCt ++;
		}
		if (queryLength >= "descending".length() && givenQuery.toString().toLowerCase().substring(queryLength - "descending".length()).equals("descending")) {
			thisDescCt ++;
		}
		//System.out.println("thisDescCt is: " + thisDescCt);
		
		// build full condition from string condition
		String fullCondition = (thisDescCt + condition);
		
		// evaluate full condition
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		try {
			compResult = (Boolean)engine.eval(fullCondition);
		}
		catch (ScriptException se) {
			System.err.println("CondDescCount - cannot evaluate condition");
		}
		
		// compare and generate result
		result = compResult ? 10 : 0;
		
		return new TestResult(result);
	}	// end - condOrderByCount
	
	public String getName() {
		return "CondDescCount";
	}
	
	public String getDesc() {
		return "Answer has appropriate number of DESC keyword";
	}
}	// end - class CondDescCount
