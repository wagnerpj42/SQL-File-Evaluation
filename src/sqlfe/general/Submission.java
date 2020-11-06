/*
 * Submission - class to hold a single submission for an assignment
 * 
 * Created - Paul J. Wagner, 12-Sep-2018
 */
package sqlfe.general;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

import sqlfe.sqltests.ISQLTest;
import sqlfe.util.Utilities;

public class Submission {
	// data
	static final int MAX_LINE_ATTEMPTS = 15;		// max. times to try before generating parse exception
	private String submissionFileName;				// name of file submission came from
	private String submissionName;					// name of assignment as submitted - from template
	private String studentName;						// name of the student
	private String studentID;						// student id code

	private ArrayList <QuestionAnswer> answers;		// list of answers as submitted
	private ArrayList <QueryEvaluation> queryEvals;	// list of query evals for each answer
	private double totalPoints;						// total number of points for assignment
	
	// methods
	// constructors
	// all-arg constructor
	public Submission(String submissionFileName, String studentName, String studentID,
			String submissionName, ArrayList<QuestionAnswer> answers, 
			ArrayList<QueryEvaluation> queryEvals, double totalPoints) {
		super();
		this.submissionFileName = submissionFileName;
		this.studentName = studentName;
		this.studentID = studentID;
		this.submissionName = submissionName;
		this.answers = answers;
		this.queryEvals = queryEvals;
		this.totalPoints = totalPoints;
	}
	
	// default constructor
	public Submission() {
		this(null, null, null, null, null, null, 0.0);
	}

	// getters and setters
	public String getSubmissionFileName() {
		return submissionFileName;
	}

	public void setSubmissionFileName(String submissionFileName) {
		this.submissionFileName = submissionFileName;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	
	public String getStudentID() {
		return studentID;
	}

	public void setStudentID(String studentID) {
		this.studentID = studentID;
	}
	
	public String getSubmissionName() {
		return submissionName;
	}

	public void setSubmissionName(String submissionName) {
		this.submissionName = submissionName;
	}

	public ArrayList<QuestionAnswer> getAnswers() {
		return answers;
	}

	public void setAnswers(ArrayList<QuestionAnswer> answers) {
		this.answers = answers;
	}
	
	public ArrayList<QueryEvaluation> getQueryEvals() {
		return queryEvals;
	}

	public void setQueryEvals(ArrayList<QueryEvaluation> queryEvals) {
		this.queryEvals = queryEvals;
	}
	
	public double getTotalPoints() {
		return totalPoints;
	}

	public void setTotalPoints(double totalPoints) {
		this.totalPoints = totalPoints;
	}

	// hashCode
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((answers == null) ? 0 : answers.hashCode());
		result = prime * result + ((queryEvals == null) ? 0 : queryEvals.hashCode());
		result = prime * result + ((studentID == null) ? 0 : studentID.hashCode());
		result = prime * result + ((studentName == null) ? 0 : studentName.hashCode());
		result = prime * result + ((submissionFileName == null) ? 0 : submissionFileName.hashCode());
		result = prime * result + ((submissionName == null) ? 0 : submissionName.hashCode());
		result = prime * result + (int)totalPoints;
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
		Submission other = (Submission) obj;
		if (answers == null) {
			if (other.answers != null)
				return false;
		} else if (!answers.equals(other.answers))
			return false;
		if (queryEvals == null) {
			if (other.queryEvals != null)
				return false;
		} else if (!queryEvals.equals(other.queryEvals))
			return false;
		if (studentID == null) {
			if (other.studentID != null)
				return false;
		} else if (!studentID.equals(other.studentID))
			return false;
		if (studentName == null) {
			if (other.studentName != null)
				return false;
		} else if (!studentName.equals(other.studentName))
			return false;
		if (submissionFileName == null) {
			if (other.submissionFileName != null)
				return false;
		} else if (!submissionFileName.equals(other.submissionFileName))
			return false;
		if (submissionName == null) {
			if (other.submissionName != null)
				return false;
		} else if (!submissionName.equals(other.submissionName))
			return false;
		if (totalPoints != other.totalPoints)
			return false;
		return true;
	}	// end - method equals

	// toString
	@Override
	public String toString() {
		return "Submission [submissionFileName=" + submissionFileName + ", studentName=" + studentName + ", studentID="
				+ studentID + ", submissionName=" + submissionName + ", answers=" + answers + ", queryEvals="
				+ queryEvals + ", totalPoints=" + totalPoints + "]";
	}
	
