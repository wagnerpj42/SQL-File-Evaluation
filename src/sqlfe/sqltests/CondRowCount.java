/*
 * CondRowCount - class to evaluate condition for count of rows
 * 
 * Created - Paul J. Wagner, 20-Feb-2019
 */
package sqlfe.sqltests;

import java.sql.ResultSet;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import sqlfe.general.IDAO;
import sqlfe.general.Query;
import sqlfe.general.ResultSetMetaDataSummary;
import sqlfe.general.TestResult;

public class CondRowCount implements ISQLTest {
	public TestResult sqlTest (IDAO dao, Query givenQuery, String condition) {
		int result;						// result on scale 0 to 10
		boolean compResult = false;		// result of condition evaluation
		
		int thisQRowCt = -1;			// row count returned from this query
		ResultSet rset = null;			// an SQL result set
										// summary of result set's metadata
		ResultSetMetaDataSummary summary = new ResultSetMetaDataSummary();
	
		// execute given query, get row count for this query
		rset = dao.executeSQLQuery(givenQuery.toString());
		summary = dao.processResultSet(rset);
		thisQRowCt = summary.getNumRows();
		
		// clean up from running given query
		rset = null;
		summary = null;

		// build full condition
		String fullCondition = (thisQRowCt + condition);
		
		// evaluate full condition
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		try {
			compResult = (Boolean)engine.eval(fullCondition);
		}
		catch (ScriptException se) {
			System.err.println("CondRowCount - cannot evaluate condition");
		}
		
		// compare and generate result
		result = compResult ? 10 : 0;
		
		return new TestResult(result);
	}	// end - method sqlTest
	
	public String getName() {
		return "CondRowCount";
	}
	
	public String getDesc() {
		return "Answer has appropriate row count";
	}
}	// end - class CondRowCount
