/*
 * Backend - backend SQL file evaluation system class
 *
 * Created - Paul J. Wagner, 18-Oct-2017
 * Last updated - PJW, 7-Oct-2019
 */
package sqlfe.general;

import java.io.File;
import java.io.IOException;
//import java.io.PrintStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sqlfe.sqltests.ISQLTest;
import sqlfe.util.Utilities;

//public class BackEnd implements Runnable {
public class BackEnd {
	// -- data
	private String dbmsName = null;							// name of DBMS to use (e.g. Oracle, MySQL)
	private String hostName = null;							// name or IP address of DBMS host (e.g. localhost, abc.univ.edu, 162.03.24.119)
	private String portString = null;						// port used on host (e.g. 3306 for MySQL)
	private String idName = null;							// system/schema id on DBMS host
	private String username = null;							// DBMS username
	private String password = null;							// DBMS password
	//private PrintStream printStream = null;					// printStream for console output

	private String mainFolderPath = null;					// main folder for other folders (submissions, evaluations) and assignment prop.			
	private String submissionFolderPath = null;			 	// location of submission files relative to workspace folder
	private String evaluationFolderPath = null; 			// location of evaluation output files relative to workspace folder
	private String assignmentPropertiesFileName = null;		// name of assignment properties file relative to workspace folder
	private String gradesFileName = null;					// name of grades summary file in evaluation folder

	private IDAO dao = null;								// data access object, created based on information from front end

	FrontEnd aFrontEnd = null;						// front end holding information from GUI to use in backend processing

	// -- constructor, default
	public BackEnd() {
		// nothing right now
	}

	// -- constructor, one arg
	public BackEnd(FrontEnd aFrontEnd) {
		this.aFrontEnd = aFrontEnd;
	}

	public void setEvaluationFolderPath(String evaluationFolderPath) {
		this.evaluationFolderPath = evaluationFolderPath;
	}

	public void setSubmissionFolderPath(String submissionFolderPath) {
		this.submissionFolderPath = submissionFolderPath;
	}

	// -- process - general method to call other methods for processing
	public void process(FrontEnd aFrontEnd) {
		// move data in
		transferData(aFrontEnd);

		try {
			// create file
			String systemErrorFileName = evaluationFolderPath + "AAA_system_err.out";
			PrintStream printStream = new PrintStream(new File(systemErrorFileName));
			System.setErr(printStream);
		} catch (IOException e) {
			System.err.println("Failed to create system error logger");
		}

		// set up and run the evaluation system in a separate thread to avoid linear flow in regard to GUI
		Thread thread = new Thread(() -> {
			evaluate();
		});
		thread.start();

	}	// end - method process


	// -- transferData() - move data from front end to back end, also set data based on these
	public void transferData(FrontEnd aFrontEnd) {
		dbmsName = aFrontEnd.getDbmsChoice();							// get DBMS choice directly from front end
		hostName = aFrontEnd.getDbmsHost();								// get DBMS host directly "
		portString = aFrontEnd.getDbmsPort();							// get DBMS port directly "
		idName = aFrontEnd.getDbmsSystemID();							// get DBMS system ID directly "
		username = aFrontEnd.getDbmsUsername();							// get DBMS username directly "
		password = aFrontEnd.getDbmsPassword();							// get DBMS password directly "
		mainFolderPath = Utilities.processSlashes(aFrontEnd.getEvaluationFolder());		// get main folder path directly "

		// set folder paths under main folder
		submissionFolderPath = mainFolderPath + "/files/"; 				// set location of submission files relative to workspace folder
		evaluationFolderPath = mainFolderPath + "/evaluations/"; 		// set location of evaluation output files relative to workspace folder
		assignmentPropertiesFileName = mainFolderPath + "/" + aFrontEnd.getAssignPropFile();	// get assignment properties file name directly "

		// make sure evaluations folder exists, and create it if not
		File directory = new File(String.valueOf(evaluationFolderPath));
		if (!directory.exists()) {										// if evaluations folder doesn't exist, create it
			directory.mkdir();
		} else {														// else clear out existing files
			for (File f: directory.listFiles())
				f.delete();
		}

		// set grades summary file name in evaluations folder
		gradesFileName = evaluationFolderPath + "AAA_grade_summary.out";

		switch (dbmsName) {
			case "Oracle":
				dao = new OracleDataAccessObject(hostName, portString, idName, username, password, false);
				break;
			case "MySQL 5.x":
				dao = new MySQL5xDataAccessObject(hostName, portString, idName, username, password, false);
				break;
			case "MySQL 8.0":
				dao = new MySQL80DataAccessObject(hostName, portString, idName, username, password, false);
				break;
			case "Mock":
				dao = new MockDataAccessObject(hostName, portString, idName, username, password, false);
				break;
			default:
				System.err.println(this.getClass().getSimpleName() + " Incorrect DAO specification");
		}

	}	// end - method transferData

