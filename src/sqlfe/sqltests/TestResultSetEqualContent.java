/* TestResultSetEqualContent - class to test for result set equality regardless of row/column order
 *
 * Created - Paul J. Wagner, 2-Oct-2018
 * Major modifications by Ryan Vaughn, ??-Jan-2020 - column matching, date formats, real formats and precision - see README
 */
package sqlfe.sqltests;

import sqlfe.general.*;
import sqlfe.util.QueryParseUtil;

import static sqlfe.util.QueryParseUtil.*;

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

/**		README:
 *
 * 		TestResultSetEqualContent will compare a student's result set to the solution result set and determine
 * 		a student's score according to a ratio between # of correct rows and # of incorrect (extra or missing) rows.
 *
 *		In order to to this, the following operations will be performed.
 *			(1) Student's and Solution's queries will be executed through JDBC and their ResultSet object instances will be acquired.
 *			(2) Both ResultSet objects will be processed into a matrices. Some data entries will be reformatted during this step.
 *				  -Dates will be reformatted into a standardized format so that we can more easily compare them.
 * 				  -Long decimals will be truncated to two decimal places so we can more easily compare tables with different rounding strategies.
 * 			(3) In order to compare result sets, we need to know which column of the student's result set corresponds to which
 * 				column in the solution result set. We will *attempt* to do this by parsing the query strings and identify
 * 				column names in both queries, and see if we can match columns based off of their column names
 * 				  -We will also take into consideration any JOIN'ed columns so that, for example, a foreign key
 * 				  can be matched with it's corresponding primary key and vice versa.
 * 				  -It is possible that a column name corresponds to an alias name defined in a subselect statement.
 * 				  		e.g SELECT aliasName FROM (SELECT col1 as aliasName))
 * 				  In this case, an attempt to retrieve the original column name will be made by parsing subselect statements.
 * 			(4) If (3) succeeds, we will see how many rows we can match between both result sets according to any column associations found.
 *			(5) We will now employ a different strategy to match rows between the result sets. We will attempt to find ONE example
 *				of two rows that can be matched between the result sets, and we will store in memory a mapping of which columns
 *				mapped to each other during the matching. Any time a match is found, we will attempt to match all other rows
 *				according to the column mapping used to match the first two rows. If a perfect match is found, we are done,
 *				otherwise we will keep searching for possible matches and keep track of the best case.
 *
 *
 *		  	As seen above, two strategies are employed in attempt to match rows between desired and solution result sets.
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
 *		 Both strategies should hypothetically (and in almost all cases, will) will calculate the same score for a given
 *		 answer to a given question. However, there are a few edge cases where strategy 2 can fire a false-positive (answer
 *		 was incorrect but marked correct), and potentially some cases where strategy 1 can misfire as well. These cases
 *		 are rare, but to maximize correctness both strategies will be executed. This way, if the strategies
 *		 result in two different scores we can issue a warning to the professor in a warnings file so that the
 *		 answer can be investigated.
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

public class TestResultSetEqualContent implements ISQLTest {
	private int numDuplicates;															//total number of student's duplicate rows
	private int numUnmatchedRows;														//total number of student's strictly incorrect rows

	private ArrayList<Map<Integer, Integer>> failedMappings = new ArrayList<Map<Integer, Integer>>();		//attempted column mappings performed by algorithm
	private ArrayList<Map<Integer, Integer>> failedMappingTemp = new ArrayList<Map<Integer, Integer>>();	//temporary storage for failed mappings
	private boolean columnAssociationCompleted = false;									//switch-variable for if query-parse was successful
	private ArrayList<Edge> givenColumnNameEquivalenceGraph = new ArrayList<Edge>();	//student's graph to determine equivalent column names
	private ArrayList<Edge> desiredColumnNameEquivalenceGraph = new ArrayList<Edge>();	//solution's graph to determine equivalent column names
	private ArrayList<String[]> extraRows = new ArrayList<String[]>();							//extra rows (student provided, but should not have)
	private ArrayList<String[]> missingRows = new ArrayList<String[]>();						//missing rows (student did not provide, but should have)
	private ArrayList<Boolean> desiredMonthFirstFormat = new ArrayList<>();				//boolean arrays for if a column contains "yyyy-mm-dd"
	private ArrayList<Boolean> givenMonthFirstFormat = new ArrayList<>();					//or "mm-dd-yy[yy] date formats
	private String[][] desiredResultMatrix;												//Array to hold solution's result set entries
	private String[][] givenResultMatrix;												//Array to hold student's result set entries
	private ResultSetMetaData rsmdGiven = null;											//Student's result set's meta data
	private ResultSetMetaData rsmdDesired = null;										//Solution's result set's meta data
	private TestResult testResult = new TestResult();									//return value for ISQLTest()


	// default constructor
	public TestResultSetEqualContent() {
		// nothing at this time
	}

	// sqlTest - from interface


	public TestResult sqlTest(IDAO dao, Query givenQuery, String desiredQueryString){
		//System.out.println("entering TestResultSetEqualContent class, sqlTest() method");
		int result = 10;                        // result on scale 0 to 10
		this.numDuplicates = Integer.MAX_VALUE;	 //We will be looking for minimum numUnmatched and numDuplicates, so set to MAX_VALUE initially
		this.numUnmatchedRows = Integer.MAX_VALUE;

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

		// 1) execute given query, get resultSet and metadata
		try {
			rsetGiven = dao.executeSQLQuery(givenQuery.toString());
			rsmdGiven = rsetGiven.getMetaData();
			summaryGiven = dao.processResultSet(rsetGiven);
			rsetGiven.beforeFirst();	//move result set cursor to start
		} catch (Exception e) {
			//System.err.println("Error in testResultSetEqualContent on executing SQL/processing result set for given query");
			//System.err.println(e.getMessage());
			//System.out.println("RESULT: 0");
			return new TestResult(0);
		}
		//get row and column counts
		givenRowCt = summaryGiven.getNumRows();
		givenColCt = summaryGiven.getNumCols();
		//System.out.println("submitted/given query: " + givenRowCt + " rows, " + givenColCt + " cols");

		// 2) execute desired query
		try {
			rsetDesired = dao.executeSQLQuery(desiredQuery.toString());
			rsmdDesired = rsetDesired.getMetaData();
			summaryDesired = dao.processResultSet(rsetDesired);
			rsetDesired.beforeFirst();
		} catch (Exception e) {
			//System.err.println("Error executing SQL/processing result set for desired query");
		}

		desiredRowCt = summaryDesired.getNumRows();
		desiredColCt = summaryDesired.getNumCols();
		//System.out.println("desired query: " + desiredRowCt + " rows, " + desiredColCt + " cols");

		//Attempt matching student and solution's columns based off of column names found in queries
		Map<Integer, ArrayList<Integer>> columnAssociations = attemptColumnAssociation(desiredQueryString, givenQuery.toString());

		//Convert student and solution's result sets to a matrix.
		try{
			desiredResultMatrix = convertResultSetToMatrix(rsetDesired, desiredColCt, desiredRowCt, desiredMonthFirstFormat);
		}
		catch(Exception e){
			//System.out.println("Could not process desired matrix.");
			System.err.println("Could not process desired result set matrix.");
		}
		
		try {
			givenResultMatrix = convertResultSetToMatrix(rsetGiven, givenColCt, givenRowCt, givenMonthFirstFormat);
		}
		catch(Exception e){
			//System.out.println("Could not process given matrix.\nRESULT: 0");
			return new TestResult(0);
		}
		//System.out.println("GIVEN:\n" + Arrays.deepToString(givenResultMatrix) + "\n\n" + "DESIRED:\n" + Arrays.deepToString(desiredResultMatrix) + "\n");


		//if student and solution's column indexes successfully matched based off of column names, then use the
		//mapping to find number of matching rows between the two result sets.
		if(columnAssociations != null){
			matchResultSetsWithColumnAssociations(columnAssociations, new boolean[givenColCt], new HashMap<Integer, Integer>(), 0, 0);
			if(numUnmatchedRows != Integer.MAX_VALUE && numDuplicates != Integer.MAX_VALUE) {
		        columnAssociationCompleted=true;
			}
		}
		//Find number of matching rows between student and solution result sets, using column content to match the columns.
		matchResultSets();

		//calculate final score
		//System.out.println("TRSEC - result before calculation: " + result);
		int numMatched = desiredRowCt - numUnmatchedRows - numDuplicates;
		//System.out.println("TRSEC -- numMatched is      : " + numMatched + ", calculated " + desiredRowCt + " - " + numUnmatchedRows + " - " + numDuplicates);
		//System.out.println("TRSEC -- numUnmatchedRows is: " + numUnmatchedRows);
		//System.out.println("TRSEC -- numDuplicates is   : " + numDuplicates);
		//System.out.println("TRSEC ----- desiredRowCt is : " + desiredRowCt);
		if(desiredRowCt != 0){
			result = (int)(((double)numMatched / ((double)(numMatched + 3 * (numUnmatchedRows + numDuplicates))) * 10.0));
			//System.out.println("TRSEC - result after initial division calculation: " + result);
		}
		else if(givenRowCt == 0) result = 10;
		else result = 0;
		
		if(result != 10) result -= 2;
		
		// deduction for different column counts
		if(desiredColCt != givenColCt) result -= 3;

		// cannot set result lower than zero
		if(result < 0) result = 0;
		
		//System.out.println("TRSEC - result after calculation: " + result);

		/*System.out.println("numUnmatched: " + numUnmatchedRows);
		System.out.println("numDuplicates: " + numDuplicates);
		System.out.println("result: " + result);
		System.out.println("\nMissing rows:");*/
		//for(int i = 0; i < missingRows.size(); i++) {
		//	System.out.println(Arrays.toString(missingRows.get(i)));
		//}
		//System.out.println("\nExtra rows:" );
		//for(int i = 0; i < extraRows.size(); i++) {
		//	System.out.println(Arrays.toString(extraRows.get(i)));
		//}

		//load values into TestResult object and return
		testResult.setScore(result);
		testResult.setExtraRows(extraRows);
		testResult.setMissingRows(missingRows);
		return testResult;
	}    // end - method sqlTest


	// getName - from interface, return name of test
	public String getName() {
		return ("TestResultSetEqualContent");
	}

	// getDesc - from interface, return full description of test 
	public String getDesc() {
		return "Answer has same result set content as desired query";
	}


	/**
	 * matchResultSets()
	 * Will attempt to match student rows and solution rows. Will return true if a perfect match can be found,
	 * otherwise will continue searching for possible matches, and return false if no perfect match was ultimately found.
	 * Call to matchOne() will be used to determine if two rows match. If so, matchOne() will call matchRest() to determine
	 * if all other rows can be matched according to the same column-mapping schema used to find the first match.
	 */
	private boolean matchResultSets() {
		//if either array is empty, return true if both are empty and false if only one is non-empty
		//also populate unmatched rows array and determine number of incorrect rows (equal to number of rows in the non-empty result set)
		//System.out.println("TRSEC - matchResultSets() - 1) before first if");
		if (desiredResultMatrix.length == 0 || givenResultMatrix.length == 0) {
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

		//for each desired row, see if we can match it with a given row,
		//System.out.println("TRSEC - matchResultSets() - 2) before double for loops");
		for (int i = 0; i < givenResultMatrix.length; i++) {
			for(int j = 0; j < desiredResultMatrix.length; j++)
				//hand control flow to matchOne(), which will check if rows match, and if so, call matchRest() to see if all other rows
				//	can be matched
				if (matchOne(desiredResultMatrix[j], givenResultMatrix[i],
						new boolean[desiredResultMatrix[0].length], new boolean[givenResultMatrix[0].length],
						new HashMap<Integer, Integer>(), 0, 0, i, j, new boolean[givenResultMatrix[0].length])) {
					//System.out.println("TRSEC - matchResultSets() - 2) before return true");
					return true;
				}
		}

		//if numUnmatchedRows and numDuplicates remain default values (MAX_VALUE), then no desired rows have matched with any given rows.
		//set numUnmatched to total row counts of both matrices to reflect this
		//System.out.println("TRSEC - matchResultSets() - 3) before last if");
		if(numUnmatchedRows == Integer.MAX_VALUE && numDuplicates == Integer.MAX_VALUE){
			for(int i = 0; i < desiredResultMatrix.length; i ++) missingRows.add(desiredResultMatrix[i]);
			for(int j = 0; j < givenResultMatrix.length; j++)	 extraRows.add(givenResultMatrix[j]);
			numUnmatchedRows = desiredResultMatrix.length + givenResultMatrix.length;
			numDuplicates = 0;
		}

		return false;
	}	// end - method matchResultSets


	/**
	 * Will attempt to match a student's row and a solution row, keeping track of which column indexes are being matched.
	 * If the rows match, then matchRest() will be called to determine how many other rows in the result sets can be
	 * matched according to the same index-association used to make the first match.
	 */
	@SuppressWarnings("unchecked")
	private boolean matchOne(String[] desiredRow, String[] givenRow, boolean[] desiredMarked, boolean[] givenMarked,
							 HashMap<Integer, Integer> columnMapping,
							 int numMatched, int desiredColumnIndex, int givenRowIndex, int desiredRowIndex, boolean[] isReversedDate) {
		//System.out.println("TRSEC - entering matchOne()");
		//Base case
		if (desiredColumnIndex == desiredRow.length) return false;
		for (int j = 0; j < givenRow.length; j++) {
			//if we can match two unmarked elements...
			if (givenRow[j].equals(desiredRow[desiredColumnIndex]) && !desiredMarked[desiredColumnIndex] && !givenMarked[j]) {
				//Mark as visited and add column indexes to our column mapping schema schema.
				desiredMarked[desiredColumnIndex] = true;
				givenMarked[j] = true;
				columnMapping.put(desiredColumnIndex, j);
				numMatched++;

				//System.out.println("TRSEC - matchOne - before if test and call to matchRest(), or else with recursive call to matchOne()");
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
	}	// end - method matchOne

	
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

		//System.out.println("TRSEC - entering matchRest()");
		//System.out.println("TRSEC, matchRest() - numDuplicates is: " + numDuplicates);
		
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
		//System.out.println("TRSEC - matchRest() - middle, numDuplicates is: " + numDuplicates);
		//System.out.println("TRSEC - matchRest() - middle, numOfDuplicates is: " + numOfDuplicates);
		//System.out.println("TRSEC - matchRest() - middle, numOfExtraRows is: " + numOfExtraRows);
		//System.out.println("TRSEC - matchRest() - middle, numOfMissingRows is: " + numOfMissingRows);
		//System.out.println("TRSEC - matchRest() - middle, sum of last three is: " + (numOfExtraRows + numOfMissingRows + numOfDuplicates));
		if (numOfExtraRows + numOfMissingRows + numOfDuplicates == 0) {
			//If columnAssociation was completed and we found a better score than it, issue a warning to Professor
			if(columnAssociationCompleted && (numUnmatchedRows != Integer.MAX_VALUE && numDuplicates != Integer.MAX_VALUE) && (numUnmatchedRows + numDuplicates) != 0) {
				testResult.addWarning(TestResult.MISMATCHED_SCORE_WARNING);
				//System.out.println("TRSEC, matchRest() - perfect match, numDuplicates is: " + numDuplicates);
				//System.out.println("TRSEC, matchRest() - perfect match with warning, before return true");
				return true;
			}
			else{
				numUnmatchedRows = 0;
				numDuplicates = 0;
				missingRows = missingRowsTemp;
				extraRows = extraRowsTemp;
				//System.out.println("TRSEC, matchRest() - perfect match no warning, before return true");
				return true;
			}

			//Otherwise not a perfect match. Check if it is best answer found so far, and return false.
		} else if (numOfMissingRows + numOfExtraRows < numUnmatchedRows) {
			//If columnAssociation was already completed, issue warning to professor.
			if(columnAssociationCompleted){
				//System.out.println("TRSEC - matchRest() - not a perfect match, warning to be issued");
				testResult.addWarning(TestResult.MISMATCHED_SCORE_WARNING);
			}
			else{
				//System.out.println("TRSEC - matchRest() - not a perfect match, no warning needed");
				numUnmatchedRows = numOfMissingRows + numOfExtraRows;
				numDuplicates = numOfDuplicates;
				missingRows = missingRowsTemp;
				extraRows = extraRowsTemp;
			}

		}
		//System.out.println("TRSEC = matchRest() - not perfect match, before return false");
		return false;
	}

	/**	matchResultSetsWithColumnAssociations() will attempt to match all rows of student result set with all rows of solution result set
	 * according to all possible column-matching schemas found by the attemptColumnAssociation() method if at least one was found
	 * Will adjust instance variables numDuplicates, numUnmatchedRows to determine correctness of student result set.
	 */
	private void matchResultSetsWithColumnAssociations(Map<Integer, ArrayList<Integer>> columnAssociations, boolean[] markedGivenColumns, HashMap<Integer, Integer> columnMapping, int numCols, int currDesiredCol){
		//System.out.println("TRSEC - matchResultSetsWithColumnAssociations - entering");
		//System.out.println("TRSEC - matchResultSetsWithColumnAssociations - numDuplicates is: " + numDuplicates);
		for(Integer givenColumn : columnAssociations.get(currDesiredCol)){
			//if givenColumn has already been accounted for, try next
			if(markedGivenColumns[givenColumn]) continue;
			//else add this column to our column mapping and mark as visited
			columnMapping.put(currDesiredCol, givenColumn);
			markedGivenColumns[givenColumn] = true;
			numCols++;
			//if column mapping is complete, then try to matchRest()
			if(numCols == columnAssociations.size()) matchRest(columnMapping, -1, -1, new boolean[markedGivenColumns.length]);
			else matchResultSetsWithColumnAssociations(columnAssociations, markedGivenColumns, columnMapping, numCols, currDesiredCol + 1);
			numCols--;
			markedGivenColumns[givenColumn] = false;
		}
	}	// end - method matchResultSetsWithColumnAssociations

	
	/**
	 * isMatch()
	 * Attempts to match two rows according to a given column-mapping schema
	 * If date formats need to be swapped (dd-mm-yyyy to mm-dd-yyyy etc OR yyyy-mm-dd to yyyy-dd-mm) then swap will occur
	 * before determining equality
	 */

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

	/**
	 * Determines if two result set entries can be matched if the month and day positions on a date format are switched.
	 * Used by matchResultSet() call stack to differentiate between a dd-mm-yy[yy] and mm-dd-yy[yy] date format
	 * as well as a yyyy-mm-dd and yyyy-dd-mm date format
	 */
	private boolean reversedDateMatches(String s1, String s2) {
		//if string looks like dd-mm-yyyy or mm-dd-yyyy, switch positions of mm and dd on just one of the strings and compare
		if ((s1.matches("\\d{2}-\\d{2}-\\d{4}\\s.*") && s2.matches("\\d{2}-\\d{2}-\\d{4}\\s.*"))) {
			String day = s1.substring(0, 2);
			String month = s1.substring(3, 5);
			s1 = month + "-" + day + s1.substring(5, s1.length());
			return s1.equals(s2);

		//if string looks like yyyy-mm-dd or yyyy-dd-mm, switch positions of mm and dd on just one of the strings and compare
		} else if ((s1.matches("\\d{4}-\\d{2}-\\d{2}\\s.*") && s2.matches("\\d{4}-\\d{2}-\\d{2}\\s.*"))) {
			String day = s1.substring(5, 7);
			String month = s1.substring(8, 10);
			s1 = s1.substring(0, 5) + month + "-" + day + s1.substring(10, s1.length());
			return s1.equals(s2);
		} else {
			return false;
		}
	}

	/**
	 * 	Converts a result set into a matrix
	 *  Any dates will be reformatted into a "dd-mm-yyyy HH:mm:ss" timestamp to make comparisons easier
	 *  Any long decimals will be truncated to two decimal places to make comparisons easier
	 */
	private String[][] convertResultSetToMatrix(ResultSet resultSet, int numCols, int numRows, ArrayList<Boolean> monthFirstDateFormat) throws SQLException {
		String[][] ret = null;
		try {
			//create matrix with same proportions as the result set
			ret = new String[numRows][numCols];
			int i = 0;
			//iterate through the result set, reformatting each entry (if applicable) and add it to our matrix
			while (resultSet.next()) {
				for (int j = 1; j <= numCols; j++) {
					String s = resultSet.getString(j);                                //grab string
					if (resultSet.wasNull()) s = "NULL";                    //take care of null values
					else{s = reformatResultSetEntry(s, monthFirstDateFormat.get(j-1));}									//reformat decimals and dates
					ret[i][j - 1] = s;
				}
				i++;
			}
		} catch (SQLException e) {
			throw e;
		}
		return ret;
	}

	/**
	 * 	Reformats a result set entry, standardizing it to "dd-mm-yyyy HH:mm:ss" formatted timestamp if it is a date,
	 * 	or truncating it if it is a long decimal.
	 */
	private String reformatResultSetEntry(String s, Boolean isMonthFirst) {
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

	/**	Will attempt to parse both queries and determine if columns can be mapped between the queries based off of
	 *  column names and/or JOINED columns
	 */
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
						//System.out.println("Desired index " + i + " matches with given index " + j);
						found = true;
					}
				}

				//If a desired column finds no match, exit
				if(!found){
					//System.out.println("Desired index " + i + " has no match.");
					//System.out.println("NO COLUMN ASSOCIATION WAS FOUND");
					return null;
				}
			}
			//System.out.println("COLUMN ASSOCIATION FOUND");
			return columnMappings;
		}
		catch(Exception e){
			//e.printStackTrace();		 // should not normally get here, and falls back to second algorithm anyway, so only use for developer debugging
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



	/**	Will return all information related to a SELECT statement's columns, including names, aliases, and date format.
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
		Map<String, String> nestedSelectStatementList = identifySubSelectStatementsToEndOrSetOperator(fromToEnd);
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

	//changes ONLY the column name without modifying functions used on the column
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

	//checks if a columnString corresponds to a mm-dd-yyyy or a yyyy-mm-dd format
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

	//Utility function that checks if an on clause is comparing columns on equality rather than inequality.
	private boolean isValidOnClause(String onClause){
		int numQuotes = 0;
		for(int i = 0; i < onClause.length(); i++){
			if(onClause.charAt(i) == '\"') numQuotes++;
			else if ((onClause.charAt(i) == '>' || onClause.charAt(i) == '<' || onClause.charAt(i) == '!') && numQuotes % 2 == 0) return false;
			else if(onClause.charAt(i) == '=' && numQuotes % 2 == 0) return true;
		}

		return false;
	}


	/**
	 * Return value for parseSelectStatement() method
	 */
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


}	// end - class TestResultSetEqualContent
