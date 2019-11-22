/*
 * Utilities - class to hold utility methods
 * 
 * Created by Paul J. Wagner, 2-FEB-2019
 * Last Modified by PJW, 7-OCT-2019
 */
package edu.uwec.cs.wagnerpj.sqltest.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {
	// -- sortString - sort a string into ordered component parts
	public static String sortString(String origString) {
		String result = "";
		String[] words = origString.split("\\s+");
		//System.out.println("words before sort: " + words.toString());
		Arrays.sort(words);;
		//System.out.println("words after sort: " + words.toString());
		StringBuilder sb = new StringBuilder();
		for (String s: words) {
			sb.append(s);
			sb.append(" ");
		}	
		result = sb.toString().trim();

		return result;
	}	// end - method sortString
	
	// -- countMatches - count the matches for a substr in a longer string
	// TODO: need to check to only count subString if in body of query, not in table alias, column alias, or string literal
	public static int countMatches(String queryString, String subString) {
		int result = 0;
		
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
	// TODO: problem if nested subqueries, order is SELECT... SELECT..FROM  ...FROM
	public static String removeSelectFroms (String queryString) {
		String newString = null;
		
		queryString = queryString.toUpperCase().trim();
		int selectPos = queryString.indexOf("SELECT");
		//System.out.println("selectPos: " + selectPos);
		int fromPos   = queryString.indexOf("FROM");
		//System.out.println("fromPos : " + fromPos);
		int debugCount = 0;
		while (selectPos < fromPos && debugCount < 10 && selectPos != -1 && fromPos != -1) {
			queryString = queryString.substring(0, selectPos) + queryString.substring(fromPos + 4);
			//System.out.println("query string now is >" + queryString + "<");
			selectPos = queryString.indexOf("SELECT");
			//System.out.println("selectPos: " + selectPos);
			fromPos   = queryString.indexOf("FROM");
			//System.out.println("fromPos : " + fromPos);
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
				//System.out.println("skipInstructorComments();, skipping line, new line is: >" + localLine + "<");
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
		try {
			while (localLine != null && isUserComment(localLine)) {
				commWriter.println(submissionFileName + ": " + localLine);
				localLine = br.readLine();
				//System.out.println("processUserComments();, processing user comment line, new line is: >" + localLine + "<");
			}
		} 
		catch (IOException ioe) {
			System.err.println("processUserComments() - Either, cannot read from properties file, or write to comments file");
		}
		return localLine;
	}	// end = method processUserComments
	
	// -- isInstructorComment - check if line has three dashes at beginning, for header lines or question lines
	public static boolean isInstructorComment (String line) {
		boolean result = false;

		if (line != null && line.length() >= 3 && line.charAt(0) == '-' && line.charAt(1) == '-' && line.charAt(2) == '-') {
			result = true;
		}
		
		return result;
	}	// end - method isInstructorComment
	
	// -- isUserComment - check if line has two dashes at beginning, for any added user comment in submission
	public static boolean isUserComment (String line) {
		boolean result = false;

		if (line != null && line.length() >= 3 && line.charAt(0) == '-' && line.charAt(1) == '-' && line.charAt(2) != '-') {
			result = true;
		}
		
		return result;
	}	// end - method isUserComment

	
	// -- processSlashes - change Windows format backslashes to slashes, which can be processed in Java
	public static String processSlashes(String path) {
		String resultPath = "";
		
		// replace all backslashes with forward slashes
		resultPath = path.replace("\\", "/");
		//System.out.println("in processSlashes, path is now: >" + resultPath + "<");
		
		return resultPath;
	}	// end - method processSlashes
	
}	// end - class Utilities
