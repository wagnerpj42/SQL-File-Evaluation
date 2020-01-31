/* TestResultSetEqualContentv1 - class to test for result set equality regardless of row/column order
 * NOTE: currently testing row ct., column ct., and result string length.
 *       Cannot directly test column names because people may change names with various aliases
 *
 * Created - Paul J. Wagner, 2-Oct-2018
 */
package edu.uwec.cs.wagnerpj.sqltest.sqltests;

import edu.uwec.cs.wagnerpj.sqltest.general.*;
import edu.uwec.cs.wagnerpj.sqltest.util.QueryParseUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static edu.uwec.cs.wagnerpj.sqltest.sqltests.TestResultSetEqualContentNotDatesButGOod.isValidOnClause;
import static edu.uwec.cs.wagnerpj.sqltest.util.QueryParseUtil.*;

/**		README:
 *
 * 		This algorithm follows the general outline below
 *			(1) Query strings (both desired and given) are parsed for information.
 *				-Column name information is stored in list. Position in list corresponds to position of column in SELECT statement
 *				-Column date format information is stored in list. Position in list corresponds to position of column in SELECT statement
 *				-Most functions (except for to_char, round, and cast) are also included in the column name if present.
 *				-JOIN clauses are parsed to identify which columns are being joined. Stored in a list of Edges to form a graph.
 *					-Only inner joins that compare columns on equality will be considered.
 *				    -Two columns that are joined on equality will form an "Edge"
 *				    -We will store all edges in a List, which creates a traversable graph of equal columns
 *				-If an alias is used instead of column name, subselect statements will be parsed in attempt to resolve alias' column name
 *					-First find column names and aliases of subselect statements
 *					-If column name of outer select statement matches a column alias of a subselect statement column
 *					 then we change the name of the outer select statement column to the column name of the subselect statement column
 *				-Same as above will be executed to resolve column names that exist in the ON clause of a JOIN sattement
 *				-If SELECT * FROM is found, then we pull column name information directly from ResultSetMetaData object
 *			(2) Queries will be executed through JDBC.
 *		     	Given and desired result set rows will be potentially reformatted and converted into a matrix of Strings
 *				-Different date formats will be reformatted into a standardized format
 *				-Long decimals will be truncated (currently to two decimal places)
 *			(3) An attempt is made to map each desired column index to at least one given column index based off of the columns' names
 *				-If a given column name and desired column name match exactly, then they will be matched
 *				-If a given column name and desired column name are connected on the equivalency graph (due to JOIN clauses)
 *				 then they will be matched.
 *		    (4) If (3) succeeds, then we will try to match rows between the result sets based on the column mapping(s) found in (3)
 *		  		-We will record number of missing and extra rows (if any)
 *		  		-The number of missing and extra rows will be used to calculate the final score
 *		  	(5) We will then try to match desired and given result sets using a different strategy
 *		  		-Look for any rows that match between given and desired result sets
 *		  		-If two rows match, see if all other rows can be matched according to the same column-mapping used to match the first row
 *		  		-If (3) and (4) were executed, but we find a better score, then issue a warning to the professor
 *		  		-Otherwise, record the best score found using this method.
 *
 *		  	As seen above, two strategies are employed in attempt to match rows between desired and given result sets.
 *		  	The two strategies differ in the manner that they attempt to identify which given columns correspond to each desired column
 *		    between the two result sets.
 *
 *		  (1) Query-Parsing
 *		  		-Parse the String values of both queries in order to identify column names of each.
 *		  		-Use column names to create a column association and test based on column association.
 *		  		-Potentially many column associations might be found and tested here. Best score will be considered correct.
 *		  (2) Row-matching
 *		  		-Attempt to find at least one given row that matches with at least one desired row.
 *		  		-We will remember which given column corresponds to which desired column, and see if other rows can be matched
 *		  		according to the same column-mapping used to find the first match
 *		  		-If we match all rows, we are done
 *		  		-If not all rows match, we will continue looking for matches until all potential matches have been tested
 *
 *		 Strategy (1) might not succeed in creating a column association, in which case strategy (2) will be employed.
 *		 If strategy (1) does succeed, we will still test strategy (2) to ensure that scores of both tests are equal.
 *		 If scores are unequal, then either (1) returned with a false-positive column mapping or (2) returned with a
 *		 false-negative. We will issue a warning to the professor in this case so the situation can be analyzed.
 *
 *
 * 		 NOTE: If more functions are found that should be ignored by the query-parsing portion of the algorithm, go to
 * 		 QueryParseUtil class and ctrl+f "IGNORABLE_FUNCTIONS" and add them to the list of functions found in the regex
 * 		 on the line below.
 *
 * 		 NOTE: If more keywords are found that should be ignored by the algorithm, go to QueryParseUtil class and
 * 		 ctrl+f "IGNORABLE_KEYWORDS_1", "IGNORABLE_KEYWORDS_2", "IGNORABLE_KEYWORDS_3", and "IGNORABLE_KEYWORDS_4"
 * 		 and add them to the lists found in the regex on the lines below. Note that there are two locations in the
 * 		 regex of IGNORABLE_KEYWORDS_2 where the ignorable keyword should be added. If not added, these keywords might
 * 		 be mistaken for column names or functions during calls to identifyColumnName() or identifyColumnFunctions().
 */


@SuppressWarnings("Duplicates")
public class TestResultSetEqualContent implements ISQLTest {
	private int numDuplicates;
	private int numUnmatchedRows;

	private ArrayList<Map<Integer, Integer>> failedMappings = new ArrayList<Map<Integer, Integer>>();
	private ArrayList<Map<Integer, Integer>> failedMappingTemp = new ArrayList<Map<Integer, Integer>>();
	private boolean columnAssociationCompleted = false;
	private ArrayList<Edge> givenColumnNameEquivalenceGraph = new ArrayList<Edge>();
	private ArrayList<Edge> desiredColumnNameEquivalenceGraph = new ArrayList<Edge>();
	private ArrayList<String[]> extraRows = new ArrayList<String[]>();
	private ArrayList<String[]> missingRows = new ArrayList<String[]>();
	private ArrayList<Boolean> desiredMonthFirstFormat = new ArrayList<>();
	private ArrayList<Boolean> givenMonthFirstFormat = new ArrayList<>();
	private String[][] desiredResultMatrix;
	private String[][] givenResultMatrix;
	private ResultSetMetaData rsmdGiven = null;
	private ResultSetMetaData rsmdDesired = null;
	private TestResult testResult = new TestResult();


