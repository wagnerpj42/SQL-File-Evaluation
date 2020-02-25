/*
 * CondTableCount - class to evaluate condition for query table count
 * 
 * Created - Paul J. Wagner, 1-Oct-2018
 */
package sqlfe.sqltests;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import sqlfe.general.IDAO;
import sqlfe.general.Query;
import sqlfe.general.TestResult;
import sqlfe.util.QueryParseUtil;
import sqlfe.util.Utilities;

public class CondTableCount implements ISQLTest {
	public TestResult sqlTest (IDAO dao, Query givenQuery, String condition) {
		int result;						// result on scale 0 to 10
		int thisFromCt = -1;			// from count in this query
		int thisCommaCt = -1;			// comma count in this query
		int thisJoinCt = -1;			// explicit join count in this query
		int thisTableCt = -1;			// total table count in this query
		boolean compResult = false;		// result of condition evaluation
		
		// count number of FROM keywords in query
		thisFromCt = Utilities.countMatches(givenQuery.toString(), "FROM ") +
				 	 Utilities.countMatches(givenQuery.toString(), "FROM(") +
				 	 Utilities.countMatches(givenQuery.toString(), "FROM\n") +
				 	 Utilities.countMatches(givenQuery.toString(), "FROM\r\n");
		//System.out.println("CondTableCount-from count is : " + thisFromCt);
	
		// count number of commas in FROM clause of query, showing old SQL-92 comma joins
		String reducedString = QueryParseUtil.identifyFromToJoinOrWhere(givenQuery.toString());
		thisCommaCt = Utilities.countMatches(reducedString, ",");
		//System.out.println("CondTableCount-comma count is: " + thisCommaCt);
		
		// count number of explicit JOINs in query
		thisJoinCt = Utilities.countMatches(givenQuery.toString(), "JOIN ") +
				 	 Utilities.countMatches(givenQuery.toString(), "JOIN(") +
				 	 Utilities.countMatches(givenQuery.toString(), "JOIN\r\n") +
				 	 Utilities.countMatches(givenQuery.toString(), "JOIN\n");
		//System.out.println("CondTableCount-join count is: " + thisJoinCt);	
		//System.out.println();

		// build full condition from string condition
		thisTableCt = thisFromCt + thisJoinCt + thisCommaCt;
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
