/*
 * ISQLTest - interface for SQL tests
 * 
 * Created - Paul J. Wagner, 10/30/2017
 */
package edu.uwec.cs.wagnerpj.sqltest.sqltests;

import edu.uwec.cs.wagnerpj.sqltest.general.*;

public interface ISQLTest {
	// methods
	// -- sqlTest() - run a specific test
	public abstract TestResult sqlTest(IDAO dao, Query givenQuery, String testString);
	
	// -- getName() - return the test's name
	public abstract String getName();
	
	// --- getDesc() - return the test's description
	public abstract String getDesc();
	
}	// end - interface ISQLTest