	// default constructor
	public TestResultSetEqualContent() {
		// nothing at this time
	}

	// sqlTest - from interface

	//TODO: Allow additional options on each column. Decimal length, deactivate formatting.
	//TODO: Allow multiple return values. Number of points returned. Incorrect rows. Warnings for professor
	//TODO: Check both versions of algorithm. If they are not equivalent, issue a warning for grader to hand-check.
	//TODO: Clean-up / refactoring

	public TestResult sqlTest(IDAO dao, Query givenQuery, String desiredQueryString){
		int result = 10;                        // result on scale 0 to 10
		this.numDuplicates = Integer.MAX_VALUE;
		this.numUnmatchedRows = Integer.MAX_VALUE;
		//String givenResultString = "";		// result set string returned from given query
		//String desiredResultString = "";	// result set string returned from desired query
		ResultSet rsetGiven = null;
		ResultSet rsetDesired = null;
		// summary of result set's metadata
		ResultSetMetaDataSummary summaryDesired = new ResultSetMetaDataSummary();
		ResultSetMetaDataSummary summaryGiven = new ResultSetMetaDataSummary();
		// query for string
		Query desiredQuery = new Query(desiredQueryString);


		int givenRowCt = 0;                // temp holders for given query results
		int givenColCt = 0;
		//String givenColSet = null;		
		int desiredRowCt = 0;                // temp holders for desired query results
		int desiredColCt = 0;
		//String desiredColSet = null;

		// 1) execute given query, get result set matrix for given query
		dao.connect();

		try {
			rsetGiven = dao.executeSQLQuery(givenQuery.toString());
			rsmdGiven = rsetGiven.getMetaData();
			summaryGiven = dao.processResultSet(rsetGiven);
			//givenResultString = summary.getResultString();
			rsetGiven.beforeFirst();
		} catch (Exception e) {
			System.err.println("Error executing SQL/processing result set for given");
			//givenResultString = "given_error";
			givenResultMatrix = new String[0][0];
			System.out.println("RESULT: 0");
			return new TestResult(0);
		}
		givenRowCt = summaryGiven.getNumRows();
		givenColCt = summaryGiven.getNumCols();


		// 2) execute desired query, get result set matrix


		try {
			rsetDesired = dao.executeSQLQuery(desiredQuery.toString());
			rsmdDesired = rsetDesired.getMetaData();
			summaryDesired = dao.processResultSet(rsetDesired);
			//desiredResultString = summary.getResultString();
			//System.out.println(desiredResultString);
			rsetDesired.beforeFirst();
		} catch (Exception e) {
			System.err.println("Error executing SQL/processing result set for desired");
			//desiredResultString = "desired_error";
		}

		desiredRowCt = summaryDesired.getNumRows();
		desiredColCt = summaryDesired.getNumCols();

		//desiredColSet = summary.getColumnSet();

		Map<Integer, ArrayList<Integer>> columnAssociations = attemptColumnAssociation(desiredQueryString, givenQuery.toString());
		try{
			desiredResultMatrix = resultSetToMatrix(rsetDesired, desiredColCt, desiredRowCt, desiredMonthFirstFormat);
		}
		catch(Exception e){
			System.out.println("Could not process desired matrix.");
		}
		try {
			givenResultMatrix = resultSetToMatrix(rsetGiven, givenColCt, givenRowCt, givenMonthFirstFormat);
		}
		catch(Exception e){
			System.out.println("Could not process given matrix.\nRESULT: 0");
			return new TestResult(0);
		}
		System.out.println("GIVEN:\n" + Arrays.deepToString(givenResultMatrix) + "\n\n" + "DESIRED:\n" + Arrays.deepToString(desiredResultMatrix) + "\n");
		dao.disconnect();



		//if column association(s) successfully identified, use to match rows
		if(columnAssociations != null){
			matchAll(columnAssociations, new boolean[givenColCt], new HashMap<Integer, Integer>(), 0, 0);
			columnAssociationCompleted = true;
		}
		//otherwise attempt to build / test association rules based on resultSet content.
		matchResultSets();

		int numMatched = desiredRowCt - numUnmatchedRows - numDuplicates;
		if(desiredRowCt != 0){
			result = (int)(((double)numMatched / ((double)(numMatched + 3 * (numUnmatchedRows + numDuplicates))) * 10.0));
		}
		else if(givenRowCt == 0) result = 10;
		else result = 0;
		if(result != 10) result -= 2;
		if(desiredColCt != givenColCt) result -= 3;

		if(result < 0) result = 0;

		System.out.println("numUnmatched: " + numUnmatchedRows);
		System.out.println("numDuplicates: " + numDuplicates);
		System.out.println("result: " + result);
		System.out.println("\nMissing rows:");
		for(int i = 0; i < missingRows.size(); i++) System.out.println(Arrays.toString(missingRows.get(i)));
		System.out.println("\nExtra rows:" );
		for(int i = 0; i < extraRows.size(); i++) System.out.println(Arrays.toString(extraRows.get(i)));
		testResult.setScore(result);
		testResult.setExtraRows(extraRows);
		testResult.setMissingRows(missingRows);
		return testResult;


	}    // end - method sqlTest


	// getName - from interface
	public String getName() {
		return ("TestResultSetEqualContent");
	}

	public String getDesc() {
		return "Answer has same result set content as desired query";
	}


	/**
	 * matchResultSets()
	 * For the very first row of the given Result Set, we will look for a match in the desired Result set
	 * Any time we find a match within matchRows(), matchRows() will call matchRest() to check if ALL other
	 * rows can be matched according to the same column-matching schema.
	 * If loop exits without finding a match, we have at least one strictly wrong row, and will return false.
	 */

