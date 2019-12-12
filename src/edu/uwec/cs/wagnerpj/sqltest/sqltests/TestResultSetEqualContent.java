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
import java.time.Instant;
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
	private ArrayList<Map<Integer, Integer>> failedMappings = new ArrayList<Map<Integer, Integer>>();
	private String[][] desiredResultMatrix;
	private String[][] givenResultMatrix;

	// default constructor
	public TestResultSetEqualContent() {
		// nothing at this time
	}

	// sqlTest - from interface
	public int sqlTest(IDAO dao, Query givenQuery, String desiredQueryString) {
		int result = 10;                        // result on scale 0 to 10
		this.numDuplicates = Integer.MAX_VALUE;
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

		try {
			rset = dao.executeSQLQuery(givenQuery.toString());
			summary = dao.processResultSet(rset);
			//givenResultString = summary.getResultString();
			rset.beforeFirst();
			givenResultMatrix = resultSetToMatrix(rset, summary.getNumCols(), summary.getNumRows());
		} catch (Exception e) {
			System.err.println("Error executing SQL/processing result set for given");
			e.printStackTrace();
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

		if (matchResultSets()) {
			//do not deduct any points (do nothing)
		} else if (numDuplicates == Integer.MAX_VALUE) {
			result = 0;    //if matchResultSets() returns false and numDuplicates is unchanged, answer is strictly wrong
		} else
			result -= 5;    //amount of points to deduct if answer contains (or is missing) duplicates but is otherwise correct

		if (desiredColCt != givenColCt) {
			result -= 3;    //amount of points to deduct if answer contains extra columns
		}

		if (result < 0) result = 0;

		System.out.println("result: " + result);
		return result;
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

	//TODO: Allow algorithm to count numExtra, numMissing, numExtraDuplicates, numMissingDuplicates
	//		Mappings and failed mappings must account for swapped date orders in DD-MM-YYYY / MM-DD-YYYY cases.
	//		Make sure that if a date can be matched both ways (example 01-01-2019), that both are tested in matchRest().
	//		Keep track of minimum unmatched rows. Exceeding this minimum is early exit condition.
	private boolean matchResultSets() {
		if (desiredResultMatrix.length == 0 || givenResultMatrix.length == 0) {            //for cases involving an empty matrix
			return desiredResultMatrix.length == 0 && givenResultMatrix.length == 0;
		}
		for (int i = 0; i < givenResultMatrix.length; i++) {
			//for each desired row, see if we can match a given row
			if (matchOne(desiredResultMatrix[0], givenResultMatrix[i],
					new boolean[desiredResultMatrix[0].length], new boolean[givenResultMatrix[0].length],
					new HashMap<Integer, Integer>(), 0, 0, i)) {
				return true;
			}
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
	private boolean matchOne(String[] desiredRow, String[] givenRow, boolean[] desiredMarked, boolean[] givenMarked,
							  HashMap<Integer, Integer> columnMapping,
							  int numMatched, int desiredColumnIndex, int givenRowIndex) {

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
						if (matchRest(columnMapping, givenRowIndex)) return true;
					}
				}
				//otherwise if column mapping schema is not yet complete, continue building with a recursive call
				else if (matchOne(desiredRow, givenRow, desiredMarked, givenMarked, columnMapping,
						numMatched, desiredColumnIndex + 1, givenRowIndex)) {
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
	private boolean matchRest(HashMap<Integer, Integer> columnMapping, int givenRowIndex) {
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
		return true;    //Perfect match found, return true
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
		for (int j = 0; j < size; j++) {
			if (!(desiredRow[j].equals(givenRow[columnMappings.get(j)]) || areEquivalentDates(desiredRow[j], givenRow[columnMappings.get(j)]))) return false;
		}
		return true;
	}

	//Converts a resultSet into a matrix
	private String[][] resultSetToMatrix(ResultSet resultSet, int numCols, int numRows) throws SQLException {
		String[][] ret = null;
		try {
			ret = new String[numRows][numCols];
			int i = 0;
			while (resultSet.next()) {
				for (int j = 1; j <= numCols; j++) {
					String s = resultSet.getString(j);                                //grab string
					if (resultSet.wasNull()) s = "NULL";                    //take care of null values
					else{s = reformat(s);}									//reformat decimals and dates
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
	//			 or case, for example, where desird format is yy
	public static boolean areEquivalentDates(String s1, String s2){
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
	}


	public static int getScale(String s){
		int index = s.indexOf(".") + 1;		//index of first number after radix
		int scale = 2;						//2 = number of digits to show in decimal that aren't preceding 0s  //TODO: this number could be passed as parameter
		while(s.charAt(index) == '0'){																			//TODO: to allow grader to specify precision
			index++; scale++;
		}
		return scale;




	}
	public static String reformat(String s) {
		DateFormat dfStandardized = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		//Convert decimal values to standardized form
		if (s.matches("-?\\d*\\.\\d+(E(-?)\\d+)?")) {
			try {
				int scale = getScale(s);
				s = new BigDecimal(s).setScale(scale, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
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
			DateFormat df = new SimpleDateFormat("dd-MM-yy");
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
			DateFormat df = new SimpleDateFormat("yyyy-dd-MM");
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


	public static void main(String[] args) {

		String s1 = "01-02-1993 ";
		String s2 = "02-01-1993 ";
		System.out.println(areEquivalentDates(s1, s2));

		TestResultSetEqualContent test = new TestResultSetEqualContent();
		ArrayList<String> list = new ArrayList<String>();

		list.add("01-01-2019");
		list.add("01/01/2019");
		list.add("1\\1\\2019");
		list.add("01 - 01 - 19");
		list.add("1-JAN-2019");
		list.add("JAN-1-2019");
		list.add("January, 1, 2019");
		list.add("01/JANUARY/2019");
		list.add("January 1, 2019");
		list.add("JAN 1 2019");
		list.add("1   / JAN   / 2019");
		list.add("2019-01-01 00:00:00.0");
		list.add("01-JAN-19 00.00.00.0 AM");
		list.add("01:1\\2019");
		list.add("2019-JAN-1");
		list.add("2019-JANUARY,1");
		list.add("2019-1-JAN");
		list.add("2019/01/01");
		list.add("2019-01-01");
		list.add("2019, January 1");
		list.add("2019/JAN/01");
		list.add("5024.000000603");
		list.add("5024.000000600");
		list.add("3090.504");
		list.add("3090.5");

		for (String s : list) {
			System.out.println(reformat(s));
		}
		Date end = new Date();


	}



		/*IDAO dao = new OracleDataAccessObject("alfred.cs.uwec.edu", "csdev", "vaugharj", "UZ6TO9P");
		dao.connect();
		ResultSet rs = dao.executeSQLQuery("SELECT ts, to_char(ts) FROM test");
		try{
			rs.next();
			System.out.println(rs.getString(1) + ", " + rs.getString(2));
		}
		catch(Exception e){}
		dao.disconnect();*/



}	// end - class TestResultSetEqualContent
