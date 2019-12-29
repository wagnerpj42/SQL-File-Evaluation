/* TestResultSetEqualContentv1 - class to test for result set equality regardless of row/column order
 * NOTE: currently testing row ct., column ct., and result string length.  
 *       Cannot directly test column names because people may change names with various aliases
 * 
 * Created - Paul J. Wagner, 2-Oct-2018
 */
package edu.uwec.cs.wagnerpj.sqltest.sqltests;

import edu.uwec.cs.wagnerpj.sqltest.general.IDAO;
import edu.uwec.cs.wagnerpj.sqltest.general.Query;
import edu.uwec.cs.wagnerpj.sqltest.general.ResultSetMetaDataSummary;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("Duplicates")
public class TestResultSetEqualContent implements ISQLTest {
	private int numDuplicates;
	private int numUnmatchedRows;

	private ArrayList<Map<Integer, Integer>> failedMappings = new ArrayList<Map<Integer, Integer>>();
	private ArrayList<String[]> extraRows = new ArrayList<String[]>();
	private ArrayList<String[]> missingRows = new ArrayList<String[]>();
public static ArrayList<Boolean> desiredMonthFirstFormat = new ArrayList<>();
public static ArrayList<Boolean> givenMonthFirstFormat = new ArrayList<>();
	private String[][] desiredResultMatrix;
	private String[][] givenResultMatrix;


	// default constructor
	public TestResultSetEqualContent() {
		// nothing at this time
	}

	// sqlTest - from interface

	//TODO: Make reformatting an option? If so, reformat() should not be called, and we should not check for MM-dd-yyyy and dd-MM-yyyy distinctoin
	//TODO: Make decimal scale an option if we are formatting?
	//TODO: Clean up...
	//TODO: Change return value type so we can return a list of incorrect rows as well as the point total. (Rearrange order of columns when presenting to student?)
	//TODO:			additional information to show student? Extra columns?
	//TODO: Check if MySQL will require modifications to the code?
	//TODO: Attempt to match columns based on column names before attempting to match columns based on content.
	//TODO:			Leverage code in format() to identify date formats from query string.
	//TODO: Possibly improve regex used to match column names
	//TODO: Query-reading part of algorithm can account for functions
	//TODO: Create appropriate jUnit tests and test code extensively

	public int sqlTest(IDAO dao, Query givenQuery, String desiredQueryString) {
		int result = 10;                        // result on scale 0 to 10
		this.numDuplicates = Integer.MAX_VALUE;
		this.numUnmatchedRows = Integer.MAX_VALUE;
		//String givenResultString = "";		// result set string returned from given query
		//String desiredResultString = "";	// result set string returned from desired query
		ResultSet rset = null;                // result set for SQL query
		// summary of result set's metadata
		ResultSetMetaDataSummary summary = new ResultSetMetaDataSummary();
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
		Map<Integer, ArrayList<Integer>> columnAssociations = attemptColumnAssociation(desiredQueryString, givenQuery.toString());
		try {
			rset = dao.executeSQLQuery(givenQuery.toString());
			summary = dao.processResultSet(rset);
			//givenResultString = summary.getResultString();
			rset.beforeFirst();
			givenResultMatrix = resultSetToMatrix(rset, summary.getNumCols(), summary.getNumRows(), givenMonthFirstFormat);
		} catch (Exception e) {
			System.err.println("Error executing SQL/processing result set for given");
			//givenResultString = "given_error";
			givenResultMatrix = new String[0][0];
			System.out.println("RESULT: 0");
			return 0;
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
			desiredResultMatrix = resultSetToMatrix(rset, summary.getNumCols(), summary.getNumRows(), desiredMonthFirstFormat);
		} catch (Exception e) {
			System.err.println("Error executing SQL/processing result set for desired");
			//desiredResultString = "desired_error";
		}
		System.out.println("GIVEN:\n" + Arrays.deepToString(givenResultMatrix) + "\n\n" + "DESIRED:\n" + Arrays.deepToString(desiredResultMatrix) + "\n");
		desiredRowCt = summary.getNumRows();
		desiredColCt = summary.getNumCols();

		//desiredColSet = summary.getColumnSet();

		dao.disconnect();
		rset = null;
		summary = null;


		//if column association(s) successfully identified, use to match rows
		//TODO: what if column association is found but answer won't compile?
				//also check if
		if(columnAssociations != null){
			matchAll(columnAssociations, new boolean[givenColCt], new HashMap<Integer, Integer>(), 0);
		}
		//otherwise attempt to build / test association rules based on resultSet content.
		else matchResultSets();

		if(desiredRowCt != 0){
			result = (int)(((double)desiredRowCt / ((double)(desiredRowCt + numUnmatchedRows + numDuplicates))) * 10.0);
		}
		else if(givenRowCt == 0) result = 10;
		else result = 0;
		if(desiredColCt != givenColCt) result -= 3;

		if(result < 0) result = 0;

		System.out.println("numUnmatched: " + numUnmatchedRows);
		System.out.println("numDuplicates: " + numDuplicates);
		System.out.println("result: " + result);
		System.out.println("\nMissing rows:");
		for(int i = 0; i < missingRows.size(); i++) System.out.println(Arrays.toString(missingRows.get(i)));
		System.out.println("\nExtra rows:" );
		for(int i = 0; i < extraRows.size(); i++) System.out.println(Arrays.toString(extraRows.get(i)));
		return result;



	}    // end - method sqlTest


