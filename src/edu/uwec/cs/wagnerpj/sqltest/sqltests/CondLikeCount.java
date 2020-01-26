/*
 * CondLikeCount - class to evaluate condition for query LIKE count
 * 
 * Created - Paul J. Wagner, 03-Apr-2019
 */
package edu.uwec.cs.wagnerpj.sqltest.sqltests;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import edu.uwec.cs.wagnerpj.sqltest.general.Query;
import edu.uwec.cs.wagnerpj.sqltest.general.TestResult;
import edu.uwec.cs.wagnerpj.sqltest.util.Utilities;
import edu.uwec.cs.wagnerpj.sqltest.general.IDAO;

public class CondLikeCount implements ISQLTest {
	public TestResult sqlTest (IDAO dao, Query givenQuery, String condition) {
		int result;						// result on scale 0 to 10
		int thisLikeCt = -1;			// count of LIKE phrases returned from this query
		boolean compResult = false;		// result of condition evaluation
		
		// count number of selects in query
		thisLikeCt = Utilities.countMatches(givenQuery.toString(), "LIKE");
		
		// build full condition from string condition
		String fullCondition = (thisLikeCt + condition);
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
	}	// end - condAvgCount
	
	public String getName() {
		return "CondLikeCount";
	}
	
	public String getDesc() {
		return "Answer has appropriate number of LIKE keyword";
	}
}	// end - class CondLikeCount
