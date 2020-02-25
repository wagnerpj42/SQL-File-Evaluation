/*
 * CondJoinExplicitCount - class to evaluate condition for count of explicit SQL-99 joins
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

public class CondJoinExplicitCount implements ISQLTest {
	public TestResult sqlTest (IDAO dao, Query givenQuery, String condition) {
		int result;						// result on scale 0 to 10
		int thisJoinCt = -1;			// explicit join count in this query
		boolean compResult = false;		// result of condition evaluation
				
		// count number of explicit JOINs in query
		thisJoinCt = Utilities.countMatches(givenQuery.toString(), "JOIN ") +
				 Utilities.countMatches(givenQuery.toString(), "JOIN(") +
				 Utilities.countMatches(givenQuery.toString(), "JOIN\r\n") +
				 Utilities.countMatches(givenQuery.toString(), "JOIN\n");
		//System.out.println("CondJoinExplicitCount-join count is: " + thisJoinCt);	
		//System.out.println();

		// build full condition from string condition
		String fullCondition = (thisJoinCt + condition);
		
		// evaluate full condition
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		try {
			compResult = (Boolean)engine.eval(fullCondition);
		}
		catch (ScriptException se) {
			System.err.println("CondJoinExplicitCount - cannot evaluate condition");
		}
		
		// compare and generate result
		result = compResult ? 10 : 0;
		
		return new TestResult(result);
	}	// end - method sqlTest
	
	public String getName() {
		return "CondJoinExplicitCount";
	}
	
	public String getDesc() {
		return "Answer has appropriate number of explicit JOIN operations";
	}
}	// end - class CondJoinExplicitCount
