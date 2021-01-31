/*
 * Utilities - class to hold utility methods
 * 
 * Created by Paul J. Wagner, 2-FEB-2019
 * Last Modified by PJW, 7-OCT-2019
 */
package sqlfe.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;

public class Utilities { 	
	// methods
	// -- sortString - sort a string into ordered component parts
	public static String sortString(String origString) {
		String result = "";
		String[] words = origString.split("\\s+");
		Arrays.sort(words);;
		StringBuilder sb = new StringBuilder();
		for (String s: words) {
			sb.append(s);
			sb.append(" ");
		}	
		result = sb.toString().trim();

		return result;
	}	// end - method sortString
	
	// -- countMatches - count the matches for a substr in a longer query string
	public static int countMatches(String queryString, String subString) {
		int result = 0;
		
		// make sure query string wasn't empty to start with
		if (queryString == null) {
			return result;
		}
		
		// otherwise, go through entire string looking for substring
		//System.out.println("subString being searched for is: >" + subString + "<");
		queryString = queryString.toUpperCase();
		int subIndex = queryString.indexOf(subString);
		while (subIndex >= 0) {
			result++;
			queryString = queryString.substring(subIndex + 1);
			//System.out.println("countMatches();, rest of queryString is: >" + queryString + "<");
			subIndex = queryString.indexOf(subString);
		}
		
		return result;
	}	// end - method countMatches
	
	// -- removeSelectFroms - remove SELECT...FROM parts of query so that comma counting
	//                          only counts join commas
	// TODO: what about commas in GROUP BY or ORDER BY clauses?
	public static String removeSelectFroms (String queryString) {
		String newString = null;
		
		queryString = queryString.toUpperCase().trim();
		int selectPos = queryString.indexOf("SELECT");
		int fromPos   = queryString.indexOf("FROM");
		int debugCount = 0;
		while (selectPos < fromPos && debugCount < 10 && selectPos != -1 && fromPos != -1) {
			queryString = queryString.substring(0, selectPos) + queryString.substring(fromPos + 4);
			selectPos = queryString.indexOf("SELECT");
			fromPos   = queryString.indexOf("FROM");
			debugCount++;
		}
		newString = queryString;
		return newString;
	}
	
	// -- skipBlankLines = skip blank lines in buffered reader
	public static String skipBlankLines (BufferedReader br, String line) {
		
		String localLine = null;
		if (line != null) {
			localLine = line.trim();
		}
		
        try {
        	while (localLine != null && (localLine.equals("") ) ) {
				localLine = br.readLine();
				if (localLine != null) {
					localLine = localLine.trim();
				}
			}
		} 
		catch (IOException ioe) {
			System.err.println("skipBlankLines() - Cannot read from properties file");
		}
		return localLine;
	}	// end = method skipBlankLines

	// -- skipInstructorComments = skip instructor comment lines in buffered reader
	public static String skipInstructorComments (BufferedReader br, String line) {
		String localLine = line;
		try {
			while (localLine != null && isInstructorComment(localLine)) {
				localLine = br.readLine();
			}
		} 
		catch (IOException ioe) {
			System.err.println("skipInstructorComments() - Cannot read from properties file");
		}

		return localLine;
	}	// end = method skipInstructorComments

	// -- processUserComments = process any user comment lines in buffered reader
	public static String processUserComments (BufferedReader br, String line, 
											PrintWriter commWriter, String submissionFileName) {
		String localLine = line;
		boolean isSingleLineComment = isUserCommentSingleLine(line);
		boolean isMultiLineComment = isUserCommentMultiLineStart(line);
		if (isSingleLineComment) {							// is single-line comment, process until don't find any more
			try {
				while (localLine != null && isUserCommentSingleLine(localLine)) {
					commWriter.println(submissionFileName + ": " + localLine);
					localLine = br.readLine();
				}
			} 
			catch (IOException ioe) {
				System.err.println("processUserComments() - Either, cannot read from submission file, or write to comments file");
			}
		} else if (isMultiLineComment) {					// is multi-line comment, process to end of this comment
			try {
				while (localLine != null && !isUserCommentMultiLineEnd(localLine)) {
					commWriter.println(submissionFileName + ": " + localLine);
					localLine = br.readLine();
				}
				// process the multi-line comment end line
				commWriter.println(submissionFileName + ": " + localLine);
				localLine = br.readLine();
			} 
			catch (IOException ioe) {
				System.err.println("processUserComments() - Either, cannot read from submission file, or write to comments file");
			}	
		}
		return localLine;
	}	// end = method processUserComments
	