	private boolean matchResultSets() {
		//if either array is empty, return true if both are empty.
		//otherwise, set unmatched rows.
		if (desiredResultMatrix.length == 0 || givenResultMatrix.length == 0) {            //for cases involving an empty matrix
			for(int i = 0; i < desiredResultMatrix.length; i++) missingRows.add(desiredResultMatrix[i]);
			for(int j = 0; j < givenResultMatrix.length; j++)	extraRows.add(givenResultMatrix[j]);
			if(desiredResultMatrix.length == 0 && givenResultMatrix.length == 0){
				numUnmatchedRows = 0;
				numDuplicates = 0;
				return true;
			}
			numUnmatchedRows = desiredResultMatrix.length + givenResultMatrix.length;
			numDuplicates = 0;
			return false;
		}

		//for each desired row, see if we can match a given row
		for (int i = 0; i < givenResultMatrix.length; i++) {
			for(int j = 0; j < desiredResultMatrix.length; j++)
				if (matchOne(desiredResultMatrix[j], givenResultMatrix[i],
						new boolean[desiredResultMatrix[0].length], new boolean[givenResultMatrix[0].length],
						new HashMap<Integer, Integer>(), 0, 0, i, j, new boolean[givenResultMatrix[0].length])) {
					return true;
				}
		}

		//if numUnmatchedRows and numDuplicates remain default values (MAX_VALUE), then no desired rows matched with any given rows.
		//set numUnmatched to total row counts of both matrices.
		if(numUnmatchedRows == Integer.MAX_VALUE && numDuplicates == Integer.MAX_VALUE){
			for(int i = 0; i < desiredResultMatrix.length; i ++) missingRows.add(desiredResultMatrix[i]);
			for(int j = 0; j < givenResultMatrix.length; j++)	 extraRows.add(givenResultMatrix[j]);
			numUnmatchedRows = desiredResultMatrix.length + givenResultMatrix.length;
			numDuplicates = 0;
		}

		return false;
	}


	//Will attempt to match one desired row with one given row
	//If successful, matchRest() will be called to see how many other rows can be matched according to same column mapping used to
	//match the first pair of rows.
	@SuppressWarnings("Duplicates")
	private boolean matchOne(String[] desiredRow, String[] givenRow, boolean[] desiredMarked, boolean[] givenMarked,
							 HashMap<Integer, Integer> columnMapping,
							 int numMatched, int desiredColumnIndex, int givenRowIndex, int desiredRowIndex, boolean[] isReversedDate) {

		//Base case
		if (desiredColumnIndex == desiredRow.length) return false;
		Map<Integer, Integer> mapSnapshot = null;
		for (int j = 0; j < givenRow.length; j++) {
			//if we can match two unmarked elements...
			if (givenRow[j].equals(desiredRow[desiredColumnIndex]) && !desiredMarked[desiredColumnIndex] && !givenMarked[j]) {
				//Mark as visited and add column indexes to our column mapping schema schema.
				desiredMarked[desiredColumnIndex] = true;
				givenMarked[j] = true;
				columnMapping.put(desiredColumnIndex, j);
				numMatched++;

				//if our column mapping schema accounts for all desired columns and this mapping hasn't previously failed
				//then try to match the rest of the rows
				if (numMatched == desiredRow.length) {
					failedMappingTemp.add((HashMap<Integer, Integer>) columnMapping.clone());
					if (!failedMappings.contains(columnMapping)) {
						if (matchRest(columnMapping, givenRowIndex, desiredRowIndex, isReversedDate)) return true;
					}
				}
				//otherwise if column mapping schema is not yet complete, continue building with a recursive call
				else if (matchOne(desiredRow, givenRow, desiredMarked, givenMarked, columnMapping,
						numMatched, desiredColumnIndex + 1, givenRowIndex, desiredRowIndex, isReversedDate)) {
					return true; //Bubble up return value through recursive stack
				}

				//Matching failed. Unmark as visited and remove column indexes from our column mapping schema.

				desiredMarked[desiredColumnIndex] = false;
				givenMarked[j] = false;
				numMatched--;
				columnMapping.remove(desiredColumnIndex);
			}

			if (reversedDateMatches(givenRow[j], desiredRow[desiredColumnIndex]) && !desiredMarked[desiredColumnIndex] && !givenMarked[j]) {
				//Mark as visited and add column indexes to our column mapping schema schema.
				desiredMarked[desiredColumnIndex] = true;
				givenMarked[j] = true;
				isReversedDate[j] = true;
				columnMapping.put(desiredColumnIndex, j);
				numMatched++;

				//if our column mapping schema accounts for all desired columns and this mapping hasn't previously failed
				//then try to match the rest of the rows
				if (numMatched == desiredRow.length) {
					failedMappingTemp.add((HashMap<Integer, Integer>) columnMapping.clone());
					if (!failedMappings.contains(columnMapping)) {
						if (matchRest(columnMapping, givenRowIndex, desiredRowIndex, isReversedDate)) return true;
					}
				}
				//otherwise if column mapping schema is not yet complete, continue building with a recursive call
				else if (matchOne(desiredRow, givenRow, desiredMarked, givenMarked, columnMapping,
						numMatched, desiredColumnIndex + 1, givenRowIndex, desiredRowIndex, isReversedDate)) {
					return true; //Bubble up return value through recursive stack
				}

				//Matching failed. Unmark as visited and remove column indexes from our column mapping schema.
				desiredMarked[desiredColumnIndex] = false;
				givenMarked[j] = false;
				numMatched--;
				columnMapping.remove(desiredColumnIndex);
				isReversedDate[j] = false;
			}
		}

		//if returning to matchResultSets(), add all failed mappings found.
		if (numMatched == 0) {
			failedMappings.addAll(failedMappingTemp);
			failedMappingTemp.clear();
		}
		return false;
	}

	/**
	 * matchRest()
	 * Determines if rest of rows is desired result set can be matched with given rows and vice versa.
	 * Will adjust global value numDuplicates in the case where the only unmatched rows are duplicates,
	 * if the numDuplicates found is less than any previous value.
	 *
	 * @param columnMapping The column matching schema in question
	 * @param givenRowIndex The given row in index (used to avoid trying to match this row twice
	 * @return True if perfect match found. False if not found.
	 */


