/* TestResultSetEqualContent - class to test for result set equality regardless of row/column order
 * NOTE: currently testing row ct., column ct., and result string length.  
 *       Cannot directly test column names because people may change names with various aliases
 * 
 * Created - Paul J. Wagner, 2-Oct-2018
 */
package edu.uwec.cs.wagnerpj.sqltest.sqltests;

import java.awt.List;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import edu.uwec.cs.wagnerpj.sqltest.general.*;




public class TestResultSetEqualContent implements ISQLTest {
	private int numDuplicates;
	private ArrayList<Map<Integer, Integer>> failedMappings = new ArrayList<Map<Integer,Integer>>();
	private String[][] desiredResultMatrix;
	private String[][] givenResultMatrix;
	// default constructor
	public TestResultSetEqualContent() {
		// nothing at this time
	}
	
	// sqlTest - from interface
	public int sqlTest (IDAO dao, Query givenQuery, String desiredQueryString) {
		int result = 10;						// result on scale 0 to 10
		this.numDuplicates = Integer.MAX_VALUE;
		//String givenResultString = "";		// result set string returned from given query
		//String desiredResultString = "";	// result set string returned from desired query
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
		
		
		//calculate grade
		// if matchResultSets() is true, we found a perfect match
		// if numDuplicates != Integer.MAX_VALUE, then we found a case where the only error was duplicate rows
		// if matchResultSets() was false and numDuplicates == Integer.MAX_VALUE, then student answer is strictly wrong

		if(matchResultSets()) {
			//do not deduct any points (do nothing)
		}
		else if(numDuplicates == Integer.MAX_VALUE){
			result = 0;	//if matchResultSets() returns false and numDuplicates is unchanged, answer is strictly wrong
		}
		else result -= 5;	//amount of points to deduct if answer contains (or is missing) duplicates but is otherwise correct

		if(desiredColCt != givenColCt){
			result -= 3;	//amount of points to deduct if answer contains extra columns
		}

		if(result < 0) result = 0;
	
		System.out.println("result: " + result);
		return result;
	}	// end - method sqlTest
	
	
	
	// getName - from interface
	public String getName() {
		return ("TestResultSetEqualContent");
	}
	
	public String getDesc() {
		return "Answer has same result set content as desired query";
	}


	/** matchResultSets()
	 * 		For the very first row of the given Result Set, we will look for a match in the desired Result set
	 * 		Any time we find a match within matchRows(), matchRows() will call matchRest() to check if ALL other
	 * 		rows can be matched according to the same column-matching schema.
	 *
	 * 		If loop exits without finding a match, we have at least one strictly wrong row, and will return false.
	 */
	private boolean matchResultSets() {
		if(desiredResultMatrix.length == 0 || givenResultMatrix.length == 0) {			//for cases involving an empty matrix
			return desiredResultMatrix.length == 0 && givenResultMatrix.length == 0;
		}
		for(int i = 0; i < givenResultMatrix.length; i++) {
				//for each desired row, see if we can match a given row
				if (matchRows(desiredResultMatrix[0], givenResultMatrix[i],
									new boolean[desiredResultMatrix[0].length], new boolean[givenResultMatrix[0].length],
									new HashMap<Integer, Integer>(), 0, 0, i)){
					return true;
				}
		}
		
		return false;
	}

	/** matchRows()
	 * 			Will conduct a recursive depth-first-search to find all possible column-matching schemas that can equate
	 * 			the desiredRow and givenRow. Any time a valid column-matching schema is found, matchRest() will be called
	 * 			to see if all other rows can be matched according to that schema
	 * @param desiredRow		row in question from desiredResultMatrix
	 * @param givenRow			row in question from givenResultMatrix
	 * @param desiredMarked		memory array to record through each recursive call which indexes of desiredRow have already been matched
	 * @param givenMarked		memory array to record through each recursive call which indexes of givenRow have already been matched
	 * @param columnMapping		a column-matching schema that will be built and sent to matchRest() if a complete schema can successfully be built.
	 * @param numMatched		record of how many matches have been established so that we can determine whether a columnMapping is complete
	 * @param desiredColumnIndex	record of column index in question to pass through recursive calls
	 * @param givenRowIndex		record of row index in question to be passed to matchRest() in order to prevent that row from being matched twice
	 * @return	True if perfect match. False if not.
	 */
	private boolean matchRows(String[] desiredRow, String[] givenRow, boolean[] desiredMarked, boolean[] givenMarked, 
							HashMap<Integer, Integer> columnMapping,
							int numMatched, int desiredColumnIndex, int givenRowIndex) {

		//Base case
		if(desiredColumnIndex == desiredRow.length) return false;

		for(int j = 0; j < givenRow.length; j++) {
			//if we can match two unmarked elements...
			if(givenRow[j].equals(desiredRow[desiredColumnIndex]) && !desiredMarked[desiredColumnIndex] && !givenMarked[j]) {
				//Mark as visited and add column indexes to our column mapping schema schema.
				desiredMarked[desiredColumnIndex] = true; givenMarked[j] = true;
				columnMapping.put(desiredColumnIndex, j);
				numMatched++;

				//if our column mapping schema accounts for all desired columns and this mapping hasn't previously failed
				//then try to match the rest of the rows
				if(numMatched == desiredRow.length) {
					if (!failedMappings.contains(columnMapping)) {
						if(matchRest(columnMapping, givenRowIndex)) return true;
					}
				}
				//otherwise if column mapping schema is not yet complete, continue building with a recursive call
				else if (matchRows(desiredRow, givenRow, desiredMarked, givenMarked, columnMapping,
						numMatched, desiredColumnIndex + 1, givenRowIndex)) {
					return true; //Bubble up return value through recursive stack
				}

				//Matching failed. Unmark as visited and remove column indexes from our column mapping schema.
				desiredMarked[desiredColumnIndex] = false; givenMarked[j] = false;
				numMatched--;
				columnMapping.remove(desiredColumnIndex);
			}
		}
		return false;
	}

