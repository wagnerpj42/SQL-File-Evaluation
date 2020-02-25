/*
 * CondMinusCount - class to evaluate condition for query 'minus' count
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

public class CondMinusCount implements ISQLTest {
	public TestResult sqlTest (IDAO dao, Query givenQuery, String condition) {
		int result;						// result on scale 0 to 10
		int thisMinusCt = -1;			// count of MINUS keywords returned from this query
		boolean compResult = false;		// result of condition evaluation
		
		// count number of intersect keywords in query
		thisMinusCt = Utilities.countMatches(givenQuery.toString(), "MINUS ") +
					  Utilities.countMatches(givenQuery.toString(), "MINUS(") +
					  Utilities.countMatches(givenQuery.toString(), "MINUS\r\n") +
					  Utilities.countMatches(givenQuery.toString(), "MINUS\n");
		//System.out.println("count of MINUS is: " + thisMinusCt);
		
		// build full condition from string condition
		String fullCondition = (thisMinusCt + condition);
		//System.out.println("CondSelectCount full condition: >" + fullCondition + "<");
		
		// evaluate full condition
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		try {
			compResult = (Boolean)engine.eval(fullCondition);
		}
		catch (ScriptException se) {
			System.err.println("CondIntersectCount - cannot evaluate condition");
		}
		
		// compare and generate result
		result = compResult ? 10 : 0;
		
		return new TestResult(result);
	}	// end - condMinusCount
	
	public String getName() {
		return "CondMinusCount";
	}
	
	public String getDesc() {
		return "Answer has appropriate number of MINUS keyword";
	}
}	// end - class CondMinusCount
