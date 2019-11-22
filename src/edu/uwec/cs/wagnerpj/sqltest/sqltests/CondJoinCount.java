/*
 * CondJoinCount - class to evaluate condition for count of joins
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

public class CondJoinCount implements ISQLTest {
	public int sqlTest (IDAO dao, Query givenQuery, String condition) {
		int result;						// result on scale 0 to 10
		int thisCommaCt = -1;			// comma count in this query
		int thisJoinCt = -1;			// explicit join count in this query
		int thisTotalJoinCt = -1;		// total join count in this query
		boolean compResult = false;		// result of condition evaluation
		
		// count number of commas in query
		String reducedString = Utilities.removeSelectFroms(givenQuery.toString());
		thisCommaCt = Utilities.countMatches(reducedString, ",");
		//System.out.println("CondJoinCount-comma count is: " + thisCommaCt);
		
		// count number of explicit JOINs in query
		thisJoinCt = Utilities.countMatches(givenQuery.toString(), "JOIN");
		//System.out.println("CondTableCount-join count is: " + thisJoinCt);	
		//System.out.println();

		// build full condition from string condition
		thisTotalJoinCt = thisCommaCt + thisJoinCt;
		String fullCondition = (thisTotalJoinCt + condition);
		
		// evaluate full condition
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		try {
			compResult = (Boolean)engine.eval(fullCondition);
		}
		catch (ScriptException se) {
			System.err.println("CondJoinCount - cannot evaluate condition");
		}
		
		// compare and generate result
		result = compResult ? 10 : 0;
		
		return result;
	}	// end - condJoinCount
	
	public String getName() {
		return "CondJoinCount";
	}
	
	public String getDesc() {
		return "Answer has appropriate number of join operators (comma or JOIN/ON)";
	}
}	// end - class CondJoinCount
