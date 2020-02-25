/*
 * CondAndCount - class to evaluate condition for query AND count
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

public class CondAndCount implements ISQLTest {
	public TestResult sqlTest (IDAO dao, Query givenQuery, String condition) {
		int result;						// result on scale 0 to 10
		int thisAndCt = -1;				// count of AND phrases returned from this query
		boolean compResult = false;		// result of condition evaluation
		
		// count number of selects in query
		thisAndCt = Utilities.countMatches(givenQuery.toString(), "AND ") +
		            Utilities.countMatches(givenQuery.toString(), "AND(") +
        			Utilities.countMatches(givenQuery.toString(), "AND\r\n") +
        			Utilities.countMatches(givenQuery.toString(), "AND\n");
        
		// build full condition from string condition
		String fullCondition = (thisAndCt + condition);
		//System.out.println("CondAndCount full condition: >" + fullCondition + "<");
		
		// evaluate full condition
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		try {
			compResult = (Boolean)engine.eval(fullCondition);
		}
		catch (ScriptException se) {
			System.err.println("CondAndCount - cannot evaluate condition");
		}
		
		// compare and generate result
		result = compResult ? 10 : 0;
		
		return new TestResult(result);
	}	// end - condAndCount
	
	public String getName() {
		return "CondAndCount";
	}
	
	public String getDesc() {
		return "Answer has appropriate number of AND keyword";
	}
}	// end - class CondAndCount