	private boolean matchRest(HashMap<Integer, Integer> columnMapping, int givenRowIndex, int desiredRowIndex, boolean[] isReversedDate) {
		boolean[] markedGiven = new boolean[givenResultMatrix.length];        //memory array to remember marked given columns
		boolean[] markedDesired = new boolean[desiredResultMatrix.length];    //memory array to remember marked desired columns
		boolean foundDup = false;
		boolean foundMatch = false;
		int numOfDuplicates = 0;
		int numOfExtraRows = 0;
		int numOfMissingRows = 0;
		ArrayList<String[]> missingRowsTemp = new ArrayList<String[]>();
		ArrayList<String[]> extraRowsTemp = new ArrayList<String[]>();

		//Mark as visited the rows that were found in matchOne. -1 is stand-in for null value for when we are matching based off of column association
		//that was found by parsing the query strings themselves, in which case no rows have yet been visited.
		if(givenRowIndex != -1) markedGiven[givenRowIndex] = true;        //mark given row matched in matchRows() as already matched.
		if(desiredRowIndex != -1) markedDesired[desiredRowIndex] = true;    //mark desired row matched in matchRows() as already matched

		//For each given row, try to find matching desired row
		for (int i = 0; i < givenResultMatrix.length; i++) {
			if (markedGiven[i]) continue;    //Do not try to match row that was matched in matchRows()
			foundDup = false;
			foundMatch = false;

			//try to match with desired row
			for (int j = 0; j < desiredResultMatrix.length; j++) {

				//If we can match, it is either a true match or a duplicate match
				if (isMatch(givenResultMatrix[i], desiredResultMatrix[j], columnMapping, isReversedDate)) {
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
				} else {            //If no match is found and no duplicate match could be established, then extra row found
					numOfExtraRows++;
				}
				extraRowsTemp.add(givenResultMatrix[i]);
			}
		}

		//Test remaining unmarked desired Rows to determine whether they are duplicates or strictly incorrect.
		for (int i = 0; i < desiredResultMatrix.length; i++) {
			if (markedDesired[i]) continue;    //Only need to check unmatched desired rows...
			foundDup = false;

			//check given Rows
			for (int j = 0; j < givenResultMatrix.length; j++) {
				//If we find a match, it must be a duplicate desired row
				if (isMatch(givenResultMatrix[j], desiredResultMatrix[i], columnMapping, isReversedDate)) {
					foundDup = true;
				}
			}
			if (foundDup) {    //If at least one match found, increment numOfDuplicates
				numOfDuplicates++;
				missingRowsTemp.add(desiredResultMatrix[i]);
			} else {            //Otherwise student is strictly missing a row.
				numOfMissingRows++;
				missingRowsTemp.add(desiredResultMatrix[i]);
			}
		}

		//If no extra, missing, or duplicate rows then we have a perfect match. Return true.
		if (numOfExtraRows + numOfMissingRows + numOfDuplicates == 0) {
			//If columnAssociation was completed and we found a better score than it, issue a warning to Professor
			if(columnAssociationCompleted && (numUnmatchedRows + numDuplicates) != 0){
				testResult.setWarning(true);
				return true;
			}
			else{
				numUnmatchedRows = 0;
				numDuplicates = 0;
				missingRows = missingRowsTemp;
				extraRows = extraRowsTemp;
				return true;
			}

			//Otherwise not a perfect match. Check if it is best answer found so far, and return false.
		} else if (numOfMissingRows + numOfExtraRows < numUnmatchedRows) {
			//If columnAssociation was completed
			if(columnAssociationCompleted){
				testResult.setWarning(true);
			}
			else{
				numUnmatchedRows = numOfMissingRows + numOfExtraRows;
				numDuplicates = numOfDuplicates;
				missingRows = missingRowsTemp;
				extraRows = extraRowsTemp;
			}

		}

		return false;
	}

	//if column association was found in attemptColumnAssociation(), then test any column association found
	private void matchAll(Map<Integer, ArrayList<Integer>> columnAssociations, boolean[] markedGivenColumns, boolean[] markedDesiredColumns, HashMap<Integer, Integer> columnMapping, int numCols){
		for(Integer desiredColumn : columnAssociations.keySet()){
			for(Integer givenColumn : columnAssociations.get(desiredColumn)){
				if(markedDesiredColumns[desiredColumn]) continue;
				if(markedGivenColumns[givenColumn]) continue;
				columnMapping.put(desiredColumn, givenColumn);
				markedGivenColumns[givenColumn] = true;
				markedDesiredColumns[desiredColumn] = true;
				numCols++;
				if(numCols == columnAssociations.size()) matchRest(columnMapping, -1, -1, new boolean[markedGivenColumns.length]);
				else matchAll(columnAssociations, markedGivenColumns, markedDesiredColumns, columnMapping, numCols);
				numCols--;
				markedGivenColumns[givenColumn] = false;
			}
			markedDesiredColumns[desiredColumn] = false;
		}
	}

	private void matchAll(Map<Integer, ArrayList<Integer>> columnAssociations, boolean[] markedGivenColumns, HashMap<Integer, Integer> columnMapping, int numCols, int currDesiredCol){
		for(Integer givenColumn : columnAssociations.get(currDesiredCol)){
			if(markedGivenColumns[givenColumn]) continue;
			columnMapping.put(currDesiredCol, givenColumn);
			markedGivenColumns[givenColumn] = true;
			numCols++;
			if(numCols == columnAssociations.size()) matchRest(columnMapping, -1, -1, new boolean[markedGivenColumns.length]);
			else matchAll(columnAssociations, markedGivenColumns, columnMapping, numCols, currDesiredCol + 1);
			numCols--;
			markedGivenColumns[givenColumn] = false;
		}
	}

	/**
	 * isMatch()
	 * Attempts to match two rows according to the column matching schema in question.
	 *
	 * @param givenRow       The given row in question.
	 * @param desiredRow     The desired row in question.
	 * @param columnMappings The column matching schema in question.
	 * @return True if match. False if not.
	 */

	//Test if two rows match according to a supplied columnMapping, which associates desired and given column indexes
	//Will make sure date format is corrected if a dd-mm-yyyy date format is being tested against a mm-dd-yyyy date format
	private boolean isMatch(String[] givenRow, String[] desiredRow, HashMap<Integer, Integer> columnMappings, boolean[] isReversedDate) {
		int size = columnMappings.keySet().size();
		for (int i = 0; i < size; i++) {
			if (!isReversedDate[columnMappings.get(i)] && !desiredRow[i].equals(givenRow[columnMappings.get(i)]))
				return false;
			if (isReversedDate[columnMappings.get(i)] && !reversedDateMatches(desiredRow[i], givenRow[columnMappings.get(i)]))
				return false;
		}
		return true;
	}

	//checks if a dd-mm-yyyy formatted column matches a mm-dd-yyyy formatted column
	private boolean reversedDateMatches(String s1, String s2) {
		if ((s1.matches("\\d{2}-\\d{2}-\\d{4}\\s.*") && s2.matches("\\d{2}-\\d{2}-\\d{4}\\s.*"))) {
			String day = s1.substring(0, 2);
			String month = s1.substring(3, 5);
			s1 = month + "-" + day + s1.substring(5, s1.length());
			return s1.equals(s2);
		} else if ((s1.matches("\\d{4}-\\d{2}-\\d{2}\\s.*") && s2.matches("\\d{4}-\\d{2}-\\d{2}\\s.*"))) {
			String day = s1.substring(5, 7);
			String month = s1.substring(8, 10);
			s1 = s1.substring(0, 5) + month + "-" + day + s1.substring(10, s1.length());
			return s1.equals(s2);
		} else {
			return false;
		}
	}

