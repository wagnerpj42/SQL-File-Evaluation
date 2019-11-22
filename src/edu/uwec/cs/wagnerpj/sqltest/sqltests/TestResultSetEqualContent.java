/*
 * TestResultSetEqualContent - class to test for result set equality regardless of row/column order
 * NOTE: currently testing row ct., column ct., and result string length.  
 *       Cannot directly test column names because people may change names with various aliases
 * 
 * Created - Paul J. Wagner, 2-Oct-2018
 */
package edu.uwec.cs.wagnerpj.sqltest.sqltests;

import java.sql.ResultSet;

import edu.uwec.cs.wagnerpj.sqltest.general.*;

public class TestResultSetEqualContent implements ISQLTest {
	// default constructor
	public TestResultSetEqualContent() {
		// nothing at this time
	}
	
	// sqlTest - from interface
	// TODO: need to make better test; epsilon value can be exceeded by how columns are formatted and displayed, so temporarily removed
	public int sqlTest (IDAO dao, Query givenQuery, String desiredQueryString) {
		int result = 0;						// result on scale 0 to 10
		String givenResultString = "";		// result set string returned from given query
		String desiredResultString = "";	// result set string returned from desired query
		ResultSet rset = null;				// result set for SQL query
											// summary of result set's metadata
		ResultSetMetaDataSummary summary = new ResultSetMetaDataSummary();
											// query for string
		Query desiredQuery = new Query(desiredQueryString);
		
		int givenRowCt = 0; 				// temp holders for given query results
		int givenColCt = 0;
		//String givenColSet = null;		
		int desiredRowCt = 0; 				// temp holders for desired query results
		int desiredColCt = 0;
		//String desiredColSet = null;
		final int epsilonPerLine = 2;		// epsilon for each line in result string
		int epsilon = 0;					// epsilon for overall result string length comparison
		
		// 1) execute given query, get result set string for given query
		dao.connect();

		try {
			rset = dao.executeSQLQuery(givenQuery.toString());
			summary = dao.processResultSet(rset);
			givenResultString = summary.getResultString();
		} catch (Exception e) {
			System.err.println("Error executing SQL/processing result set for given");
			givenResultString = "given_error";
		}

		givenRowCt = summary.getNumRows();
		givenColCt = summary.getNumCols();
		//givenColSet = summary.getColumnSet();
		
		dao.disconnect();
		rset = null;
		summary = null;
		
		// 2) execute desired query, get counts for this query
		dao.connect();
		
		try {
			rset = dao.executeSQLQuery(desiredQuery.toString());
			summary = dao.processResultSet(rset);
			desiredResultString = summary.getResultString();
		} catch (Exception e) {
			System.err.println("Error executing SQL/processing result set for desired");
			desiredResultString = "desired_error";
		}

		desiredRowCt = summary.getNumRows();
		desiredColCt = summary.getNumCols();
		//desiredColSet = summary.getColumnSet();
		
		dao.disconnect();
		rset = null;
		summary = null;
		
		//System.out.println("thisResultString: >" + thisResultString + "<");
		//System.out.println("desiredResultString: >" + desiredResultString + "<"); 
		
		// comparison and setting result
		epsilon = desiredRowCt * epsilonPerLine;		// allow ePL bytes difference per row

		try {
			//System.out.println("desired result string length: " + desiredResultString.length());
			//System.out.println("given   result string length: " + givenResultString.length());
			//if (Math.abs(givenResultString.length() - desiredResultString.length()) <= epsilon &&
			if (
				givenRowCt == desiredRowCt &&
				givenColCt == desiredColCt 
				// && givenColSet.equals(desiredColSet) 
				) {
				result = 10;
			}
			else {
				result = 0;
			}
		}
		catch (NullPointerException npe) {
			System.err.println("TestResultSetEqualContent - sqlTest - submitted query does not generate result string");
		}
		
		return result;

	}	// end - method sqlTest
	
	// getName - from interface
	public String getName() {
		return ("TestResultSetEqualContent");
	}
	
	public String getDesc() {
		return "Answer has same result set content as desired query";
	}
}	// end - class TestResultSetEqualContent