	// getName - from interface
	public String getName() {
		return ("TestResultSetEqualContentv1");
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
		for (int i = 0; i < givenResultMatrix.length; i++) {
			for(int j = 0; j < desiredResultMatrix.length; j++)
			//for each desired row, see if we can match a given row
				if (matchOne(desiredResultMatrix[j], givenResultMatrix[i],
						new boolean[desiredResultMatrix[0].length], new boolean[givenResultMatrix[0].length],
						new HashMap<Integer, Integer>(), 0, 0, i, j)) {
					return true;
			}
		}

		//if numUnmatchedRows and numDuplicates remain default values, then no desired rows matched with any given rows.
		//set numUnmatched to total row counts of both matrices.
		if(numUnmatchedRows == Integer.MAX_VALUE && numDuplicates == Integer.MAX_VALUE){
			for(int i = 0; i < desiredResultMatrix.length; i ++) missingRows.add(desiredResultMatrix[i]);
			for(int j = 0; j < givenResultMatrix.length; j++)	 extraRows.add(givenResultMatrix[j]);
			numUnmatchedRows = desiredResultMatrix.length + givenResultMatrix.length;
			numDuplicates = 0;
		}

		return false;
	}

	/**
	 * matchOne()
	 * Will conduct a recursive depth-first-search to find all possible column-matching schemas that can equate
	 * the desiredRow and givenRow. Any time a valid column-matching schema is found, matchRest() will be called
	 * to see if all other rows can be matched according to that schema
	 *
	 * @param desiredRow         row in question from desiredResultMatrix
	 * @param givenRow           row in question from givenResultMatrix
	 * @param desiredMarked      memory array to record through each recursive call which indexes of desiredRow have already been matched
	 * @param givenMarked        memory array to record through each recursive call which indexes of givenRow have already been matched
	 * @param columnMapping      a column-matching schema that will be built and sent to matchRest() if a complete schema can successfully be built.
	 * @param numMatched         record of how many matches have been established so that we can determine whether a columnMapping is complete
	 * @param desiredColumnIndex record of column index in question to pass through recursive calls
	 * @param givenRowIndex      record of row index in question to be passed to matchRest() in order to prevent that row from being matched twice
	 * @return True if perfect match. False if not.
	 */
	@SuppressWarnings("Duplicates")
	private boolean matchOne(String[] desiredRow, String[] givenRow, boolean[] desiredMarked, boolean[] givenMarked,
							  HashMap<Integer, Integer> columnMapping,
							  int numMatched, int desiredColumnIndex, int givenRowIndex, int desiredRowIndex) {

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

				//if our column mapping schema accounts for all desired columns and this mapping hasn't previously failed
				//then try to match the rest of the rows
				if (numMatched == desiredRow.length) {
					if (!failedMappings.contains(columnMapping)) {
						if (matchRest(columnMapping, givenRowIndex, desiredRowIndex)) return true;
					}
				}
				//otherwise if column mapping schema is not yet complete, continue building with a recursive call
				else if (matchOne(desiredRow, givenRow, desiredMarked, givenMarked, columnMapping,
						numMatched, desiredColumnIndex + 1, givenRowIndex, desiredRowIndex)) {
					return true; //Bubble up return value through recursive stack
				}

				//Matching failed. Unmark as visited and remove column indexes from our column mapping schema.

				desiredMarked[desiredColumnIndex] = false;
				givenMarked[j] = false;
				numMatched--;
				columnMapping.remove(desiredColumnIndex);
			}
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
	private boolean matchRest(HashMap<Integer, Integer> columnMapping, int givenRowIndex, int desiredRowIndex) {
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
				if (isMatch(givenResultMatrix[j], desiredResultMatrix[i], columnMapping)) {
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
			numUnmatchedRows = 0;
			numDuplicates = 0;
			missingRows = missingRowsTemp;
			extraRows = extraRowsTemp;
			return true;
		//Otherwise not a perfect match. Check if it is best answer found so far, and return false.
		} else if (numOfMissingRows + numOfExtraRows < numUnmatchedRows) {
			numUnmatchedRows = numOfMissingRows + numOfExtraRows;
			numDuplicates = numOfDuplicates;
			missingRows = missingRowsTemp;
			extraRows = extraRowsTemp;
		}
		failedMappings.add(columnMapping);
		return false;
	}

