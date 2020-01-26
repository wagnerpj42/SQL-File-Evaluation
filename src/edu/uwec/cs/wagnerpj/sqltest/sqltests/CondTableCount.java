/*
 * CondTableCount - class to evaluate condition for query table count
 * 
 * Created - Paul J. Wagner, 1-Oct-2018
 */
package edu.uwec.cs.wagnerpj.sqltest.sqltests;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import edu.uwec.cs.wagnerpj.sqltest.general.Query;
import edu.uwec.cs.wagnerpj.sqltest.general.TestResult;
import edu.uwec.cs.wagnerpj.sqltest.util.Utilities;
import edu.uwec.cs.wagnerpj.sqltest.general.IDAO;

public class CondTableCount implements ISQLTest {
	// TODO: problem - counting commas gets commas in TO_DATE(), TO_CHAR(), and other functions; temporarily removed
	public TestResult sqlTest (IDAO dao, Query givenQuery, String condition) {
		int result;						// result on scale 0 to 10
		int thisFromCt = -1;			// from count in this query
		//int thisCommaCt = -1;			// comma count in this query
		int thisJoinCt = -1;			// explicit join count in this query
		int thisTableCt = -1;			// total table count in this query
		boolean compResult = false;		// result of condition evaluation
		
		// count number of froms in query
		thisFromCt = Utilities.countMatches(givenQuery.toString(), "FROM");
		//System.out.println("CondTableCount-from count is : " + thisFromCt);
	
		// count number of commas in query
		//String reducedString = Utilities.removeSelectFroms(givenQuery.toString());
		//thisCommaCt = Utilities.countMatches(reducedString, ",");
		//System.out.println("CondTableCount-comma count is: " + thisCommaCt);
		
		// count number of JOINs in query
		thisJoinCt = Utilities.countMatches(givenQuery.toString(), "JOIN");
		//System.out.println("CondTableCount-join count is: " + thisJoinCt);	
		//System.out.println();

		// build full condition from string condition
		thisTableCt = thisFromCt + thisJoinCt;				// + thisCommaCt if fix
		String fullCondition = (thisTableCt + condition);
		
		// evaluate full condition
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		try {
			compResult = (Boolean)engine.eval(fullCondition);
		}
		catch (ScriptException se) {
			System.err.println("CondFromCount - cannot evaluate condition");
		}
		
		// compare and generate result
		result = compResult ? 10 : 0;
		
		return new TestResult(result);
	}	// end - condTableCount
	
	public String getName() {
		return "CondTableCount";
	}
	
	public String getDesc() {
		return "Answer has appropriate number of tables used";
	}
}	// end - class CondTableCount
