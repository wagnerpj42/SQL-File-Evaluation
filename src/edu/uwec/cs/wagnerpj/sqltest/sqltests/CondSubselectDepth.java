/*
 * CondJoinCount - class to evaluate condition for count of joins
 * 
 * Created - Paul J. Wagner, 1-Oct-2018
 */
package edu.uwec.cs.wagnerpj.sqltest.sqltests;

import edu.uwec.cs.wagnerpj.sqltest.general.IDAO;
import edu.uwec.cs.wagnerpj.sqltest.general.Query;
import edu.uwec.cs.wagnerpj.sqltest.general.TestResult;
import edu.uwec.cs.wagnerpj.sqltest.util.QueryParseUtil;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Map;

public class CondSubselectDepth implements ISQLTest {
	public TestResult sqlTest (IDAO dao, Query givenQuery, String condition) {
		int result = 10;

		int subselectDepth = calculateSubselectDepth(givenQuery.toString(), 1);

		boolean compResult = false;
		String fullCondition = subselectDepth + condition;
		// evaluate full condition
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		try {
			compResult = (Boolean)engine.eval(fullCondition);
		}
		catch (ScriptException se) {
			System.err.println("CondSubselectDepth - cannot evaluate condition");
		}

		result = compResult ? 10 : 0;

		
		return new TestResult(result);
	}	// end - condJoinCount

	public static int calculateSubselectDepth(String query, int currentDepth){

		//Select FROM to end of the query
		String fromToEnd = QueryParseUtil.identifyFromToEnd(query);

		//Find all subselect statements one level below this one.
		Map<String, String> subselects = QueryParseUtil.identifySubSelectStatements(fromToEnd);

		//find which subselect statement has greatest depth. Return that depth to calling function.
		int maxSubselectDepth = currentDepth;
		for(String subselect : subselects.keySet()){
			int subselectDepth = calculateSubselectDepth(subselect, currentDepth + 1);
			if(subselectDepth > maxSubselectDepth) maxSubselectDepth = subselectDepth;
		}
		return maxSubselectDepth;
	}	// end - method calculate SubselectDepth

	public String getName() {
		return "CondSubselectDepth";
	}
	
	public String getDesc() {
		return "Answer has appropriate maximum subselect statement depth";
	}
}	// end - class CondJoinCount