	// Skip comments or blank spaces before reaching a new question or an instructor comment
	private String parseComments(String line, BufferedReader br , PrintWriter commWriter ){
		while (line != null && Utilities.isInstructorComment(line)) {			
		// skip remaining instructor comment lines with question text
			line = Utilities.skipInstructorComments(br, line);
			//System.out.println("line after skipping instructor comments with question text is: >" + line + "<");
			
			// skip any blank lines after instructor comments
			line = Utilities.skipBlankLines(br, line);
			//System.out.println("line after skipping blanks after instructor comments is: >" + line + "<");
			
			// process any user comments above the answer
			line = Utilities.processUserComments(br, line, commWriter, submissionFileName);
			//System.out.println("line after skipping user comments above answer is: >" + line + "<");
			
			// skip any remaining blank lines before answer
			line = Utilities.skipBlankLines(br, line);
			//System.out.println("line after skipping any remaining blank lines before answer is: >" + line + "<");
		
			// check the new line to see if is new question instructor comment
			if (Utilities.isQuestionFound(line)) {				// start of new question
				//System.out.println("parsing instructor comments, but found new question");
				break;
	        } else {
	        	continue;
	        	//System.out.println("parsing instructor comments, found another instructor comment for same question");
	        }
		}	// end - while
		return line;
	}
	
	//Get the complete SQL query written by student and return the answer query string
	private String getAnswerQuery(String line, String answerQueryStr, BufferedReader br,PrintWriter commWriter)
	{
		//start of answer (possibly complete on one line) unless no answer present, then make answerQueryStr blank
		if (line != null && !Utilities.isInstructorComment(line)) {
			answerQueryStr = line;
		}else if (Utilities.isInstructorComment(line)) {	// if found next question - no answer submitted
			answerQueryStr = "";
			return answerQueryStr;
		}else {
			answerQueryStr = "";
		} 
		//System.out.println("start of answerQueryStr is: >" + answerQueryStr + "<");
		
		// process the remaining lines for that answer to get the complete query
		try {
			while((line = br.readLine())!= null) {
				
				// if not at end of file...
			    if (Utilities.isQuestionFound(line)) {	// if find start of next question
			    	//System.out.println("found start of next question");
			    	br.reset();
			    	break;
			    }
			    else if (line.indexOf(';') == -1) {	// look for terminating semicolon,						
			    	//System.out.println("found additional answer line");
			    	answerQueryStr += ("\n" + line); //  if not found, still part of answer
			    	br.mark(0);
			    }
			    else if (line.indexOf(';') != -1) { // found semicolon, is end of answer 
			    	//System.out.println("found last question line, with semicolon");
			    	answerQueryStr += ("\n" + line);
			    	break;
			    }
			    else if (Utilities.isUserCommentSingleLine(line) ||
			    		 Utilities.isUserCommentMultiLineStart(line)) { 		// found user comment embedded in answer
			    	line = Utilities.processUserComments(br, line, commWriter, submissionFileName);
			    }
			    else {
			    	System.err.println("unexpected answer line condition");
			    }
			    //System.out.println("next line is: >" + line + "<");
			}// end - while more lines for answer
		} catch (FileNotFoundException e) {
			System.err.println("Cannot find file " + submissionFileName);
		} catch (IOException ioe) {
			System.err.println("Cannot read from file " + submissionFileName);
		}
		//System.out.println("final answer before blank/comment check for Question is " + answerQueryStr.trim() + "<\n");
		return answerQueryStr;
	}
	
