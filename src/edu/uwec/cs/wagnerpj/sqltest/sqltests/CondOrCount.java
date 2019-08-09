/*
 * CondOrCount - class to evaluate condition for query OR count
 * 
 * Created - Paul J. Wagner, 08-Aug-2019
 */
package edu.uwec.cs.wagnerpj.sqltest.sqltests;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import edu.uwec.cs.wagnerpj.sqltest.general.Query;
import edu.uwec.cs.wagnerpj.sqltest.util.Utilities;

public class CondOrCount implements ISQLTest {
	public int sqlTest (Query givenQuery, String condition) {
		int result;						// result on scale 0 to 10
		int thisOrCt = -1;				// count of OR phrases returned from this query
		boolean compResult = false;		// result of condition evaluation
		
		// count number of selects in query
		thisOrCt = Utilities.countMatches(givenQuery.toString(), "OR");
		
		// build full condition from string condition
		String fullCondition = (thisOrCt + condition);
		//System.out.println("CondAndCount full condition: >" + fullCondition + "<");
		
		// evaluate full condition
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		try {
			compResult = (Boolean)engine.eval(fullCondition);
		}
		catch (ScriptException se) {
			System.err.println("CondOrCount - cannot evaluate condition");
		}
		
		// compare and generate result
		result = compResult ? 10 : 0;
		
		return result;
	}	// end - condOrCount
	
	public String getName() {
		return "CondOrCount";
	}
	
	public String getDesc() {
		return "Answer has appropriate number of OR keyword";
	}
}	// end - class CondOrCount