	//Converts a resultSet into a matrix
	private String[][] resultSetToMatrix(ResultSet resultSet, int numCols, int numRows, ArrayList<Boolean> monthFirstDateFormat) throws SQLException {
		String[][] ret = null;
		try {
			ret = new String[numRows][numCols];
			int i = 0;
			while (resultSet.next()) {
				for (int j = 1; j <= numCols; j++) {
					String s = resultSet.getString(j);                                //grab string
					if (resultSet.wasNull()) s = "NULL";                    //take care of null values
					else{s = reformat(s, monthFirstDateFormat.get(j-1));}									//reformat decimals and dates
					ret[i][j - 1] = s;
				}
				i++;
			}
		} catch (SQLException e) {
			throw e;
		}
		return ret;
	}

	//reformats a result set entry, standardizing it if it is a date, or truncating it if it is a long decimal
	private String reformat(String s, Boolean isMonthFirst) {
		DateFormat dfStandardized = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		//Convert decimal values to standardized form
		if (s.matches("-?\\d*\\.\\d+(E(-?)\\d+)?")) {
			try {
				s = new BigDecimal(s).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
				return s;
			}
			catch(Exception e){}

			//Convert default getString(Date) and getString(TimeStamp) format to standardized format
		} else if (s.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.*")) {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				Date thisDate = dateFormat.parse(s);
				return dfStandardized.format(thisDate);

			} catch (ParseException e) {}

			//Convert default getString(TO_CHAR(TimeStamp)) format to standardized format
		} else if (s.matches("\\d{2}-[a-zA-z]{3,9}-\\d{2} \\d{2}.\\d{2}.\\d{2}.*(AM|PM)")) {
			DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy hh.mm.ss.S a");
			try {
				Date thisDate = dateFormat.parse(s);
				return dfStandardized.format(thisDate);

			} catch (ParseException e) {}

			//convert dd-MON-yy[yy] and dd-MONTH-yy[yy] to standardized format.
		} else if (s.matches("(\\d|\\d{2})\\s*(-|,|\\s+|\\\\|/|:|\\.|\\|)\\s*\\w{3,9}\\s*(-|,|\\s+|\\\\|/|:|\\.|\\|)\\s*\\d{2,4}")) {
			s = s.replaceAll("(\\s-\\s|\\s*,\\s*|\\s*\\\\\\s*|\\s*/\\s*|\\s*:\\s*|\\s*\\.\\s*|\\||\\s+)", "-");
			DateFormat df = new SimpleDateFormat("dd-MMM-yy");
			try {
				Date thisDate = df.parse(s);
				return dfStandardized.format(thisDate);
			} catch (Exception e) {}

			//convert dd-MM-yy[yy] and MM-dd-yy[yy] to standardized format.
		} else if (s.matches("(\\d|\\d{2})\\s*(-|,|\\s+|\\\\|/|:|\\.|\\|)\\s*(\\d|\\d{2})\\s*(-|,|\\s+|\\\\|/|:|\\.|\\|)\\s*\\d{2,4}")) {
			s = s.replaceAll("(\\s-\\s|\\s*,\\s*|\\s*\\\\\\s*|\\s*/\\s*|\\s*:\\s*|\\s*\\.\\s*|\\||\\s+)", "-");
			DateFormat df;
			if(isMonthFirst) df = new SimpleDateFormat("MM-dd-yy");
			else df = new SimpleDateFormat("dd-MM-yy");
			try {
				Date thisDate = df.parse(s);
				return dfStandardized.format(thisDate);
			} catch (Exception e) {}

			//convert MON-dd-yy[yy] and MONTH-dd-yy[yy] to standardized format.
		} else if (s.matches("[a-zA-z]{3,9}\\s*(-|,|\\s+|\\\\|/|:|\\.|\\|)\\s*(\\d|\\d{2})\\s*(-|,|\\s+|\\\\|/|:|\\.|\\|)\\s*\\d{2,4}")) {
			s = s.replaceAll("(\\s-\\s|\\s*,\\s*|\\s*\\\\\\s*|\\s*/\\s*|\\s*:\\s*|\\s*\\.\\s*|\\||\\s+)", "-");
			DateFormat df = new SimpleDateFormat("MMM-dd-yy");
			try {
				Date thisDate = df.parse(s);
				return dfStandardized.format(thisDate);
			} catch (Exception e) {}

			//convert yyyy-dd-MM and yyyy-MM-dd to standardized format.
		} else if (s.matches("\\d{4}\\s*(-|,|\\s+|\\\\|/|:|\\.|\\|)\\s*\\d{1,2}\\s*(-|,|\\s+|\\\\|/|:|\\.|\\|)\\s*\\d{1,2}")) {
			s = s.replaceAll("(\\s-\\s|\\s*,\\s*|\\s*\\\\\\s*|\\s*/\\s*|\\s*:\\s*|\\s*\\.\\s*|\\||\\s+)", "-");
			DateFormat df;
			if(isMonthFirst) df = new SimpleDateFormat("yyyy-MM-dd");
			else df = new SimpleDateFormat("yyyy-dd-MM");
			try {
				Date thisDate = df.parse(s);
				return dfStandardized.format(thisDate);
			} catch (Exception e) {}

			//convert yyyy-MON-dd and yyyy-MONTH-dd to standardized format.
		} else if (s.matches("\\d{4}\\s*(-|,|\\s+|\\\\|/|:|\\.|\\|)\\s*[a-zA-z]{3,9}\\s*(-|,|\\s+|\\\\|/|:|\\.|\\|)\\s*\\d{1,2}")) {
			s = s.replaceAll("(\\s-\\s|\\s*,\\s*|\\s*\\\\\\s*|\\s*/\\s*|\\s*:\\s*|\\s*\\.\\s*|\\||\\s+)", "-");
			DateFormat df = new SimpleDateFormat("yyyy-MMM-dd");
			try {
				Date thisDate = df.parse(s);
				return dfStandardized.format(thisDate);
			} catch (Exception e) {}

			//convert yyyy-dd-MON and yyyy-dd-MONTH to standardized format.
		} else if (s.matches("\\d{4}\\s*(-|,|\\s+|\\\\|/|:|\\.|\\|)\\s*\\d{1,2}\\s*(-|,|\\s+|\\\\|/|:|\\.|\\|)\\s*[a-zA-z]{3,9}")) {
			s = s.replaceAll("(\\s-\\s|\\s*,\\s*|\\s*\\\\\\s*|\\s*/\\s*|\\s*:\\s*|\\s*\\.\\s*|\\||\\s+)", "-");
			DateFormat df = new SimpleDateFormat("yyyy-dd-MMM");

			try {
				Date thisDate = df.parse(s);
				return dfStandardized.format(thisDate);
			} catch (Exception e) {}
		}
		return s;
	}

	//Attempts to create a column association between two queries based off of column names
	private Map<Integer, ArrayList<Integer>> attemptColumnAssociation(String desiredQuery, String givenQuery){
		try {
			//delete comments in both queries
			desiredQuery = deleteComments(desiredQuery);
			givenQuery = deleteComments(givenQuery);

			//Maps a desired column to all potential given columns
			Map<Integer, ArrayList<Integer>> columnMappings = new HashMap<Integer, ArrayList<Integer>>();

			//parse desired query and extract information
			ParseResult desiredColumnInfo = parseSelectStatement(desiredQuery, null, true, "desired");
			ArrayList<String> desiredColumnNames = desiredColumnInfo.getColumnNameList();
			desiredMonthFirstFormat = desiredColumnInfo.getMonthDateFormatsList();
			desiredColumnNameEquivalenceGraph = desiredColumnInfo.getColNameEquivalenceGraph();

			//parse given query and extract information.
			ParseResult givenColumnInfo = parseSelectStatement(givenQuery, null, true, "given");
			ArrayList<String> givenColumnNames = givenColumnInfo.getColumnNameList();
			givenMonthFirstFormat = givenColumnInfo.getMonthDateFormatsList();
			givenColumnNameEquivalenceGraph = givenColumnInfo.getColNameEquivalenceGraph();

			//for each desired column name, find all equivalent given column names and add them to mapping.
			boolean found;
			for(int i = 0; i < desiredColumnNames.size(); i++){
				found = false;
				for(int j = 0; j < givenColumnNames.size(); j++){
					if(areEqualColumns(desiredColumnNames.get(i), givenColumnNames.get(j))){
						if(!columnMappings.containsKey(i)) {
							ArrayList<Integer> newList = new ArrayList<Integer>();
							newList.add(j);
							columnMappings.put(i, newList);
						} else {
							columnMappings.get(i).add(j);
						}
						System.out.println("Desired index " + i + " matches with given index " + j);
						found = true;
					}
				}

				//If a desired column finds no match, exit
				if(!found){
					System.out.println("Desired index " + i + " has no match.");
					System.out.println("NO COLUMN ASSOCIATION WAS FOUND");
					return null;
				}
			}
			System.out.println("COLUMN ASSOCIATION FOUND");
			return columnMappings;
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("NO COLUMN ASSOCIATION WAS FOUND");
			return null;
		}
	}

	//Test if strings are equal
	//Otherwise test if strings are connected based on JOIN operations.
	private boolean areEqualColumns(String s1, String s2){
		if(s1.equals(s2)){
			return true;
		}
		else if(s1.contains(":") && s2.contains(":") && s1.substring(0, s1.lastIndexOf(':')).equals(s2.substring(0, s2.lastIndexOf(':')))
				&& (s1.charAt(s1.length() - 1) == '*' || s2.charAt(s2.length() - 1) == '*')){
			return true;
		}
		else if(stripColumnName(s1).equals(stripColumnName(s2)) &&
				(areConnected(stripColumnFunctions(s1), stripColumnFunctions(s2), givenColumnNameEquivalenceGraph)
						|| areConnected(s1, s2, desiredColumnNameEquivalenceGraph))){
			return true;
		}
		return false;

	}



	/**	Will record all information related to a SELECT statement's columns, including names, aliases, and date format.
	 *  If SELECT statement has corresponding subselect statements, function will be called recursively to record subselect column information
	 *  in which case subselect column's information will be used to resolve any ambiguities present in original select statement.
	 *
	 * @param query			Query being considered containing at least one SELECT statement and possibly subselect statements.
	 * @param queryAlias	If being called recursively (due to existence of subselect), will hold information regarding subselect's alias (if any)
	 * @param firstCall		Simple boolean variable to record whether or not function is currently being used recursively
	 * @param ownership		"desired" if desired query is being parsed. "given" if given query is being parsed.
	 * @return				Returns four ArrayLists containing column name, alias, date format, and subselect alias information
	 */
	private ParseResult parseSelectStatement(String query, String queryAlias, Boolean firstCall, String ownership) throws Exception {
		//Return value is package of four different lists
		//index 0 is columnNames, index 1 is columnAlias, index 2 is columnMonthFirstDateFormat, index 3 is columnSubselectAlias
		ParseResult ret;

		//initialize all lists
		//lists that pertain to column information found in this select statement
		ArrayList<Edge> columnNameEquivalenceGraph = new ArrayList<Edge>();
		ArrayList<Edge> columnNameEquivalenceGraphAliases = new ArrayList<Edge>();
		ArrayList<String> columnNameList = new ArrayList<>();
		ArrayList<String> columnAliasList = new ArrayList<>();
		ArrayList<String> columnPrefixList = new ArrayList<>();
		ArrayList<Boolean> columnMonthFirstDateFormatList = new ArrayList<>();
		ArrayList<String> columnSubselectAliasList = new ArrayList<>();
		//lists that pertain to column information found in all subselect statements that are one level below this one.
		ArrayList<String> columnNameListFunctionStripped = new ArrayList<>();
		ArrayList<String> subselectColumnNameList = new ArrayList<>();
		ArrayList<String> subselectColumnAliasList = new ArrayList<>();
		ArrayList<Boolean> subselectColumnMonthFirstDateFormatList = new ArrayList<>();
		ArrayList<String> subselectColumnSubselectAliasList = new ArrayList<>();
		ArrayList<Edge> subselectColumnNameEquivalenceGraph = new ArrayList<Edge>();

		boolean isSelectAll = false;    //switch for case where SELECT * FROM (...) is found

		String selectToFrom = identifySelectToFrom(query);    //Get SELECT (...) FROM portion of query
		String fromToEnd = identifyFromToEndOrSetOperator(query);        //Get everything that follows the FROM

		//split the SELECT (...) FROM into substrings that each correspond to a column and extract information from each substring
		ArrayList<String> columnSubstrings = splitQuery(selectToFrom);
		for (String columnSubstring : columnSubstrings) {
			String[] columnNameInfo = identifyColumnName(columnSubstring);
			String columnName = columnNameInfo[0];
			String columnNameFunctionStripped = columnNameInfo[1];
			String columnAlias = identifyColumnAlias(columnSubstring, columnNameFunctionStripped);
			String columnPrefix = identifyColumnPrefix(columnSubstring, columnNameFunctionStripped);
			boolean columnMonthFirstDateFormat = isMonthFirstFormattedDate(columnSubstring);

			//if SELECT * FROM is found and this is the FIRST call in the recursive stack,
			// then get column information from ResultSetMetaData object
			if (columnName.equals("*-selectall")) {
				isSelectAll = true;
				if (firstCall) {
					ResultSetMetaData rsmd = ownership.equals("desired") ? rsmdDesired : rsmdGiven;
					try {
						for (int j = 0; j < rsmd.getColumnCount(); j++) {
							columnNameList.add(rsmd.getColumnName(j + 1).toLowerCase());
							columnNameListFunctionStripped.add(null);
							columnAliasList.add(null);
							columnPrefixList.add(null);
							columnSubselectAliasList.add(null);
							columnMonthFirstDateFormatList.add(false);
						}
						//Parse all JOIN statements to find equivalent columns, and add them to equivalency graph
						ArrayList<String> innerJoinStatements = identifyAllInnerJoinStatementOnClauses(fromToEnd);
						for (String innerJoinStatement : innerJoinStatements) {
							if (isValidOnClause(innerJoinStatement)) {
								Edge[] results = identifyEquivalentColumns(innerJoinStatement);
								if (results[1] != null){
									columnNameEquivalenceGraphAliases.add(results[0]);
									columnNameEquivalenceGraph.add(results[1]);
									Edge reversedName = new Edge(results[1].getTwo(), results[1].getOne());
									Edge reversedAlias = new Edge(results[0].getTwo(), results[0].getOne());
									columnNameEquivalenceGraph.add(reversedName);
									columnNameEquivalenceGraphAliases.add(reversedAlias);
								}
							}
						}
						//exit
						ret = new ParseResult(columnNameList, columnAliasList, columnMonthFirstDateFormatList, columnSubselectAliasList, columnNameEquivalenceGraph);
						return ret;
					} catch (SQLException sqle) {
						throw sqle;
					}
				}
			}

			//if not SELECT * FROM, then add all parsed information to corresponding list
			else {
				columnNameList.add(columnName);
				columnNameListFunctionStripped.add(columnNameFunctionStripped);
				columnAliasList.add(columnAlias);
				columnPrefixList.add(columnPrefix);
				columnMonthFirstDateFormatList.add(columnMonthFirstDateFormat);
				columnSubselectAliasList.add(queryAlias);
			}


		}

		//Parse all JOIN statements and add equivalent columns to equivaleny graph
		ArrayList<String> innerJoinStatements = identifyAllInnerJoinStatementOnClauses(fromToEnd);
		for (String innerJoinStatement : innerJoinStatements) {
			if (isValidOnClause(innerJoinStatement)) {
				QueryParseUtil.Edge[] results = identifyEquivalentColumns(innerJoinStatement);
				if (results[1] != null){
					columnNameEquivalenceGraphAliases.add(results[0]);
					columnNameEquivalenceGraph.add(results[1]);
					QueryParseUtil.Edge reversedName = new QueryParseUtil.Edge(results[1].getTwo(), results[1].getOne());
					QueryParseUtil.Edge reversedAlias = new QueryParseUtil.Edge(results[0].getTwo(), results[0].getOne());
					//add reversed edge to graph
					columnNameEquivalenceGraph.add(reversedName);
					columnNameEquivalenceGraphAliases.add(reversedAlias);
				}
			}
		}

		//Identify all nested select statements and for each, extract every column name, column alias, date format and the alias of the subselect itself.
		Map<String, String> nestedSelectStatementList = identifyNestedSelectStatementsToEndOrSetOperator(fromToEnd);
		for (String nestedSelectStatement : nestedSelectStatementList.keySet()) {
			String subselectAlias = nestedSelectStatementList.get(nestedSelectStatement);
			ParseResult nestedSelectInfo = parseSelectStatement(nestedSelectStatement, subselectAlias, false, ownership);
			subselectColumnNameList.addAll(nestedSelectInfo.getColumnNameList());
			subselectColumnAliasList.addAll(nestedSelectInfo.getColumnAliasesList());
			subselectColumnMonthFirstDateFormatList.addAll(nestedSelectInfo.getMonthDateFormatsList());
			subselectColumnSubselectAliasList.addAll(nestedSelectInfo.getSubselectAliasesList());
			subselectColumnNameEquivalenceGraph.addAll(nestedSelectInfo.getColNameEquivalenceGraph());
		}

		//If is SELECT * FROM then set all of the outer select statement's information to be equal to the
		//aggregate information of all corresponding subselects
		if (isSelectAll) {
			columnNameList = subselectColumnNameList;
			columnAliasList = subselectColumnAliasList;
			columnMonthFirstDateFormatList = subselectColumnMonthFirstDateFormatList;
			columnSubselectAliasList = subselectColumnSubselectAliasList;
		}

		//If not SELECT * FROM, then compare outer select statement's information to all subselect statement's information for matching.
		else {
			for (int i = 0; i < columnNameList.size(); i++) {
				for (int j = 0; j < subselectColumnNameList.size(); j++) {
					//If column name matches a subselect's column name's alias
					//and the column name's prefix matches the subselect table alias or the column name's prefix does not exist (thus no threat of ambiguity)
					//then copy all the subselect column's data into outer select's data
					if (columnNameList.get(i).equals(subselectColumnAliasList.get(j))
							&& (columnPrefixList.get(i) == null || columnPrefixList.get(i).equals(subselectColumnSubselectAliasList.get(j)))) {
						columnNameList.set(i, subselectColumnNameList.get(j));
						columnAliasList.set(i, subselectColumnAliasList.get(j));
						if (!columnMonthFirstDateFormatList.get(i)) {
							columnMonthFirstDateFormatList.set(i, subselectColumnMonthFirstDateFormatList.get(j));
						}
					}
					//Does same as above if statement, but for the cases where a function is used on an alias
					else if (columnNameListFunctionStripped.get(i).equals(subselectColumnAliasList.get(j))
							&& (columnPrefixList.get(i) == null || columnPrefixList.get(i).equals(subselectColumnSubselectAliasList.get(j)))) {
						columnNameList.set(i, adjustColumnNameOnly(subselectColumnNameList.get(j), columnNameList.get(i)));
						if (!columnMonthFirstDateFormatList.get(i)) {
							columnMonthFirstDateFormatList.set(i, subselectColumnMonthFirstDateFormatList.get(j));
						}
					}
				}
			}
		}

		//Resolve any column names in equivalence graph according to subselect information, and then add all subselect equivalencies to graph
		for (int i = 0; i < subselectColumnNameList.size(); i++) {
			for (int j = 0; j < columnNameEquivalenceGraph.size(); j++) {
				if(subselectColumnNameList.get(i).equals(columnNameEquivalenceGraph.get(j).getOne())
						&& (columnNameEquivalenceGraphAliases.get(j).getOne().equals("")
						|| columnNameEquivalenceGraphAliases.get(j).getOne().equals(subselectColumnSubselectAliasList.get(i)))) {
					columnNameEquivalenceGraph.get(j).setOne(subselectColumnNameList.get(i));
				}
				if(subselectColumnNameList.get(i).equals(columnNameEquivalenceGraph.get(j).getTwo())
						&& (columnNameEquivalenceGraphAliases.get(j).getTwo().equals("")
						|| columnNameEquivalenceGraphAliases.get(j).getTwo().equals(subselectColumnSubselectAliasList.get(i)))) {
					columnNameEquivalenceGraph.get(j).setTwo(subselectColumnNameList.get(i));
				}
			}
		}

		//Add any joins from subselects to equivalence graph (turned off... could cause problems?)
		//columnNameEquivalenceGraph.addAll(subselectColumnNameEquivalenceGraph);

		//package together column name, alias, date format, and alias of corresponding subselect statement and plug into return list.
		ret = new ParseResult(columnNameList, columnAliasList, columnMonthFirstDateFormatList, columnSubselectAliasList, columnNameEquivalenceGraph);
		return ret;

	}

	//removes column name from a function + column name pair
	//':' character is used as separator between function and column name
	private String stripColumnName(String colName){
		int i = colName.lastIndexOf(':');
		if (i > 0) return colName.substring(i, colName.length());
		else return "";

	}

	//removes a function from a column name + function pair
	//':' character is used as separator between function and column name
	private String stripColumnFunctions(String colName){
		int i = colName.lastIndexOf(':');
		if (i > 0) return colName.substring(0, i+1);
		else return colName;
	}

	//changes a column name to a different name, without changing functions
	private String adjustColumnNameOnly(String newName, String oldName) {
		int quoteCount = 0;
		int colonIndex = -1;
		for (int i = 0; i < oldName.length(); i++) {
			if (oldName.charAt(i) == '"') quoteCount++;
			else if (oldName.charAt(i) == ':' && quoteCount % 2 == 0) {
				colonIndex = i;
			}
		}
		String oldFunc = oldName.substring(0, colonIndex);
		String ret = oldFunc + ":" + newName;
		return ret;
	}

	//checks if a columnString [SELECT | ,] column_logic_here [, | FROM] corresponds to a mm-dd-yyyy or a yyyy-mm-dd format
	private boolean isMonthFirstFormattedDate(String query){
		Pattern pattern = Pattern.compile("(?i)(yyyy|yy)\\s*(-|,|\\s+|\\\\|/|:|\\.|\\|)\\s*MM\\s*(-|,|\\s+|\\\\|/|:|\\.|\\|)\\s*dd");
		Matcher matcher = pattern.matcher(query);
		if(matcher.find()) return true;
		pattern = Pattern.compile("(?i)MM\\s*(-|,|\\s+|\\\\|/|:|\\.|\\|)\\s*dd\\s*(-|,|\\s+|\\\\|/|:|\\.|\\|)\\s*(yyyy|yy)");
		matcher = pattern.matcher(query);
		if(matcher.find()) return true;
		return false;
	}

	//deletes all comments from a query
	private String deleteComments(String query){
		StringBuilder ret = new StringBuilder();

		boolean delete = false;
		for(int i = 0; i < query.length(); i++){
			if(query.charAt(i) == '\n') delete = false;
			else if(query.charAt(i) == '-' && i != query.length() - 1 && query.charAt(i+1) == '-') delete = true;
			if(!delete) ret.append(query.charAt(i));
		}
		return ret.toString();
	}

	private void main(String[] args) {

		//If not at top level of algorithm,
	//	OracleDataAccessObject dao = new OracleDataAccessObject("alfred.cs.uwec.edu", "csdev", "vaugharj", "UZ6TO9P");
	//	TestResultSetEqualContentGood trsec = new TestResultSetEqualContentGood();
		String s =
				"SELECT col1 FROM(SELECT col1 FROM(SELECT col1 FROM TABLE) UNION SELECT COL1 FROM TABLE)" +
						"WHERE col1 IN (SELECT WHERECOL1 FROM TABLE WHERE col1 IN (SELECT WHERECOL2 FROM TABLE)))";
		System.out.println(identifyNestedSelectStatementsToEndOrSetOperator(s));
		System.out.println(identifySubselectStatements(s));


	}






	private static class ParseResult{

		ArrayList<String> colNames;
		ArrayList<String> colAliases;
		ArrayList<Boolean> monthDateFormats;
		ArrayList<String> colSubselectAliases;
		ArrayList<Edge> colNameEquivalenceGraph;
		public ParseResult(ArrayList<String> colNames, ArrayList<String> colAliases, ArrayList<Boolean> monthDateFormats,
						   ArrayList<String> colSubselectAliases, ArrayList<Edge> colNameEquivalenceGraph){
			this.colNames = colNames;
			this.colAliases = colAliases;
			this.monthDateFormats = monthDateFormats;
			this.colSubselectAliases = colSubselectAliases;
			this.colNameEquivalenceGraph = colNameEquivalenceGraph;
		}

		public ArrayList<String> getColumnNameList(){
			return colNames;
		}
		public ArrayList<String> getColumnAliasesList(){
			return colAliases;
		}
		public ArrayList<Boolean> getMonthDateFormatsList(){
			return monthDateFormats;
		}
		public ArrayList<String> getSubselectAliasesList(){
			return colSubselectAliases;
		}
		public ArrayList<Edge> getColNameEquivalenceGraph(){
			return colNameEquivalenceGraph;
		}
	}


}	// end - class TestResultSetEqualContentv1
