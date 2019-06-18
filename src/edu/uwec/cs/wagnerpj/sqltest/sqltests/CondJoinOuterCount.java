/*
 * CondJoinOuterCount - class to evaluate condition for count of explicit SQL-99 outer joins
 * 
 * Created - Paul J. Wagner, 20-Feb-2019
 */
package edu.uwec.cs.wagnerpj.sqltest.sqltests;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import edu.uwec.cs.wagnerpj.sqltest.general.Query;
import edu.uwec.cs.wagnerpj.sqltest.util.Utilities;

public class CondJoinOuterCount implements ISQLTest {
	public int sqlTest (Query givenQuery, String condition) {
		int result;						// result on scale 0 to 10
		int thisJoinCt = -1;			// explicit join count in this query
		boolean compResult = false;		// result of condition evaluation
				
		// count number of explicit JOINs in query
		thisJoinCt = Utilities.countMatches(givenQuery.toString(), "OUTER JOIN");
		//System.out.println("CondJoinOuterCount-join count is: " + thisJoinCt);	
		//System.out.println();
		thisJoinCt += Utilities.countMatches(givenQuery.toString(), "LEFT JOIN");
		thisJoinCt += Utilities.countMatches(givenQuery.toString(), "RIGHT JOIN");
		thisJoinCt += Utilities.countMatches(givenQuery.toString(), "FULL JOIN");

		// build full condition from string condition
		String fullCondition = (thisJoinCt + condition);
		
		// evaluate full condition
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		try {
			compResult = (Boolean)engine.eval(fullCondition);
		}
		catch (ScriptException se) {
			System.err.println("CondJoinOuterCount - cannot evaluate condition");
		}
		
		// compare and generate result
		result = compResult ? 10 : 0;
		
		return result;
	}	// end - method sqlTest
	
	public String getName() {
		return "CondJoinOuterCount";
	}
	
	public String getDesc() {
		return "Answer has appropriate number of OUTER/LEFT/RIGHT/BOTH keywords";
	}
}	// end - class CondJoinOuterCount