	// set dao for testing
	public void createTestObject(IDAO dao, String mainFolder) {
		this.dao = dao;
		mainFolderPath = mainFolder;		// get main folder path directly "

		// set folder paths under main folder
		submissionFolderPath = mainFolderPath + "/files/test/"; 				// set location of submission files relative to workspace folder
		evaluationFolderPath = mainFolderPath + "/evaluations/"; 		// set location of evaluation output files relative to workspace folder
		gradesFileName = evaluationFolderPath + "AAA_grade_summary.out";
		assignmentPropertiesFileName = mainFolderPath + "/assignmentProperties-MySQL";	// get assignment properties file name directly "
	}

	public Assignment createAssignment(String propertyFilePath){
		Assignment a = new Assignment();
		a.readProperties(propertyFilePath);
		return a;
	}

	// -- evaluate() - do the main evaluation work
	public void evaluate () {

		// read in the assignment properties, getting the questions
		Assignment a = createAssignment(assignmentPropertiesFileName);
		ArrayList<Question> questions = a.getQuestions();

		// set up grade summary file
		DecimalFormat df = new DecimalFormat();					// decimal format for number display
		df.setMaximumFractionDigits(2);
		PrintWriter gradesWriter = null;						// grade summary file writer

		// set up the assignment output file
		try {
			gradesWriter = new PrintWriter(gradesFileName, "UTF-8");
			// output general information
			gradesWriter.println("Assignment  : " + a.getAssignmentName());
			gradesWriter.println("");
		}
		catch (IOException ioe) {
			System.err.println(this.getClass().getSimpleName() + " IOException in writing to file " + gradesFileName);
		}

		// read in all submission files
		SubmissionCollection sc = new SubmissionCollection();
		try {
			sc.getAllFiles(submissionFolderPath, evaluationFolderPath, a.getAssignmentName());
		}
		catch (Exception e){
			System.err.println(this.getClass().getSimpleName() + " Error in reading submission collection");
			Utilities.threadSafeOutput("Error, please check the error file");
			return;
		}

		ArrayList<Submission> sa = sc.getSubmissions();

		if(dao.connect()==null){
			System.err.println(this.getClass().getSimpleName() + " Invalid database properties");
			Utilities.threadSafeOutput("Error, please check the error file");
			return;
		}

		goThroughAllSubmissions(sc, sa, questions, gradesWriter, df);

		// close evaluation folder / grades file
		try {
			dao.disconnect();
			gradesWriter.close();
		}
		catch (Exception e) {
			System.err.println(this.getClass().getSimpleName() + "Error in closing grades file: " + gradesFileName);
		}
		finally {
			dao.disconnect();
			gradesWriter.close();
		}

		// tell user that processing is done
		//System.out.println("\n\nProcessing of this submission set completed.");
		Utilities.threadSafeOutput("\n\nProcessing of this submission set completed.\n");
	}	// end - method evaluate

	/** Method responsible for going through the submissions
	 *
	 * @param sc A submission collection object that holds the submissions of all the student.s
	 * @param questions Arraylist that holds the list of questions.
	 * @param gradesWriter Writer object that writes to GUI.
	 * @param df Defines the floating point precision.
	 */
	void goThroughAllSubmissions(SubmissionCollection sc,ArrayList<Submission> sa, ArrayList<Question> questions,
										 PrintWriter gradesWriter, DecimalFormat df) {
		ArrayList<Question> currQuestions = null;		// current question(s) for a submitted answer

		Map<Integer, ArrayList<Question>> questionToAnswer = createQuestionToAnswer(questions);

		// Iterate through all the student's submissions.
		for (int sIndex = 0; sIndex < sc.getTotalSubmissions(); sIndex++) {
			Submission s = sa.get(sIndex);
			Utilities.threadSafeOutput("\nEvaluating " + s.getSubmissionFileName() + ": \n    ");

			ArrayList<QueryEvaluation> queryEvals = new ArrayList<>();

			// initialize output point string for grade summary file
			String outputPointString = ": ";

			// process each question answer in the submission
			ArrayList<QuestionAnswer> qas = s.getAnswers();
			if (qas != null) {
				outputPointString=gradeSubmission(questions, qas, questionToAnswer
						, queryEvals, df, s);
			}
			// write each total grade to grades file
			try {
				String formattedString= String.format("%s : %"+(60 -s.getStudentName().length())+".2f %s", s.getStudentName(), s.getTotalPoints(),   outputPointString.substring(0,outputPointString.length()-1));
				gradesWriter.println(formattedString);
			}
			catch (Exception e) {
				System.err.println(this.getClass().getSimpleName() + " Error in writing to grades file " + gradesFileName);
			}
		}

	}	// end - for each submission

