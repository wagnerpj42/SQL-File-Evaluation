/*
 * CondNotCount - class to evaluate condition for query NOT count
 * 
 * Created - Paul J. Wagner, 08-Aug-2019
 */
package sqlfe.sqltests;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import sqlfe.general.IDAO;
import sqlfe.general.Query;
import sqlfe.general.TestResult;
import sqlfe.util.Utilities;

public class CondNotCount implements ISQLTest {
	public TestResult sqlTest (IDAO dao, Query givenQuery, String condition) {
		int result;						// result on scale 0 to 10
		int thisNotCt = -1;				// count of NOT phrases returned from this query
		boolean compResult = false;		// result of condition evaluation
		
		// count number of selects in query
		thisNotCt = Utilities.countMatches(givenQuery.toString(), "NOT ") +
					Utilities.countMatches(givenQuery.toString(), "NOT(") +
					Utilities.countMatches(givenQuery.toString(), "NOT\r\n") +
					Utilities.countMatches(givenQuery.toString(), "NOT\n");
		
		// build full condition from string condition
		String fullCondition = (thisNotCt + condition);
		//System.out.println("CondAndCount full condition: >" + fullCondition + "<");
		
		// evaluate full condition
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		try {
			compResult = (Boolean)engine.eval(fullCondition);
		}
		catch (ScriptException se) {
			System.err.println("CondNotCount - cannot evaluate condition");
		}
		
		// compare and generate result
		result = compResult ? 10 : 0;
		
		return new TestResult(result);
	}	// end - condNotCount
	
	public String getName() {
		return "CondNotCount";
	}
	
	public String getDesc() {
		return "Answer has appropriate number of NOT keyword";
	}
}	// end - class CondNotCount
