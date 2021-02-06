/*
 * CondNumLinesCount - class to evaluate condition for number of lines
 * 
 * Created - Paul J. Wagner, 03-Apr-2019
 */
package sqlfe.sqltests;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import sqlfe.general.IDAO;
import sqlfe.general.Query;
import sqlfe.general.TestResult;
import sqlfe.util.Utilities;

public class CondNumLinesCount implements ISQLTest {
	public TestResult sqlTest (IDAO dao, Query givenQuery, String condition) {
		int result;						// result on scale 0 to 10
		int thisLinesCt = -1;			// count of LIKE phrases returned from this query
		boolean compResult = false;		// result of condition evaluation
		
		// count number of selects in query
		thisLinesCt = Utilities.countMatches(givenQuery.toString(), "\n");
		thisLinesCt += 1;				// one more line than line break
		//System.out.println("line count is: " + thisLinesCt);
		
		// build full condition from string condition
		String fullCondition = (thisLinesCt + condition);
		//System.out.println("CondNumLinesCount full condition: >" + fullCondition + "<");
		
		// evaluate full condition
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		try {
			compResult = (Boolean)engine.eval(fullCondition);
		}
		catch (ScriptException se) {
			System.err.println("CondNumLinesCount - cannot evaluate condition");
		}
		
		// compare and generate result
		result = compResult ? 10 : 0;
		
		return new TestResult(result);
	}	// end - condNumLinesCount
	
	public String getName() {
		return "CondNumLinesCount";
	}
	
	public String getDesc() {
		return "Answer is broken into appropriate number of lines";
	}
}	// end - class CondNumLinesCount
