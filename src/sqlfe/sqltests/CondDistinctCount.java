/*
 * CondDistinctCount - class to evaluate condition for query 'distinct' count
 * 
 * Created - Paul J. Wagner, 1-Oct-2018
 */
package sqlfe.sqltests;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import sqlfe.general.IDAO;
import sqlfe.general.Query;
import sqlfe.general.TestResult;
import sqlfe.util.Utilities;

public class CondDistinctCount implements ISQLTest {
	public TestResult sqlTest (IDAO dao, Query givenQuery, String condition) {
		int result;						// result on scale 0 to 10
		int thisDistinctCt = -1;		// distinct count returned from this query
		boolean compResult = false;		// result of condition evaluation
		
		// count number of selects in query
		thisDistinctCt = Utilities.countMatches(givenQuery.toString(), "DISTINCT ") +
						 Utilities.countMatches(givenQuery.toString(), "DISTINCT(") +
		 				 Utilities.countMatches(givenQuery.toString(), "DISTINCT\r\n") +
		 				 Utilities.countMatches(givenQuery.toString(), "DISTINCT\n");
		 
		// build full condition from string condition
		String fullCondition = (thisDistinctCt + condition);
		
		// evaluate full condition
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		try {
			compResult = (Boolean)engine.eval(fullCondition);
		}
		catch (ScriptException se) {
			System.err.println("CondDistinctCount - cannot evaluate condition");
		}
		
		// compare and generate result
		result = compResult ? 10 : 0;
		
		return new TestResult(result);
	}	// end - condDistinctCount
	
	public String getName() {
		return "CondDistinctCount";
	}
	
	public String getDesc() {
		return "Answer has appropriate number of DISTINCT keyword";
	}
}	// end - class CondDistinctCount
