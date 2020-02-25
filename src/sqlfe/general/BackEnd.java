/*
 * Backend - backend SQL file evaluation system class
 * 
 * Created - Paul J. Wagner, 18-Oct-2017
 * Last updated - PJW, 7-Oct-2019
 */
package sqlfe.general;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

import sqlfe.sqltests.ISQLTest;
import sqlfe.util.Utilities;

public class BackEnd {
	// -- data
	String resultString = "default result";			// overall back end result string (TODO: needed?)

	String dbmsName = null;							// name of DBMS to use (e.g. Oracle, MySQL)
	String hostName = null;							// name or IP address of DBMS host (e.g. localhost, abc.univ.edu, 162.03.24.119)
	String idName = null;							// system/schema id on DBMS host
	String username = null;							// DBMS username
	String password = null;							// DBMS password
	
	String mainFolderPath = null;					// main folder for other folders (submissions, evaluations) and assignment prop.			
	String submissionFolderPath = null;			 	// location of submission files relative to workspace folder
	String evaluationFolderPath = null; 			// location of evaluation output files relative to workspace folder
	String assignmentPropertiesFileName = null;		// name of assignment properties file relative to workspace folder
	String gradesFileName = null;					// name of grades summary file in evaluation folder
	
	IDAO dao = null;								// data access object, created based on information from front end
	
	
	// -- process - general method to call other methods for processing
	public String process(FrontEnd aFrontEnd) {			
		// move data in
		transferData(aFrontEnd);
		
		// set up and run the evaluation system
		resultString = evaluate();
		
		return resultString;
	}	// end - method process

	
	// -- transferData() - move data from front end to back end, also set data based on these
	public void transferData(FrontEnd aFrontEnd) {
		dbmsName = aFrontEnd.getDbmsChoice();							// get DBMS choice directly from front end
		hostName = aFrontEnd.getDbmsHost();								// get DBMS host directly "
		idName = aFrontEnd.getDbmsSystemID();							// get DBMS system ID directly "
		username = aFrontEnd.getDbmsUsername();							// get DBMS username directly "
		password = aFrontEnd.getDbmsPassword();							// get DBMS password directly "
																		// get main folder path directly "
		mainFolderPath = Utilities.processSlashes(aFrontEnd.getEvaluationFolder());				
		submissionFolderPath = mainFolderPath + "/files/"; 				// set location of submission files relative to workspace folder
		evaluationFolderPath = mainFolderPath + "/evaluations/"; 		// set location of evaluation output files relative to workspace folder
		assignmentPropertiesFileName = mainFolderPath + "/" + aFrontEnd.getAssignPropFile();	// get assignment properties file name directly "

		gradesFileName = evaluationFolderPath + "AAA_grade_summary.out"; // set grades summary file name in evaluations folder
		
		switch (dbmsName) {
		case "Oracle":
			dao = new OracleDataAccessObject(hostName, idName, username, password);
			break;
		case "MySQL":
			dao = new MySQLDataAccessObject(hostName, idName, username, password);
			break;
		default:
			// TODO: what to do if bad DAO specification?
		}
		
		System.out.println("main folder path is:        " + mainFolderPath);
		System.out.println("submissions folder path is: " + submissionFolderPath);
		System.out.println("evaluations folder path is: " + evaluationFolderPath);
	}	// end - method transferData
	
	
	// -- evaluate() - do the main evaluation work
	// TODO: do we need to return anything here?
	public String evaluate () {
		//Question currQuestion = null;					// current question for a submitted answer
		ArrayList<Question> currQuestions = null;		// current question(s) for a submitted answer
		
		// read in the assignment properties
		Assignment a = new Assignment();
		a.readProperties(assignmentPropertiesFileName);
		System.out.println(a.toString());
		ArrayList<Question> questions = a.getQuestions();

		// set up grade summary file
		DecimalFormat df = new DecimalFormat();					// decimal format for number display
		df.setMaximumFractionDigits(2);
		PrintWriter gradesWriter = null;						// grade summary file writer

		try {
			gradesWriter = new PrintWriter(gradesFileName, "UTF-8");
			// output general information
			gradesWriter.println("Assignment  : " + a.getAssignmentName());
			gradesWriter.println("");
		}
		catch (IOException ioe) {
			System.err.println("IOException in writing to file " + gradesFileName);
		}
		
		// read in all submission files
		SubmissionCollection sc = new SubmissionCollection();
		sc.getAllFiles(submissionFolderPath, evaluationFolderPath, a.getAssignmentName());
		
		// process each submission in the collection
		ArrayList<Submission> sa = sc.getSubmissions();
		
		for (int sIndex = 0; sIndex < sc.getTotalSubmissions(); sIndex++) {
			Submission s = sa.get(sIndex);
			//System.out.println(s.toString());
			System.out.println("\nProcessing file " + s.getSubmissionFileName());
			double submissionPoints = 0;
			ArrayList<QueryEvaluation> queryEvals = new ArrayList<QueryEvaluation>();

			// initialize output point string for grade summary file
			String outputPointString = ": ";
			
			// process each question answer in the submission
			ArrayList<QuestionAnswer> qas = s.getAnswers();
			for (int qaIndex = 0; qaIndex < qas.size(); qaIndex++) {				
				// get the next answer for this submission
				QuestionAnswer qa = qas.get(qaIndex);
				System.out.println("processing question " + qa.qNumStr
						+ " for submission " + s.getSubmissionFileName());
				
				Query actualQuery = qa.getActualQuery();
				//System.out.println("Main-actualQuery: >" + actualQuery.toString() + "<");
				
				// find the matching question(s) for the answer
				currQuestions = new ArrayList<Question>();
				boolean foundOne = false;
				boolean foundAll = false;
				int qIndex = 0;
				while (qIndex < questions.size() && !foundAll) {
					//System.out.println("student qa is : >" + qa.getQNumStr() + "<");
					//System.out.println("instr. ques is: >" + questions.get(qIndex).getQNumStr());
					// first match
					if (!foundOne && questions.get(qIndex).getQNumStr().indexOf(qa.getQNumStr()) == 0) {
						foundOne = true;
						currQuestions.add(questions.get(qIndex));		// use this question
						qIndex++;
					}
					// subsequent match
					else if (foundOne && questions.get(qIndex).getQNumStr().indexOf(qa.getQNumStr()) == 0) {
						currQuestions.add(questions.get(qIndex));		// add this question too
						qIndex++;
					}
					// first non-match after subsequent match
					else if (foundOne && questions.get(qIndex).getQNumStr().indexOf(qa.getQNumStr()) != 0) {
						foundAll = true;
						qIndex++;
					}
					// not a match
					else {
						qIndex++;
					}
				}	// end - while looking for question(s) to match student answer
				
				// TODO: how to do error handling for finding question to match submission answer number?
				//       may need try/catch block to avoid later work
				if (!foundOne) {
					System.out.println("cannot find question");
				}
				
				// loop through all possible questions, evaluate, choose max
				double highestPoints = -1.0;			// set below zero so any evaluation is better
				double qPoints = 0.0;
				QueryEvaluation qe = null;
				QueryEvaluation maxQE = null;
				
				for (int qcIndex = 0; qcIndex < currQuestions.size(); qcIndex++) {
					// get the desired query for this question
					Query desiredQuery = currQuestions.get(qcIndex).getDesiredQuery(); 
					//System.out.println("evaluating answer: " + currQuestions.get(qcIndex).getQNumStr());
					//System.out.println("Main-desiredQuery: >" + desiredQuery.toString() + "<");
					
					// get the evaluation components for this question
					ArrayList<EvalComponentInQuestion> questionEvalComps = currQuestions.get(qcIndex).getTests(); 
					int maxPoints = currQuestions.get(qcIndex).getQuestionPoints();
					
					ArrayList<ISQLTest> questionTests = new ArrayList<ISQLTest>();
					ArrayList<Integer> questionPcts  = new ArrayList<Integer>();
					ArrayList<String> questionConditions = new ArrayList<String>();
					
					// evaluate all tests for this question
					for (int tiqIndex = 0; tiqIndex < questionEvalComps.size(); tiqIndex++) {
						// get test names
						String currTestName = questionEvalComps.get(tiqIndex).getEvalComponentName();
						currTestName = "sqlfe.sqltests." + currTestName;
						//System.out.println("test name is: >" + currTestName + "<");
	
						// make test object out of test name
						try {
							Class<?> aClass = Class.forName(currTestName);
							Object oTest = aClass.newInstance();
							ISQLTest test = (ISQLTest)oTest;
							questionTests.add(test);
						}
						catch (Exception e) {
							System.out.println("exception in generating class object from name");
						}

						// get percents
						int currTestPct = questionEvalComps.get(tiqIndex).getPercent();
						questionPcts.add(currTestPct);
						
						// get condition
						String currTestCondition = questionEvalComps.get(tiqIndex).getCondition();
						questionConditions.add(currTestCondition);
					}	// end - for each test in question

					// build a query evaluation, evaluate and add this qe to the current submission
					qe = new QueryEvaluation(actualQuery, desiredQuery, dao, maxPoints, 
												questionTests, questionPcts, questionConditions, null, 0.0);
					qPoints = qe.evaluate();
					//System.out.println("weighted points from evaluation: " + qPoints);
					//System.out.println();
					if (qPoints > highestPoints) {
						highestPoints = qPoints;
						maxQE = qe;
					}
				}		// end - for each question
				queryEvals.add(maxQE);					// add best qe for this answer to the list
					
				submissionPoints += highestPoints;		// add the highest question score to the submission total
				
				outputPointString += (df.format(highestPoints) + ", ");	// add highest points to string for grade summary output
			}	// end - for each question answer
			//System.out.println("Total points for this submission: " + submissionPoints);
			//System.out.println();
			s.setTotalPoints(submissionPoints);				// add the total points to the submission
			s.setQueryEvals(queryEvals);					// add the query evaluations to the submission
			
			//System.out.println("finished processing submission " + s.getSubmissionFileName() + ", now writing it out");
			s.writeSubmission(evaluationFolderPath);		// write out each submission's output file

			// write each total grade to grades file
			try {
				gradesWriter.println(s.getStudentName() + ": " + df.format(s.getTotalPoints()) + outputPointString);
			}
			catch (Exception e) {
				System.err.println("Error in writing to grades file " + gradesFileName);
			}
		}	// end - for each submission

		// close evaluation folder / grades file
		try {
			gradesWriter.close();
		}
		catch (Exception e) {
			System.err.println("Error in closing grades file: " + gradesFileName);
		}
		finally {
			gradesWriter.close();
		}
		
		return resultString;		
	}	// end - method evaluate

}	// end - class BackEnd
