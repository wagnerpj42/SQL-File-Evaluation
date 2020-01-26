/*
 * CondDescCount - class to evaluate condition for query 'desc(ending)' count
 * 
 * Created - Paul J. Wagner, 20-Feb-2019
 */
package edu.uwec.cs.wagnerpj.sqltest.sqltests;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import edu.uwec.cs.wagnerpj.sqltest.general.Query;
import edu.uwec.cs.wagnerpj.sqltest.general.TestResult;
import edu.uwec.cs.wagnerpj.sqltest.util.Utilities;
import edu.uwec.cs.wagnerpj.sqltest.general.IDAO;

public class CondDescCount implements ISQLTest {
	public TestResult sqlTest (IDAO dao, Query givenQuery, String condition) {
		int result;						// result on scale 0 to 10
		int thisDescCt = -1;			// desc(ending) count returned from this query
		boolean compResult = false;		// result of condition evaluation
		
		// count number of group bys in query
		thisDescCt = Utilities.countMatches(givenQuery.toString(), " DESCENDING");
		if (thisDescCt == 0) {
			thisDescCt = Utilities.countMatches(givenQuery.toString(), " DESC");
		}
		
		// build full condition from string condition
		String fullCondition = (thisDescCt + condition);
		
		// evaluate full condition
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		try {
			compResult = (Boolean)engine.eval(fullCondition);
		}
		catch (ScriptException se) {
			System.err.println("CondDescCount - cannot evaluate condition");
		}
		
		// compare and generate result
		result = compResult ? 10 : 0;
		
		return new TestResult(result);
	}	// end - condOrderByCount
	
	public String getName() {
		return "CondDescCount";
	}
	
	public String getDesc() {
		return "Answer has appropriate number of DESC keyword";
	}
}	// end - class CondDescCount
