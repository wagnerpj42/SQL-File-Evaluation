/*
 * TestResultSetEqualContent - class to test for result set equality regardless of row/column order
 * NOTE: currently testing row ct., column ct., and result string length.  
 *       Cannot directly test column names because people may change names with various aliases
 * 
 * Created - Paul J. Wagner, 2-Oct-2018
 */
package edu.uwec.cs.wagnerpj.sqltest.sqltests;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import edu.uwec.cs.wagnerpj.sqltest.general.*;


/*											TODO /  CONSIDERATIONS
 *  1) Optimization concerns: 
 *  	-What size result sets are we expected to process in reasonable time? 
 *  	-Exit early conditions. Huge/wrong student answer?
 *  	-Processing result set twice, first time only to get the row count (could use resizing arrays instead to get around this...)
 *  2) How to allocate points? -> Change code to reflect
 *  	-Any unmatched rows at all -> 0 points
 *  	-If only duplicate related errors -> a few points
 *  	-If no errors -> 10 points
 *  3) Supporting different date formats
 *  	-If Date or TimeStamp data type detected, use getDate() and then standardize format into a String
 *  	-If toChar(someDate) used in a query, which formats should be detected/standardized?
 *  4) Other formatting concerns
 *  	-Long decimals. toString() returns scientific notation. Truncate?
 *  	-Any others?
 *  5) Code that doesn't compile is currently interpreted as empty matrix. This could coincidentally be correct answer. Fix.
 *  6) Algorithm isn't 100% foolproof. Will this be an issue? How to fix?
 *  	-EXAMPLES: 	DESIRED: [1, 2]			GIVEN: [1, 2]		<------- considered correct by algorithm
 *  			  			 [3, 4]  			   [4, 3] 				 ideally columns would be required to align
 *  	
 *  				DESIRED: [1, 2]			GIVEN: [1, 2, x]    <------- considered (partially?) correct by algorithm
 *  						 [3, 4]				   [3, x, 4]			 ideally extra column's values wouldn't be considered
 *  
 *					Highly unlikely that the above occurs with rest of results all being correct. But conceivably could happen...
 *	7) Bit of cleanup needed...
 */
public class TestResultSetEqualContent implements ISQLTest {
	private int numDuplicates;
	private int numUnmatched;
	// default constructor
	public TestResultSetEqualContent() {
		// nothing at this time
	}
	
	// sqlTest - from interface
	public int sqlTest (IDAO dao, Query givenQuery, String desiredQueryString) {
		int result = 10;						// result on scale 0 to 10
		//String givenResultString = "";		// result set string returned from given query
		//String desiredResultString = "";	// result set string returned from desired query
		ResultSet rset = null;				// result set for SQL query
											// summary of result set's metadata
		ResultSetMetaDataSummary summary = new ResultSetMetaDataSummary();
											// query for string
		Query desiredQuery = new Query(desiredQueryString);
		
		String[][] givenResultMatrix = null;				// given result set, converted to matrix
		String[][] desiredResultMatrix = null;				//desired result set, converted to matrix
		
		int givenRowCt = 0; 				// temp holders for given query results
		int givenColCt = 0;
		//String givenColSet = null;		
		int desiredRowCt = 0; 				// temp holders for desired query results
		int desiredColCt = 0;
		//String desiredColSet = null;
		
		// 1) execute given query, get result set matrix for given query
		dao.connect();

		try {
			rset = dao.executeSQLQuery(givenQuery.toString());
			summary = dao.processResultSet(rset);
			//givenResultString = summary.getResultString();
			rset.beforeFirst();  
			givenResultMatrix = resultSetToMatrix(rset, summary.getNumCols(), summary.getNumRows());
		} catch (Exception e) {
			System.err.println("Error executing SQL/processing result set for given");
			//givenResultString = "given_error";
			givenResultMatrix = new String[0][0];
		}
		givenRowCt = summary.getNumRows();
		givenColCt = summary.getNumCols();
		dao.disconnect();
		rset = null;
		summary = null;
		
		// 2) execute desired query, get result set matrix
		dao.connect();
		
		try {
			rset = dao.executeSQLQuery(desiredQuery.toString());
			summary = dao.processResultSet(rset);
			//desiredResultString = summary.getResultString();
			//System.out.println(desiredResultString);
			rset.beforeFirst();
			desiredResultMatrix = resultSetToMatrix(rset, summary.getNumCols(), summary.getNumRows());
		} catch (Exception e) {
			System.err.println("Error executing SQL/processing result set for desired");
			//desiredResultString = "desired_error";
		}
		System.out.println(Arrays.deepToString(desiredResultMatrix) + "\n\n" + Arrays.deepToString(givenResultMatrix));
		desiredRowCt = summary.getNumRows();
		desiredColCt = summary.getNumCols();
		//desiredColSet = summary.getColumnSet();
		
		dao.disconnect();
		rset = null;
		summary = null;
		
		
		matchResultSets(givenResultMatrix, desiredResultMatrix);
		System.out.println("numUnmatched: " +numUnmatched +"\nnumDuplicates: " + numDuplicates);
		if(numDuplicates != 0) result = 3;
		if(givenColCt != desiredColCt) result -= 2;
		if(numUnmatched != 0) result = 0;
	
		System.out.println("result: " + result);
		return result;
	}	// end - method sqlTest
	
