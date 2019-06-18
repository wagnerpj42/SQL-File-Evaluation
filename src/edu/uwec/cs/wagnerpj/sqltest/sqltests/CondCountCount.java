/*
 * CondCountCount - class to evaluate condition for query 'count' count
 * 
 * Created - Paul J. Wagner, 5-Oct-2018
 */
package edu.uwec.cs.wagnerpj.sqltest.sqltests;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import edu.uwec.cs.wagnerpj.sqltest.general.Query;
import edu.uwec.cs.wagnerpj.sqltest.util.Utilities;

public class CondCountCount implements ISQLTest {
	public int sqlTest (Query givenQuery, String condition) {
		int result;						// result on scale 0 to 10
		int thisCountCt = -1;			// count of COUNT phrases returned from this query
		boolean compResult = false;		// result of condition evaluation
		
		// count number of selects in query
		thisCountCt = Utilities.countMatches(givenQuery.toString(), "COUNT");
		
		// build full condition from string condition
		String fullCondition = (thisCountCt + condition);
		//System.out.println("CondSelectCount full condition: >" + fullCondition + "<");
		
		// evaluate full condition
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		try {
			compResult = (Boolean)engine.eval(fullCondition);
		}
		catch (ScriptException se) {
			System.err.println("CondCountCount - cannot evaluate condition");
		}
		
		// compare and generate result
		result = compResult ? 10 : 0;
		
		return result;
	}	// end - condCountCount
	
	public String getName() {
		return "CondCountCount";
	}
	
	public String getDesc() {
		return "Answer has appropriate number of COUNT keyword";
	}
}	// end - class CondCountCount