	// readSubmission - read one submission from a file
	public void readSubmission(String submissionFileName, PrintWriter commWriter, PrintWriter parseWriter) {
		FileReader fr = null;						// file stream for reading SQL submission file
		BufferedReader br = null;					// buffered reader for that stream
		String answerQueryStr = "";					// each answer string given in assignment
		String qNumStr = "";						// question number as a string; e.g. 1.c)
		
		try {
			fr = new FileReader(submissionFileName);
			br = new BufferedReader(fr);
			String line;
			this.submissionFileName = submissionFileName;
			///System.out.println("\nReading in file " + submissionFileName);
			
			// initialize answers and total points
			if (answers == null) {					// initialize questions list
				answers = new ArrayList<QuestionAnswer>();
			}
			totalPoints = 0;
			
			// Parse and store the first three lines of file which include fields class name, 
			// student name and a blank space for maintaining separation from the next section
			line = getFileMetadata(br);
			
			// Parse through any additional lines(empty or non-empty) before reaching the first question
			line = reachFirstQuestion(br, line);

			// process student's answers
			int loopCount = 0;						// for debugging
			//line = br.readLine();               	// get first answer line - assume at least one question number on template
			final int MAX_TIMES_TO_TRY = 25;		// maximum number of times to try processing line before saying stuck and move on
			System.out.print("   Parsing: ");
			while (line != null && loopCount < MAX_TIMES_TO_TRY) {					// more answers to process  
				loopCount++;
				// skip white lines before/between/after questions
				// TODO - need better new question check than period at less than hard-coded position
				// TODO: need general way of detecting non-query text; e.g. comments, garbage
				//System.out.println("line before skipping any blanks is: >" + line + "<");
				line = Utilities.skipBlankLines(br, line);
				
				// remove 0, 1 or more user comment sections and blank sections
				while (!Utilities.isQuestionFound(line)) {
					line = Utilities.processUserComments(br, line, commWriter, submissionFileName);
					line = Utilities.skipBlankLines(br, line);
				}
				//System.out.println("next line to analyze is: >" + line + "<");				
				
				if (Utilities.isQuestionFound(line)) {				// start of new question
		        	//System.out.println("found new question...");
					// process the first line to get question number and desired query
		        	// TODO: need to generalize to support . or ) as in pattern
					
		        	// get question number as string from the line with a '.' or ')'
					qNumStr = Utilities.getQuestionNumber(line);	// skip past -- -- and space
					System.out.print("Q" + qNumStr + ".");

					//Scan any comments, blank lines before reaching the line containing answerQuery
					line = parseComments(line, br , commWriter);
					
					//Parse the answer query as a string and store it in answerQuerySt
					answerQueryStr = getAnswerQuery(line,answerQueryStr,br,commWriter);

					//Write the Question number and answer pair in the QuestionAnswer list
					writeToQuestionAnswerList(qNumStr, answerQueryStr);
					
					line = br.readLine();										// go to next line and check that line

					// process any remaining lines, looking for user comments, possibly surrounded by blank lines
					line = Utilities.skipBlankLines(br, line);
					line = Utilities.processUserComments(br, line, commWriter, submissionFileName);
					line = Utilities.skipBlankLines(br, line);
					
					// skip any lines that contain anything other than user comments until reaching the next question  
					while (line != null && !Utilities.isQuestionFound(line)) {
			        	line = br.readLine();										// go to next line and check that line
					}	// end - while
					
				}	// end - if matcher found the start of a question
			}	// end - while more answers to process
			System.out.println(); 									// end parsing output to console
		 
		} catch (FileNotFoundException e) {
			System.err.println("Cannot find file " + submissionFileName);
		} catch (IOException ioe) {
			System.err.println("Cannot read from file " + submissionFileName);
		} catch (SQLFEParseException sqlfepe) {
			System.err.println(sqlfepe.getMessage());
			parseWriter.println(sqlfepe.getMessage());
		}

	}	// end - method readSubmission
	
	// Get the details of the file and store them in submissionName and studentName
	private String getFileMetadata(BufferedReader br) {
		// TODO - remove -- -- from these three lines before writing out
		// TODO - make how many lines are in an assignment customizable? e.g. add student id
		String line = null;
		try {
			line = br.readLine(); 											// get first line
			line = Utilities.skipBlankLines(br, line);						// skip blanks if any
			final int BASE_PROMPT_LENGTH = 6;								// length of instructor comment marker >-- -- <
			submissionName = line.substring(BASE_PROMPT_LENGTH);			// first line = assignment name, strip off leading >-- -- <
			//System.out.println("submission name: " + submissionName);
			line = br.readLine();											// second line = (student) name
			final int NAME_PROMPT_LENGTH = 11;								// length of >-- -- Name:<
			if (line.length() > NAME_PROMPT_LENGTH) {
				studentName = line.substring(NAME_PROMPT_LENGTH).trim();	// name is whatever is after prompt
			} else {
				studentName = "missing";
			}
			//System.out.println("name: " + studentName);
			line = br.readLine(); 											// read third line
		} catch (FileNotFoundException e) {
			System.err.println("Cannot find file " + submissionFileName);
		} catch (IOException ioe) {
			System.err.println("Cannot read from file " + submissionFileName);
		}
		return line;
	}

