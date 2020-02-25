/*
 * CondIntersectCount - class to evaluate condition for query 'intersect' count
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

public class CondIntersectCount implements ISQLTest {
	public TestResult sqlTest (IDAO dao, Query givenQuery, String condition) {
		int result;						// result on scale 0 to 10
		int thisIntCt = -1;				// count of INTERSECT keywords returned from this query
		boolean compResult = false;		// result of condition evaluation
		
		// count number of intersect keywords in query
		thisIntCt = Utilities.countMatches(givenQuery.toString(), "INTERSECT ") +
					Utilities.countMatches(givenQuery.toString(), "INTERSECT(") +
					Utilities.countMatches(givenQuery.toString(), "INTERSECT\r\n") +
					Utilities.countMatches(givenQuery.toString(), "INTERSECT\n");
		//System.out.println("count of INTERSECTSs is: " + thisIntCt);
		
		// build full condition from string condition
		String fullCondition = (thisIntCt + condition);
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
	}	// end - condIntersectCount
	
	public String getName() {
		return "CondIntersectCount";
	}
	
	public String getDesc() {
		return "Answer has appropriate number of INTERSECT keyword";
	}
}	// end - class CondIntersectCount
