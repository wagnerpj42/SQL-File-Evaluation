/*
 * CondJoinImplicitCount - class to evaluate condition for count of implicit SQL-92 joins with comma-separated tables
 * 
 * Created - Paul J. Wagner, 19-Feb-2020
 */
package sqlfe.sqltests;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import sqlfe.general.IDAO;
import sqlfe.general.Query;
import sqlfe.general.TestResult;
import sqlfe.util.QueryParseUtil;
import sqlfe.util.Utilities;

public class CondJoinImplicitCount implements ISQLTest {
	public TestResult sqlTest (IDAO dao, Query givenQuery, String condition) {
		int result;						// result on scale 0 to 10
		int thisJoinCt = -1;			// implicit join count in this query
		boolean compResult = false;		// result of condition evaluation
				
		// count number of implicit JOINs in query by counting number of commas in FROM to (JOIN OR WHERE or end)
		String reducedString = QueryParseUtil.identifyFromToJoinOrWhere(givenQuery.toString());
		thisJoinCt = Utilities.countMatches(reducedString, ",");

		// build full condition from string condition
		String fullCondition = (thisJoinCt + condition);
		
		// evaluate full condition
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		try {
			compResult = (Boolean)engine.eval(fullCondition);
		}
		catch (ScriptException se) {
			System.err.println("CondJoinImplicitCount - cannot evaluate condition");
		}
		
		// compare and generate result
		result = compResult ? 10 : 0;
		
		return new TestResult(result);
	}	// end - method sqlTest
	
	public String getName() {
		return "CondJoinImplicitCount";
	}
	
	public String getDesc() {
		return "Answer has appropriate number of implicit JOIN operations";
	}
}	// end - class CondJoinImplicitCount