	/**
	 * Grade a submission.
	 * @param questions List of questions
	 * @param qas	List of answers
	 * @param questionToAnswer Map containing question to answer data
	 * @param queryEvals List containing evaluation params
	 * @param df Defines the floating point precision.
	 * @param s Submission
	 * @return outputPointString
	 */
	String gradeSubmission(ArrayList<Question> questions, ArrayList<QuestionAnswer> qas,Map<Integer, ArrayList<Question>> questionToAnswer
						 ,ArrayList<QueryEvaluation> queryEvals, DecimalFormat df, Submission s){
		ArrayList<Question> currQuestions = null;
		double submissionPoints = 0;
		String outputPointString = ": ";

		for (QuestionAnswer qa : qas) {
			// get the next answer for this submission
			Utilities.threadSafeOutput("Q" + qa.getQNumStr() + ".");

			Query actualQuery = qa.getActualQuery();

			// find the matching question(s) for the answer
			// is it possible that a student has not answered all the parts. What happens in this case?
			Integer questionNo = (int) qa.getQNumStr().charAt(0) - 48;
			currQuestions = questionToAnswer.getOrDefault(questionNo, null);

			// loop through all possible questions, evaluate, choose max
			double highestPoints = -1.0;            // set below zero so any evaluation is better
			double qPoints = 0.0;
			QueryEvaluation qe = null;
			QueryEvaluation maxQE = null;

			for (Question currQuestion : currQuestions) {
				// get the desired query for this question
				Query desiredQuery = currQuestion.getDesiredQuery();

				// get the evaluation components for this question
				ArrayList<EvalComponentInQuestion> questionEvalComps = currQuestion.getTests();
				int maxPoints = currQuestion.getQuestionPoints();

				QueryEvaluationLists queryEvaluationLists = new QueryEvaluationLists();

				Map<String, QueryEvaluationLists> questionNoToEvaluationMetrics = queryEvaluationLists.createQuestionNoToEvaluationMetricsMap(questions);

				queryEvaluationLists = questionNoToEvaluationMetrics.get(currQuestion.getQNumStr());


				// build a query evaluation, evaluate and add this qe to the current submission
				qe = new QueryEvaluation(actualQuery, desiredQuery, dao, maxPoints,
						queryEvaluationLists.getQuestionTests(), queryEvaluationLists.getQuestionPcts(),
						queryEvaluationLists.getQuestionConditions(), null, 0.0);

				qPoints = qe.evaluate();

				// use maximum score if multiple options for question
				if (qPoints > highestPoints) {
					highestPoints = qPoints;
					maxQE = qe;
				}
			}        // end - for each question
			queryEvals.add(maxQE);                    // add best qe for this answer to the list

			submissionPoints += highestPoints;        // add the highest question score to the submission total

			outputPointString += (df.format(highestPoints) + ", ");    // add highest points to string for grade summary output
		}    // end - for each question answer

		s.setTotalPoints(submissionPoints);                // add the total points to the submission
		s.setQueryEvals(queryEvals);                    // add the query evaluations to the submission

		s.writeSubmission(evaluationFolderPath);        // write out each submission's output file
		return outputPointString.substring(0, outputPointString.length()-1);
	}


	/** Method to create a map of questions
	 *
	 * @param questions Array list containing a list of questions.
	 * @return Arraylist from Integer(question number) to list of questions.
	 */
	Map<Integer, ArrayList<Question>> createQuestionToAnswer(ArrayList<Question> questions) {


		Map<Integer, ArrayList<Question>>  questionToAnswer = new HashMap<>();

		// iterate through the list of questions.
		for( Question question: questions){
			// get the question number
			Integer questionNo = Integer.parseInt(String.valueOf(question.getQNumStr().charAt(0)));

			//Add the question to the map.
			questionToAnswer.putIfAbsent(questionNo, new ArrayList<>());
			questionToAnswer.get(questionNo).add(question);
		}
		return questionToAnswer;
	}



}	// end - class BackEnd
