/*
 * Assignment - class to hold the information for an entire assignment
 * 
 * Created - Paul J. Wagner, 11-Sep-2018
 */
package sqlfe.general;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import sqlfe.util.Utilities;

import java.util.regex.Matcher;

public class Assignment { 
	// data
	private String	assignmentName;					// name of assignment
	private ArrayList<Question> questions;			// list of questions in assignment

	// methods
	// constructors
	// all-arg constructor
	public Assignment(String assignmentName, ArrayList<Question> questions) {
		super();
		this.assignmentName = assignmentName;
		this.questions = questions;
	}
	
	// default constructor
	public Assignment() {
		this(null,  null);
	}

	// getters and setters
	public String getAssignmentName() {
		return assignmentName;
	}

	public void setAssignmentName(String assignmentName) {
		this.assignmentName = assignmentName;
	}

	public ArrayList<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(ArrayList<Question> questions) {
		this.questions = questions;
	}

	// other methods
	// hashCode
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assignmentName == null) ? 0 : assignmentName.hashCode());
		result = prime * result + ((questions == null) ? 0 : questions.hashCode());
		return result;
	}

	// equals
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Assignment other = (Assignment) obj;
		if (assignmentName == null) {
			if (other.assignmentName != null)
				return false;
		} else if (!assignmentName.equals(other.assignmentName))
			return false;
		if (questions == null) {
			if (other.questions != null)
				return false;
		} else if (!questions.equals(other.questions))
			return false;
		return true;
	}	// end - method equals

	// toString
	@Override
	public String toString() {
		return "Assignment[assignmentName=" + assignmentName + ", questions=" + questions + "]";
	}
	
	// readProperties - read in the evaluation properties from a file
	void readProperties(String propertiesFilename) {
		FileReader fr = null;				// file reader for properties file
		BufferedReader br = null;			// buffered reader for properties file
		String regexp = "\\d+[a-z]*(-\\d)?[.]";	// regular expression for question, e.g. >1a.< or >1a-1. if multiple
		Pattern pattern = Pattern.compile(regexp);	// pattern for regexp pattern matching
		Matcher matcher = null;				// matcher for regexp pattern matching		

		try {
			fr = new FileReader(propertiesFilename);		// reader classes
			br = new BufferedReader(fr);					// "      "
			String line = null;								// each line from assignment properties file

			// read and process assignment name
			line = br.readLine();			
			assignmentName = line;							// first line = assignment name
			Utilities.threadSafeOutput("Assignment: " + assignmentName + "\n\n");
			line = br.readLine();							// get next line after assignment name
			
			if (questions == null) {						// initialize questions list
				questions = new ArrayList<Question>();
			}
			
			line = Utilities.skipBlankLines(br, line);		// skip blank lines before any answers		
			
			// read in questions (if exist)
			while (line != null && !line.equals("")) {   	// more questions to process

				// process question line, making sure has question number format
		        matcher = pattern.matcher(line);
		        if (matcher.find()) {						// first line of answer
					// process the first line to get question number, number of points and desired query
		        	// - get the question number, required to end in period (.)
					int periodPos = line.indexOf('.');
					String qNumStr = line.substring(0, periodPos);
				
					// - get number of points
					int marksPos = line.indexOf("points");
					String numMarksStr = "";
					numMarksStr = line.substring(periodPos + 3, marksPos - 1);
					int numMarks = Integer.parseInt(numMarksStr);

					// - build answer query, starting with rest of line
					String desiredQueryStr = line.substring(marksPos + 7);

					// -- process the remaining lines for that query, if exist
					boolean moreLinesForQuery = (line.indexOf(';') == -1);
					while (moreLinesForQuery) {
						line = br.readLine();					// get next line

				        if (line.indexOf(';') == -1) {			// look for terminating semicolon, if not found...						
				        	desiredQueryStr += (" " + line);	// add line w/ space to make sure syntactically correct when combining lines
				        }
				        else {									// found next question, end query
				        	moreLinesForQuery = false;
				        	desiredQueryStr += (" " + line);
				        	line = br.readLine();
				        }    
					}	// end - while
				
					// -- remove semicolon at end
					int semiPos = desiredQueryStr.indexOf(';');
					if (semiPos != -1) {
						desiredQueryStr = desiredQueryStr.substring(0, semiPos);
					}

					Query desiredQuery = new Query(desiredQueryStr);
				
					// skip over blank line(s) if exists before test for this query
					line = Utilities.skipBlankLines(br, line);
								
					// process the remaining lines for that question to get the conditions and tests
					boolean moreTestsForQuestion = true;
					ArrayList<EvalComponentInQuestion> tests = new ArrayList<EvalComponentInQuestion>();
					while (moreTestsForQuestion) {
						if (line != null && !line.equals("") && 
								(line.substring(0, 4).equals("Test") || 
								(line.substring(0, 4).equals("Cond") )) ) {
							// process as a test/condition
							// get test name - from beginning of line to first space
							int firstSpacePos = line.indexOf(' ');
							String testNameString = line.substring(0, firstSpacePos);
							
							// get test percent
							// look for second space after percent
							int secondSpacePos = line.indexOf(' ', firstSpacePos + 1);
							
							// if no second space, percent is first space to end
							String percentString = null;
							if (secondSpacePos == -1) {
								percentString = line.substring(firstSpacePos + 1);
							} else {
								percentString = line.substring(firstSpacePos + 1, secondSpacePos);
							}
							int percent = Integer.parseInt(percentString);
							
							// if condition, get condition string
							String condition = null;
							if (secondSpacePos != -1) {
								condition = line.substring(secondSpacePos + 1);
								// strip out double quotes on edges
								condition = condition.replace("\"", "");
							}
							
							// build evaluation component according to type and add to test list
							EvalComponentInQuestion test = 
									new EvalComponentInQuestion(testNameString, percent, condition);
							tests.add(test);
							
							line = br.readLine();						// get next test, skip any blanks at end
							while (line != null && (line.equals(""))) {
								line = br.readLine();
							}
						}
						else {
							moreTestsForQuestion = false;
						}	// end - if/else
					}	// end - while

					// build the entire question
					Question question = new Question(qNumStr, numMarks, desiredQuery, tests);
					questions.add(question);
		        }	// end - if processing a question
			}	// end - while more questions
		} catch (FileNotFoundException e) {
			System.err.println("Cannot find file " + propertiesFilename);
		} catch (IOException ioe) {
			System.err.println("Cannot read from file " + propertiesFilename);
		}
	}	// end - method readProperties
	

}	// end - class Assignment
