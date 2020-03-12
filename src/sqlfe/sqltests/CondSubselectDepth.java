/*
 * CondSubselectDepthCount - class to evaluate condition for subselect depth count
 * 
 * Created - Ryan Vaughan, January 2020
 */
package sqlfe.sqltests;

import sqlfe.general.IDAO;
import sqlfe.general.Query;
import sqlfe.general.TestResult;
import sqlfe.util.QueryParseUtil;

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
	}	// end - method sqlTest

	// calculateSubselectDepth - recursively calculate the subselect depth for the given query or part of query
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
	}	// end - method calculateSubselectDepth

	// getName - return the test name
	public String getName() {
		return "CondSubselectDepth";
	}
	
	// getDesc - return the test description
	public String getDesc() {
		return "Answer has appropriate maximum subselect query depth";
	}

}	// end - class CondSubselectDepthCount


