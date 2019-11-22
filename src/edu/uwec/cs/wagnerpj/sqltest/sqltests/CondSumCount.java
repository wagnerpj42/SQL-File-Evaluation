/*
 * CondSumCount - class to evaluate condition for query SUM count
 * 
 * Created - Paul J. Wagner, 07-Aug-2019
 */
package edu.uwec.cs.wagnerpj.sqltest.sqltests;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import edu.uwec.cs.wagnerpj.sqltest.general.Query;
import edu.uwec.cs.wagnerpj.sqltest.util.Utilities;
import edu.uwec.cs.wagnerpj.sqltest.general.IDAO;

public class CondSumCount implements ISQLTest {
	public int sqlTest (IDAO dao, Query givenQuery, String condition) {
		int result;						// result on scale 0 to 10
		int thisSumCt = -1;				// count of SUM phrases returned from this query
		boolean compResult = false;		// result of condition evaluation
		
		// count number of selects in query
		thisSumCt = Utilities.countMatches(givenQuery.toString(), "SUM");
		
		// build full condition from string condition
		String fullCondition = (thisSumCt + condition);
		//System.out.println("CondAndCount full condition: >" + fullCondition + "<");
		
		// evaluate full condition
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		try {
			compResult = (Boolean)engine.eval(fullCondition);
		}
		catch (ScriptException se) {
			System.err.println("CondSumCount - cannot evaluate condition");
		}
		
		// compare and generate result
		result = compResult ? 10 : 0;
		
		return result;
	}	// end - condSumCount
	
	public String getName() {
		return "CondSumCount";
	}
	
	public String getDesc() {
		return "Answer has appropriate number of SUM keyword";
	}
}	// end - class CondSumCount
