/*
 * CondSelectCount - class to evaluate condition for query 'select' count
 * 
 * Created - Paul J. Wagner, 1-Oct-2018
 */
package edu.uwec.cs.wagnerpj.sqltest.sqltests;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import edu.uwec.cs.wagnerpj.sqltest.general.Query;
import edu.uwec.cs.wagnerpj.sqltest.util.Utilities;
import edu.uwec.cs.wagnerpj.sqltest.general.IDAO;

public class CondWhereCount implements ISQLTest {
	public int sqlTest (IDAO dao, Query givenQuery, String condition) {
		int result;						// result on scale 0 to 10
		int thisWhereCt = -1;			// where count returned from this query
		boolean compResult = false;		// result of condition evaluation
		
		// count number of selects in query
		thisWhereCt = Utilities.countMatches(givenQuery.toString(), "WHERE");
		//System.out.println("CondWhereCount thisWhereCt = " + thisWhereCt);
		
		// build full condition from string condition
		String fullCondition = (thisWhereCt + condition);
		//System.out.println("CondSelectCount full condition: >" + fullCondition + "<");
		
		// evaluate full condition
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		try {
			compResult = (Boolean)engine.eval(fullCondition);
		}
		catch (ScriptException se) {
			System.err.println("CondWhereCount - cannot evaluate condition");
		}
		
		// compare and generate result
		result = compResult ? 10 : 0;
		
		return result;
	}	// end - condWhereCount
	
	public String getName() {
		return "CondWhereCount";
	}
	
	public String getDesc() {
		return "Answer has appropriate number of WHERE keyword";
	}
}	// end - class CondWhereCount
