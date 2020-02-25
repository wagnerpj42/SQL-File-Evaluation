/*
 * CondFromCount - class to evaluate condition for query 'from' count
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

public class CondFromCount implements ISQLTest {
	public TestResult sqlTest (IDAO dao, Query givenQuery, String condition) {
		int result;						// result on scale 0 to 10
		int thisFromCt = -1;			// from count returned from this query
		boolean compResult = false;		// result of condition evaluation
		
		// count number of froms in query
		thisFromCt = Utilities.countMatches(givenQuery.toString(), "FROM ") +
					 Utilities.countMatches(givenQuery.toString(), "FROM(") +
					 Utilities.countMatches(givenQuery.toString(), "FROM\n") +
					 Utilities.countMatches(givenQuery.toString(), "FROM\r\n");
		
		// build full condition from string condition
		String fullCondition = (thisFromCt + condition);
		
		// evaluate full condition
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		try {
			compResult = (Boolean)engine.eval(fullCondition);
		}
		catch (ScriptException se) {
			System.err.println("CondFromCount - cannot evaluate condition");
		}
		
		// compare and generate result
		result = compResult ? 10 : 0;
		
		return new TestResult(result);
	}	// end - condFromCount
	
	public String getName() {
		return "CondFromCount";
	}
	
	public String getDesc() {
		return "Answer has appropriate number of FROM keyword";
	}
}	// end - class CondFromCount
