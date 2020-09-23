/*
 * CondCompiles - class to evaluate whether query compiles without errors
 * 
 * Created - Paul J. Wagner, 1-Oct-2018
 */
package sqlfe.sqltests;

import java.sql.ResultSet;

import sqlfe.general.IDAO;
import sqlfe.general.Query;
import sqlfe.general.TestResult;

public class CondCompiles implements ISQLTest {
	public TestResult sqlTest (IDAO dao, Query givenQuery, String condition) {
		int result;						// result on scale 0 to 10
		boolean compResult = false;		// result of query compilation evaluation
		
		ResultSet rset = null;			// result set for SQL query
		
		// try to execute given query, see result
		rset = dao.executeSQLQuery(givenQuery.toString());

		// NOTE: no need to evaluate condition here; all based on query and result set itself
		
		// test if rset is null, meaning query didn't return one because it couldn't compile/execute
		if (rset == null) {
			compResult = false;
		}
		else {
			compResult = true;
		}
				
		// compare and generate result
		result = compResult ? 10 : 0;
		
		return new TestResult(result);
	}	// end - condCompiles
	
	public String getName() {
		return "CondCompiles";
	}
	
	public String getDesc() {
		return "Answer compiles without errors";
	}
	
}	// end - class CondCompiles