	/**	matchRest()
	 * 			Determines if rest of rows is desired result set can be matched with given rows and vice versa.
	 * 			Will adjust global value numDuplicates in the case where the only unmatched rows are duplicates,
	 * 			if the numDuplicates found is less than any previous value.
	 *
	 * @param columnMapping		The column matching schema in question
	 * @param givenRowIndex		The given row in index (used to avoid trying to match this row twice
	 * @return					True if perfect match found. False if not found.
	 */
	private boolean matchRest(HashMap<Integer,Integer> columnMapping, int givenRowIndex) {
		boolean[] markedGiven = new boolean[givenResultMatrix.length];        //memory array to remember marked given columns
		boolean[] markedDesired = new boolean[desiredResultMatrix.length];    //memory array to remember marked desired columns
		boolean foundDup = false;
		boolean foundMatch = false;
		int numOfDuplicates = 0;
		markedGiven[givenRowIndex] = true;        //mark given row matched in matchRows() as already matched.
		markedDesired[0] = true;                //mark desired row matched in matchRows() as already matched (will always be the first row)

		//For each given row, try to find matching desired row
		for (int i = 0; i < givenResultMatrix.length; i++) {
			if (i == givenRowIndex) continue;    //Do not try to match row that was matched in matchRows()
			foundDup = false;
			foundMatch = false;

			//try to match with desired row
			for (int j = 0; j < desiredResultMatrix.length; j++) {

				//If we can match, it is either a true match or a duplicate match
				if (isMatch(givenResultMatrix[i], desiredResultMatrix[j], columnMapping)) {
					if (!markedDesired[j]) {
						markedDesired[j] = true;
						markedGiven[i] = true;
						foundMatch = true;    //match found
						break; //match found, try to match next given row
					} else {
						foundDup = true;    //possible duplicate found
					}
				}
			}


			if (!foundMatch) {
				if (foundDup) {    //If no match found but we found at least one duplicate match, increment numOfDuplicates
					numOfDuplicates++;
				} else {            //If no match is found and no duplicate match could be established, then this column matching schema fails.
					failedMappings.add(columnMapping);
					return false;
				}
			}
		}

		//Test remaining unmarked desired Rows to determine if Student's answer is only missing duplicate rows (incorrectly added DISTINCT to their statement)
		for (int i = 0; i < desiredResultMatrix.length; i++) {
			if (markedDesired[i]) continue;    //Only need to check unmatched desired rows...
			foundDup = false;

			//check given Rows
			for (int j = 0; j < givenResultMatrix.length; j++) {
				//If we find a match, it must be a duplicate desired row
				if (isMatch(givenResultMatrix[j], desiredResultMatrix[i], columnMapping)) {
					foundDup = true;
				}
			}
			if (foundDup) {    //If at least one match found, increment numOfDuplicates
				numOfDuplicates++;
			} else {            //If no match is found and no duplicate match could be established, then this column matching schema fails.
				failedMappings.add(columnMapping);
				return false;
			}
		}

		//This code will only be reached if there were no STRICTLY incorrect rows
		//If we have any duplicates, this column matching schema fails.
		//Check if numOfDuplicates is min value and adjust before exiting.
		if (numOfDuplicates != 0) {
			if (numOfDuplicates < this.numDuplicates) {
				this.numDuplicates = numOfDuplicates;
			}
			failedMappings.add(columnMapping);
			return false;
		}
		return true;	//Perfect match found, return true
	}


	/**	isMatch()
	 * 			Attempts to match two rows according to the column matching schema in question.
	 * @param givenRow			The given row in question.
	 * @param desiredRow		The desired row in question.
	 * @param columnMappings	The column matching schema in question.
	 * @return					True if match. False if not.
	 */
	private boolean isMatch(String[] givenRow, String[] desiredRow, HashMap<Integer, Integer> columnMappings) {
		int size = columnMappings.keySet().size();
		for(int j = 0; j < size; j++) {
			if(!desiredRow[j].equals(givenRow[columnMappings.get(j)])) return false;
		}
		return true;
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
