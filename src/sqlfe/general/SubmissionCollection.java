/*
 * SubmissionCollection - class to hold all submissions for an assignment
 * 
 * Created - Paul J. Wagner, 21-Sep-2018
 */
package sqlfe.general;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import sqlfe.util.Utilities;

public class SubmissionCollection {
	// data
	private ArrayList<Submission> submissions;			// collection of all submissions
	private int totalSubmissions;						// count of submissions
	private ArrayList<Double> submissionMarks;			// parallel collection of marks for all submissions

	
	// methods
	// constructors
	// all-arg constructor
	public SubmissionCollection(ArrayList<Submission> submissions, int totalSubmissions,
			ArrayList<Double> submissionMarks) {
		super();
		this.submissions = submissions;
		this.totalSubmissions = totalSubmissions;
		this.submissionMarks = submissionMarks;
	}

	// default constructor
	public SubmissionCollection() {
		this(null, 0, null);
	}

	// getters and setters
	public ArrayList<Submission> getSubmissions() {
		return submissions;
	}

	public void setSubmissions(ArrayList<Submission> submissions) {
		this.submissions = submissions;
	}

	public int getTotalSubmissions() {
		return totalSubmissions;
	}

	public void setTotalSubmissions(int totalSubmissions) {
		this.totalSubmissions = totalSubmissions;
	}

	public ArrayList<Double> getSubmissionMarks() {
		return submissionMarks;
	}

	public void setSubmissionMarks(ArrayList<Double> submissionMarks) {
		this.submissionMarks = submissionMarks;
	}

	// hashCode
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((submissionMarks == null) ? 0 : submissionMarks.hashCode());
		result = prime * result + ((submissions == null) ? 0 : submissions.hashCode());
		result = prime * result + totalSubmissions;
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
		SubmissionCollection other = (SubmissionCollection) obj;
		if (submissionMarks == null) {
			if (other.submissionMarks != null)
				return false;
		} else if (!submissionMarks.equals(other.submissionMarks))
			return false;
		if (submissions == null) {
			if (other.submissions != null)
				return false;
		} else if (!submissions.equals(other.submissions))
			return false;
		if (totalSubmissions != other.totalSubmissions)
			return false;
		return true;
	}	// end - method equals

	// toString
	@Override
	public String toString() {
		return "SubmissionCollection [submissions=" + submissions + ", totalSubmissions=" + totalSubmissions
				+ ", submissionMarks=" + submissionMarks + "]";
	}
	
	// getAllFiles
	public void getAllFiles(String folderPath, String evaluationFolderPath, String assignmentName) {
		submissions = new ArrayList<Submission>();				// list of submissions
		int fileCount = 0;										// count of submission files read
		PrintWriter commWriter = null;							// comment file writer
		PrintWriter parseWriter = null;							// parse file writer
		
		// -- set up comments file name for comments from each submission file
		String commFileName = evaluationFolderPath + "AAA_student_comments.out";
		// -- set up parsing file name for parsing problems information from submission files
		String parseFileName = evaluationFolderPath + "AAA_parse_problems.out";		

		// process all each submission
		try {
			// output files setup
			commWriter = new PrintWriter(commFileName, "UTF-8");
			parseWriter = new PrintWriter(parseFileName, "UTF-8");
			
			// output general comment file information
			commWriter.println("Assignment  : " + assignmentName);
			commWriter.println("");
		
			// output general parse file information
			parseWriter.println("Assignment  : " + assignmentName);
			parseWriter.println("");
			
			// process all files
			File folder = new File(folderPath);
			File[] listOfFiles = folder.listFiles();
			for (int index = 0; index < listOfFiles.length; index++) {
				if (listOfFiles[index].isFile()) {
					String fileName = listOfFiles[index].getName();
					Utilities.threadSafeOutput("Parsing file: " + fileName + "\n");
					Submission s = new Submission();
					s.readSubmission(folderPath + fileName, commWriter, parseWriter, false);	// get submission information
					String submissionFileName = s.getSubmissionFileName();
					s.setSubmissionFileName(submissionFileName.substring(submissionFileName.lastIndexOf("/") + 1));
					// add this submission to the list
					submissions.add(s);
					fileCount++;
				}
				else if (listOfFiles[index].isDirectory()) {
					// nothing for now - shouldn't be finding subdirectory
				}
			}	// end - for
		} catch (IOException ioe) {
			System.err.println("IOException in writing to file " + commFileName + " or " + parseFileName);
		} finally {
			commWriter.close();
			parseWriter.close();
		}
		totalSubmissions = fileCount;
	}	// end - method getallFiles
	
}	// end - class SubmissionCollection