	// -- isInstructorComment - check if line has two dashes at beginning, then blank, then two more dashes
	//                          for header comment lines or question comment lines from instructor template
	public static boolean isInstructorComment (String line) {
		boolean result = false;

		if (line != null && line.length() >= 5 && line.charAt(0) == '-' && line.charAt(1) == '-' && line.charAt(2) == ' ' &&
												  line.charAt(3) == '-' && line.charAt(4) == '-') {
			result = true;
		}
		
		return result;
	}	// end - method isInstructorComment
	
	// -- isUserCommentSingleLine - check if line is user comment (not instructor comment), for any added user comment in submission
	// --        has two dashes at beginning (i.e. -- comment)
	public static boolean isUserCommentSingleLine (String line) {
		boolean result = false;
		if (line != null) {
			line = line.trim();
		}
		if (line != null && 
				(line.length() >= 3 && line.charAt(0) == '-' && line.charAt(1) == '-' && line.charAt(2) != '-') &&			// is -- comment
				!isInstructorComment(line)) {
			result = true;
		}
		return result;
	}	// end - method isUserCommentSingleLine

	// -- isUserCommentMultiLineStart - check if line is user comment (not instructor comment), for any added user comment in submission
	// --		  has /*, and will continue until */
	public static boolean isUserCommentMultiLineStart (String line) {
		boolean result = false;

		if (line != null) {
			line = line.trim();
		}
		if (line != null && 
				 (line.length() >= 2 && line.charAt(0) == '/' && line.charAt(1) == '*') &&								// is /* comment start
				!isInstructorComment(line)) {
			result = true;
		}
		return result;
	}	// end - method isUserCommentMultiLineStart

	// -- isUserCommentMultiLineEnd - check if line is multi-line user comment ending (that is, has */ at end of line)
	public static boolean isUserCommentMultiLineEnd (String line) {
		boolean result = false;

		if (line != null) {
			line = line.trim();
		}
		if (line != null && 
				 (line.length() >= 2 && line.charAt(line.length()-2) == '*' && line.charAt(line.length()-1) == '/') &&								// is /* comment start
				!isInstructorComment(line)) {
			result = true;
		}
		return result;
	}	// end - method isUserCommentMultiLineEnd
	
	// -- processSlashes - change Windows format backslashes to slashes, which can be processed in Java
	public static String processSlashes(String path) {
		String resultPath = "";
		
		// replace all backslashes with forward slashes
		resultPath = path.replace("\\", "/");
		
		return resultPath;
	}	// end - method processSlashes
	
	// -- isQuestionFound - If a non null line matches the pattern for start of a question which includes support for . and ), then return true 
	public static boolean isQuestionFound(String line){
		if (line != null) {
			String regexp = "^-- -- ([\\d+])[a-z]*([.]|[)])";		// regular expression for question, e.g. >-- --1a. or -- --23. or -- -- 1)<
			Pattern pattern = Pattern.compile(regexp);				// pattern for regexp pattern matching
			Matcher matcher = pattern.matcher(line);
			return matcher.find();
		}
		else {
			return false;
		}
    }	// end - method isQuestionFound

	// -- skipExtraQueries - Before reaching the first question, skip any lines that do not begin with instructor comments  
	public static String skipExtraQueries(BufferedReader br, String line) {
		if (line != null) {
			line = line.trim();
		}
		String regexp = "^(?!-- --).*$";
		Pattern pattern = Pattern.compile(regexp);
		Matcher matcher = pattern.matcher(line);
		String localLine = line;
		try {
			while (localLine != null && matcher.find()) {
				localLine = br.readLine();
			}
		} 
		catch (IOException ioe) {
			System.err.println("skipInstructorComments() - Cannot read from properties file");
		}
		return localLine;
	}	// end - method skipExtraQueries

	// -- getQuestionNumber - Get question number from question string with pattern support for . and )  
	public static String getQuestionNumber(String line) {
		String regexp = "^-- -- ([\\d+])[a-z]*([.]|[)])";		// regular expression for question, e.g. >-- -- 1a. or -- -- 23. --or -- -- 1)<
		Pattern pattern = Pattern.compile(regexp);				// pattern for regexp pattern matching
		Matcher matcher = pattern.matcher(line);
		if(matcher.find()){
			return matcher.group(1);							// The first captured group ([\\d+]) which is a number string is returned. 
		}
		else {
			return null;
		}
	}	// end - method getQuestionNumber
	
	// -- threadSafeOutput - process output to make sure is run later if not in FX thread
	public static void threadSafeOutput(String string) {
		if (Platform.isFxApplicationThread()) {
			// output as normal - OK to output within FX thread
			System.out.print(string);
		} else {
			// run later to sync with FX thread
			Platform.runLater(() -> System.out.print(string));
		}
	}	// end - method threadSafeOutput
	
}	// end - class Utilities