	// skip any blank lines and additional instructions for assignment (before first question instructor comments)
	private String reachFirstQuestion(BufferedReader br, String line) throws SQLFEParseException {
		line = Utilities.skipBlankLines(br, line);
		//System.out.println("after any blanks, next line is: >" + line + "<");
		
		// if not already at the first question, look for any other instructor comments and trailing blanks and skip them
		int attemptCount = 0;							// number of line attempts so far
														// stop if user comments or start of new question
		while (!Utilities.isUserCommentSingleLine(line) && !Utilities.isUserCommentMultiLineStart(line) && !Utilities.isQuestionFound(line) && 
        		attemptCount <= MAX_LINE_ATTEMPTS) {						
			line = Utilities.skipInstructorComments(br, line);
        	//System.out.println("after instructor comments, next line is: >" + line + "<");
			line = Utilities.skipBlankLines(br, line);			
        	//System.out.println("after next set of blanks, next line is: >" + line + "<");
			line = Utilities.skipExtraQueries(br, line);
        	//System.out.println("after next set of drop queries, next line is: >" + line + "<");
        	attemptCount++;
        	if (attemptCount > MAX_LINE_ATTEMPTS) {
        		throw new SQLFEParseException("\nParse Exception in file: " + submissionFileName + ", approx. line: >" + line + "<");
        	}
        }
		return line;
	}

	// Write the Questions and the Answers to the QuestionAnswer List
	private void writeToQuestionAnswerList(String qNumStr, String answerQueryStr) {
		
		// remove any trailing semicolon from the answer
		int semiPos = answerQueryStr.indexOf(';');
		if (semiPos != -1) {
			answerQueryStr = answerQueryStr.substring(0, semiPos);
		}
		
		// build the entire question answer
		answerQueryStr = answerQueryStr.trim();
		//System.out.println("\nfinal answer for " + qNumStr + " is: >" + answerQueryStr + "<");
		QuestionAnswer answer = new QuestionAnswer(qNumStr, new Query(answerQueryStr), 0.0);
		answers.add(answer);
	}

	// writeSubmission - write a submission out to file
	public void writeSubmission(String evaluationFolderPath) {
		PrintWriter outWriter = null;						// output file writer
															// output file name, including path
		String outFileName = evaluationFolderPath + submissionFileName + ".out";
		DecimalFormat df = new DecimalFormat();				// decimal format for number display
		df.setMaximumFractionDigits(2);
		
		try {
			outWriter = new PrintWriter(outFileName, "UTF-8");

			// output general information
			outWriter.println("Assignment  : " + submissionName);
			outWriter.println("Student Name: " + studentName);
			//outWriter.println("Student ID  : " + studentID);
			outWriter.println("Answer File : " + submissionFileName);
			outWriter.println("Total Points: " + df.format(totalPoints));
			outWriter.println();
			outWriter.println("Your answers, evaluation and points follow.");
			outWriter.println();
			
			// output answer information for each question answered
			for (int aIndex = 0; aIndex < answers.size(); aIndex++) {
				// output submitted answer information
				QuestionAnswer a = answers.get(aIndex);
				outWriter.println(a.getQNumStr() + ": " + a.getActualQuery());
				outWriter.println();
				
				// output testing information for that answer
				QueryEvaluation qe = queryEvals.get(aIndex);
				outWriter.print("Points given: " + df.format(qe.getQueryScore()) );
				double maxQuestionPoints = qe.getMaxPoints();
				outWriter.println(" of maximum " + df.format(maxQuestionPoints));
				outWriter.println();
				ArrayList<ISQLTest> tests = qe.getAllTests();
				ArrayList<Integer>  testPoints = qe.getAllTestsResults();
				ArrayList<Integer>  testPcts = qe.getAllTestsPercents();
				for (int testIndex = 0; testIndex < tests.size(); testIndex++) {
					ISQLTest test = tests.get(testIndex);
					int points = testPoints.get(testIndex);
					int pct = testPcts.get(testIndex);
					// TODO: mark conversion to utility method
					outWriter.println(test.getDesc() + ": " + df.format ( (points / 100.0) * (qe.getMaxPoints() / 10.0) )
							+ " / " + df.format( (pct / 100.0) * qe.getMaxPoints() ) );
				}
				outWriter.println();
			}

		}
		catch (IOException ioe) {
			System.out.println("IOException in writing to file " + outFileName);
		}
		finally {
			outWriter.close();
		}
	}	// end - method writeSubmission

}	// end - class Submission
