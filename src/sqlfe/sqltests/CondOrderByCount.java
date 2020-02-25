/*
 * CondOrderByCount - class to evaluate condition for query 'order by' count
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

public class CondOrderByCount implements ISQLTest {
	public TestResult sqlTest (IDAO dao, Query givenQuery, String condition) {
		int result;						// result on scale 0 to 10
		int thisOrderByCt = -1;			// order by count returned from this query
		boolean compResult = false;		// result of condition evaluation
		
		// count number of group bys in query
		thisOrderByCt = Utilities.countMatches(givenQuery.toString(), "ORDER BY ") +
					    Utilities.countMatches(givenQuery.toString(), "ORDER BY(") +
					    Utilities.countMatches(givenQuery.toString(), "ORDER BY\r\n") +
					    Utilities.countMatches(givenQuery.toString(), "ORDER BY\n");
		
		// build full condition from string condition
		String fullCondition = (thisOrderByCt + condition);
		
		// evaluate full condition
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		try {
			compResult = (Boolean)engine.eval(fullCondition);
		}
		catch (ScriptException se) {
			System.err.println("CondOrderByCount - cannot evaluate condition");
		}
		
		// compare and generate result
		result = compResult ? 10 : 0;
		
		return new TestResult(result);
	}	// end - condOrderByCount
	
	public String getName() {
		return "CondOrderByCount";
	}
	
	public String getDesc() {
		return "Answer has appropriate number of ORDER BY keywords";
	}
}	// end - class CondOrderByCount