	private void matchAll(Map<Integer, ArrayList<Integer>> columnAssociations, boolean[] markedGivenColumns, HashMap<Integer, Integer> columnMapping, int numCols){

		for(Integer desiredColumn : columnAssociations.keySet()){
			for(Integer givenColumn : columnAssociations.get(desiredColumn)){
				if(markedGivenColumns[givenColumn]) continue;
				columnMapping.put(desiredColumn, givenColumn);
				markedGivenColumns[givenColumn] = true;
				numCols++;
				if(numCols == columnAssociations.size()) matchRest(columnMapping, -1, -1);
				else matchAll(columnAssociations, markedGivenColumns, columnMapping, numCols);
				numCols--;
				markedGivenColumns[givenColumn] = false;
			}
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
	private boolean isMatch(String[] givenRow, String[] desiredRow, HashMap<Integer, Integer> columnMappings) {
		int size = columnMappings.keySet().size();
		for (int i = 0; i < size; i++) {
			if (!desiredRow[i].equals(givenRow[columnMappings.get(i)])) return false;
		}
		return true;
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

	//will check if two dates are equal when we swap the position of their days and months
	//necessary for case, for example, where desired format is dd-MM-yy and given format is MM-dd-yy
	//			 or case, for example, where desired format is yy
	/*public static boolean reversedDateMatches(String s1, String s2){
		if((s1.matches("\\d{2}-\\d{2}-\\d{4}\\s.*") && s2.matches("\\d{2}-\\d{2}-\\d{4}\\s.*"))){
			String day = s1.substring(0, 2);
			String month = s1.substring(3, 5);
			s1 = month + "-" + day + s1.substring(5, s1.length());
			return s1.equals(s2);
		}
		else if((s1.matches("\\d{4}-\\d{2}-\\d{2}\\s.*") && s2.matches("\\d{4}-\\d{2}-\\d{2}\\s.*"))){
			String day = s1.substring(5, 7);
			String month = s1.substring(8, 10);
			s1 = s1.substring(0, 5) + month + "-" + day + s1.substring(10, s1.length());
			return s1.equals(s2);
		}
		else{
			return false;
		}
	}*/



	public static int getScale(String s){
		s = new BigDecimal(s).toString();
		int index = s.indexOf(".") + 1;		//index of first number after radix
		int scale = 2;						//2 = number of digits to show in decimal that aren't preceding 0s
		while(s.charAt(index) == '0'){
			index++; scale++;
		}
		return scale;
	}

	public static String reformat(String s, boolean isMonthFirst) {
		DateFormat dfStandardized = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		//Convert decimal values to standardized form
		if (s.matches("-?\\d*\\.\\d+(E(-?)\\d+)?")) {
			try {
				//int scale = getScale(s);
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


	//BELOW THIS THIS IS EXPERIMENTAL
	//given two queries, returns a mapping of all columns of q2 that can be considered equivalent to a column of q1
	public static Map<Integer, ArrayList<Integer>> attemptColumnAssociation(String desiredQuery, String givenQuery){
		try {
			Map<Integer, ArrayList<Integer>> columnMappings = new HashMap<Integer, ArrayList<Integer>>();
			ArrayList<String> columnNames1 = parseQuery(desiredQuery, desiredMonthFirstFormat);
			ArrayList<String> columnNames2 = parseQuery(givenQuery, givenMonthFirstFormat);

			boolean found;
			for (int i = 0; i < columnNames1.size(); i++) {
				found = false;
				for (int j = 0; j < columnNames2.size(); j++) {
					if (columnNames1.get(i).equals(columnNames2.get(j))) {
						if (!columnMappings.containsKey(i)) {
							ArrayList<Integer> newList = new ArrayList<Integer>();
							newList.add(j);
							columnMappings.put(i, newList);
						} else {
							columnMappings.get(i).add(j);
						}
						found = true;
					}
				}

				if(!found) {
					System.out.println("~~~COLUMN ASSOCIATION NOT FOUND");
					return null;
				}
			}
			System.out.println("\\/\\/\\COLUMN ASSOCIATION FOUND");
			return columnMappings;
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("~~~COLUMN ASSOCIATION NOT FOUND~~~");
			return null;
		}
	}

	public static boolean isMonthFirstFormattedDate(String query){
		Pattern pattern = Pattern.compile("(?i)(yyyy|yy)\\s*(-|,|\\s+|\\\\|/|:|\\.|\\|)\\s*MM\\s*(-|,|\\s+|\\\\|/|:|\\.|\\|)\\s*dd");
		Matcher matcher = pattern.matcher(query);
		if(matcher.find()) return true;
		pattern = Pattern.compile("(?i)MM\\s*(-|,|\\s+|\\\\|/|:|\\.|\\|)\\s*dd\\s*(-|,|\\s+|\\\\|/|:|\\.|\\|)\\s*(yyyy|yy)");
		matcher = pattern.matcher(query);
		if(matcher.find()) return true;
		return false;
	}

	public static ArrayList<String> parseQuery(String query, ArrayList<Boolean> monthFirstDateList){
		try {
			ArrayList<String> columnNameList = new ArrayList<String>();
			//Grab everything between SELECT and FROM inclusive
			Matcher fromMatcher = Pattern.compile("(?i)(?<=(\\s{1,999}|[)]|\"))FROM(?=(\\s{1,999}|[(]|\"))").matcher(query);
			fromMatcher.find();
			Matcher selectMatcher = Pattern.compile("(?i)SELECT(?=(\\s{1,10}|[(]))").matcher(query);
			selectMatcher.find();
			String selectStmt = query.substring(selectMatcher.start(), fromMatcher.end());
			//split select statement at each comma that isn't between parentheses.
			ArrayList<String> columnStrings = splitQuery(selectStmt);
			for (String string : columnStrings) {
				if(isMonthFirstFormattedDate(string)){
					monthFirstDateList.add(true);
					System.out.println(string + " - TRUE");
				}
				else {
					monthFirstDateList.add(false);
					System.out.println(string + " - FALSE");
				}
			}


			for (String columnString : columnStrings) {
				columnNameList.add(identifyColumnName(columnString).toLowerCase());
			}

			return columnNameList;
		}
		catch(Exception e){
			throw e;
		}
	}

	public static ArrayList<String> splitQuery(String query){
		try {
			int numQuotations = 0;
			int numParentheses = 0;
			int start = 0;
			int lastCommaIndex = 0;
			ArrayList<String> ret = new ArrayList<String>();

			for (int i = 0; i < query.length(); i++) {
				if (query.charAt(i) == ',' && numParentheses == 0 && numQuotations % 2 == 0) {
					ret.add(query.substring(start, i) + ',');
					start = i;
					lastCommaIndex = i;
				}
				if (query.charAt(i) == '(') numParentheses++;
				if (query.charAt(i) == ')') numParentheses--;
				if (query.charAt(i) == '\"') numQuotations++;
			}
			ret.add(query.substring(lastCommaIndex));
			return ret;
		}
		catch(Exception e){
			throw e;
		}
	}

	//TODO: integrate functionality for * symbol
	//TODO: support cases where different parameters are used in a function that will have same effect on resultSet. ex) count(*) vs count(col1, col2)
	//TODO: possibly consider any "s as part of variable name (change the regex)
	public static String identifyColumnName(String s){
		try {
			//find word that is preceded (ignoring whitespace) by either a comma, a left parenthesis, a quotation mark, a period
			//		or a SELECT statement which is itself followed by either a space, a quotation mark, or a left parenthesis
			//and is proceeded (ignoring whitespace) by either a comma, a right parenthesis, a quotation mark,
			//		a FROM which is itself preceded by either a space, a right parenthesis, or a quotation mark
			//		or an AS statement which is preceded by either a space or a quotation mark
			//Pattern pattern = Pattern.compile("(?i)(?<=(SELECT(?=\\s|[(]|\")|,|\"|\\.|[(])\\s{0,999})\\w+\\b(?<!\\b(distinct|unique|all))(?=\\s{0,999}((?<=(\\s|[)]|\"))FROM|,|\"|(\\s|\")AS|[)]))");
			Pattern pattern = Pattern.compile("(?i)(?<=(SELECT(?=\\s|[(]|\")|,|\"|\\.|[(])\\s{0,999})((?<!\")\\w+(?!\")\\b(?<!\\b(distinct|unique|all))|(?<=\")[^\"]+(?=\"))(?=\\s{0,999}((?<=(\\s|[)]|\"))FROM|,|\"|(\\s|\")AS|[)]))");
			Matcher matcher = pattern.matcher(s);
			matcher.find();
			return s.substring(matcher.start(), matcher.end());
		}
		catch(Exception e){
			throw e;
		}

	}

	//issues:
	//TODO: Use of * messes with things. Check if * is in a function, in which case treat it as column
	public static void main(String[] args) {

		String query1 = "(SELECT c.COL1, col2 as alias2, to_char(\"Col3\", \"MM \\ dd \\ yyyy\") FROM YADDA";
		String query2 = "SELect COL1, alias.COL2,\"COL3\"AS ALIAS3, TO_CHAR(COL3 AS SOMETHING, \"dd : mm : yyyy\") " +
				"from YADDA";
		Map<Integer, ArrayList<Integer>> columnMappings = attemptColumnAssociation(query1, query2);
		for(Integer key : columnMappings.keySet()){
			System.out.println(columnMappings.get(key));
		}

	}





}	// end - class TestResultSetEqualContentv1
