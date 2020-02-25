/*
 * CondMaxCount - class to evaluate condition for query 'max' count
 * 
 * Created - Paul J. Wagner, 31-Mar-2019
 */
package sqlfe.sqltests;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import sqlfe.general.IDAO;
import sqlfe.general.Query;
import sqlfe.general.TestResult;
import sqlfe.util.Utilities;

public class CondMaxCount implements ISQLTest {
	public TestResult sqlTest (IDAO dao, Query givenQuery, String condition) {
		int result;						// result on scale 0 to 10
		int thisMaxCt = -1;				// count of MAX phrases returned from this query
		boolean compResult = false;		// result of condition evaluation
		
		// count number of selects in query
		thisMaxCt = Utilities.countMatches(givenQuery.toString(), "MAX ") +
					Utilities.countMatches(givenQuery.toString(), "MAX(") +
					Utilities.countMatches(givenQuery.toString(), "MAX\r\n") +
					Utilities.countMatches(givenQuery.toString(), "MAX\n");
		
		// build full condition from string condition
		String fullCondition = (thisMaxCt + condition);
		//System.out.println("CondSelectCount full condition: >" + fullCondition + "<");
		
		// evaluate full condition
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		try {
			compResult = (Boolean)engine.eval(fullCondition);
		}
		catch (ScriptException se) {
			System.err.println("CondMaxCount - cannot evaluate condition");
		}
		
		// compare and generate result
		result = compResult ? 10 : 0;
		
		return new TestResult(result);
	}	// end - condMaxCount
	
	public String getName() {
		return "CondMaxCount";
	}
	
	public String getDesc() {
		return "Answer has appropriate number of MAX keyword";
	}
}	// end - class CondMaxCount