	private void matchResultSets(String[][] givenMatrix, String[][] desiredMatrix) {
		int res = 0;													//return value
		boolean[] markedDesired = new boolean[desiredMatrix.length];	//memory array of matched desired result rows
		boolean[] markedGiven = new boolean[givenMatrix.length];		//memory array of matched given result rows
		
		for(int i = 0; i < givenMatrix.length; i++) {					//for each given row, try to find matching desired row
			boolean foundDup = false;
			boolean foundMatch = false;
			
			for(int j = 0; j < desiredMatrix.length; j++) {				
				boolean isMatch = isMatch(givenMatrix[i], desiredMatrix[j]);
				if (isMatch && !markedDesired[j]) {						//if we have a match with unmarked row...
					markedDesired[j] = true;							//mark the row, continue to next given row
					markedGiven[i] = true;
					foundMatch = true;
					break;
				}
				if (isMatch && markedDesired[j]) {						//if we have match with marked row
					foundDup = true;									//we have a duplicate row
				}
			}
			
			if(foundDup && !foundMatch) numDuplicates++;				//increment duplicates if given row is a duplicate
			if(!foundDup && !foundMatch) numUnmatched++;				//increment unmatched if given row is extra
		}
		
		for(int i = 0; i < desiredMatrix.length; i++) {					//Found extra/duplicate rows. Now must find missing rows
			if(markedDesired[i]) continue;								//Only need to check unmatched desired rows...
			boolean foundDup = false;
			boolean foundMatch = false;
			
			for(int j = 0; j < givenMatrix.length; j++) {				
				boolean isMatch = isMatch(givenMatrix[j], desiredMatrix[i]);
				if (isMatch && !markedGiven[j]) {						//if we have a match with unmarked row...
					foundMatch = true;
					markedGiven[j] = true;
					break;
				}
				if (isMatch && markedGiven[j]) {
					foundDup = true;
				}
			}
			
			if(foundDup && !foundMatch) numDuplicates++;
			if(!foundDup && !foundMatch) numUnmatched++;
		}
	
		
	}
	
	private boolean isMatch(String[] givenRow, String[] desiredRow) {
		boolean[] marked = new boolean[desiredRow.length];
		int numMarked = 0;
		int numElems = desiredRow.length;
		
		for(int i = 0; i < givenRow.length; i++) {				    //for each element in givenRow
			for(int j = 0; j < desiredRow.length; j++) {		    //look for matching element in desiredRow
				if(givenRow[i].equals(desiredRow[j]) && !marked[j]) {	//if equal elements and desired desired element is unmarked
					marked[j] = true;								//mark element
					numMarked++;									//increment numMarked
					break;											//continue to next given element
				}
			}
		}
		
		return numMarked == numElems;
		
	}
	
	// getName - from interface
	public String getName() {
		return ("TestResultSetEqualContent");
	}
	
	public String getDesc() {
		return "Answer has same result set content as desired query";
	}
	
	
	//Converts a resultSet into a matrix
	
	private String[][] resultSetToMatrix(ResultSet resultSet, int numCols, int numRows) throws SQLException{
		String[][] ret = null;														//return value
		try {
			ret = new String[numRows][numCols];										//initialize
			int i = 0;
			while (resultSet.next()) {
				for (int j = 1; j <= numCols; j++) {
					String s = resultSet.getString(j);								//grab string
					if(resultSet.wasNull()) ret[i][j-1] = "NULL";					//take care of null values
					else if(s.matches("\\d{2}\\-\\w{3}\\-\\d{2}")) {				//convert dd-MMM-yy format to standardized format
						DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
						try {
					    	Date thisDate = dateFormat.parse(s);
							ret[i][j-1] = Long.toString(thisDate.getTime());

						} catch (ParseException e) {
							e.printStackTrace();
						}
					}													//convert YYYY-MM-DD HH-MM-SS format to standardized format
					else if( s.matches("\\d{4}\\-\\d{2}\\-\\d{2} \\d{2}:\\d{2}:\\d{2}.*")) {
						//convert YYYY-MM-DD HH-MM-SS to DD-MM-YYYY
						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					    try {
					    	Date thisDate = dateFormat.parse(s);
							ret[i][j-1] = Long.toString(thisDate.getTime());

						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
					else if(s.matches("\\d*\\.\\d{2}.+")) {				//truncate long decimal values (or sci. notation) to 2 decimal places
						s = new BigDecimal(s).setScale(2, RoundingMode.HALF_UP).toPlainString();
						ret[i][j-1] = s.substring(0, s.indexOf('.') + 3);
					}
					else ret[i][j-1] = s;
					
				}
				i++;
			}
		}
		catch(SQLException e) {
			throw e;
		}
		return ret;
	}	
	
}	// end - class TestResultSetEqualContent
