/*
 * CondUnionCount - class to evaluate condition for query UNION count
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

public class CondUnionCount implements ISQLTest {
	public TestResult sqlTest (IDAO dao, Query givenQuery, String condition) {
		int result;						// result on scale 0 to 10
		int thisUnionCt = -1;			// count of UNION phrases returned from this query
		boolean compResult = false;		// result of condition evaluation
		
		// count number of selects in query
		thisUnionCt = Utilities.countMatches(givenQuery.toString(), "UNION ") +
					  Utilities.countMatches(givenQuery.toString(), "UNION(") +
					  Utilities.countMatches(givenQuery.toString(), "UNION\r\n") +
					  Utilities.countMatches(givenQuery.toString(), "UNION\n");
		
		// build full condition from string condition
		String fullCondition = (thisUnionCt + condition);
		//System.out.println("CondLikeCount full condition: >" + fullCondition + "<");
		
		// evaluate full condition
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		try {
			compResult = (Boolean)engine.eval(fullCondition);
		}
		catch (ScriptException se) {
			System.err.println("CondLikeCount - cannot evaluate condition");
		}
		
		// compare and generate result
		result = compResult ? 10 : 0;
		
		return new TestResult(result);
	}	// end - condUnionCount
	
	public String getName() {
		return "CondUnionCount";
	}
	
	public String getDesc() {
		return "Answer has appropriate number of UNION keyword";
	}
}	// end - class CondUnionCount
