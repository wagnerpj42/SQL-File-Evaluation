/*
 * CondMinCount - class to evaluate condition for query 'min' count
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

public class CondMinCount implements ISQLTest {
	public TestResult sqlTest (IDAO dao, Query givenQuery, String condition) {
		int result;						// result on scale 0 to 10
		int thisMinCt = -1;				// count of MIN keywords returned from this query
		boolean compResult = false;		// result of condition evaluation
		
		// count number of min keywords in query
		thisMinCt = Utilities.countMatches(givenQuery.toString(), "MIN ") +
					Utilities.countMatches(givenQuery.toString(), "MIN(") +
					Utilities.countMatches(givenQuery.toString(), "MIN\r\n") +
					Utilities.countMatches(givenQuery.toString(), "MIN\n");
		//System.out.println("count of MINs is: " + thisMinCt);
		
		// build full condition from string condition
		String fullCondition = (thisMinCt + condition);
		//System.out.println("CondSelectCount full condition: >" + fullCondition + "<");
		
		// evaluate full condition
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		try {
			compResult = (Boolean)engine.eval(fullCondition);
		}
		catch (ScriptException se) {
			System.err.println("CondMinCount - cannot evaluate condition");
		}
		
		// compare and generate result
		result = compResult ? 10 : 0;
		
		return new TestResult(result);
	}	// end - condMinCount
	
	public String getName() {
		return "CondMinCount";
	}
	
	public String getDesc() {
		return "Answer has appropriate number of MIN keyword";
	}
}	// end - class CondMinCount
