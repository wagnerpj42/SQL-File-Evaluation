/*
 * CondAvgCount - class to evaluate condition for query 'avg' count
 * 
 * Created - Paul J. Wagner, 31-Mar-2019
 */
package edu.uwec.cs.wagnerpj.sqltest.sqltests;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import edu.uwec.cs.wagnerpj.sqltest.general.Query;
import edu.uwec.cs.wagnerpj.sqltest.util.Utilities;

public class CondAvgCount implements ISQLTest {
	public int sqlTest (Query givenQuery, String condition) {
		int result;						// result on scale 0 to 10
		int thisAvgCt = -1;				// count of AVG phrases returned from this query
		boolean compResult = false;		// result of condition evaluation
		
		// count number of selects in query
		thisAvgCt = Utilities.countMatches(givenQuery.toString(), "AVG");
		
		// build full condition from string condition
		String fullCondition = (thisAvgCt + condition);
		//System.out.println("CondSelectCount full condition: >" + fullCondition + "<");
		
		// evaluate full condition
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		try {
			compResult = (Boolean)engine.eval(fullCondition);
		}
		catch (ScriptException se) {
			System.err.println("CondAvgCount - cannot evaluate condition");
		}
		
		// compare and generate result
		result = compResult ? 10 : 0;
		
		return result;
	}	// end - condAvgCount
	
	public String getName() {
		return "CondAvgCount";
	}
	
	public String getDesc() {
		return "Answer has appropriate number of AVG keyword";
	}
}	// end - class CondAvgCount
