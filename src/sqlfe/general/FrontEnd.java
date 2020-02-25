/*
 * FrontEnd - class to hold data obtained from FrontEndView class
 * 
 * Created by Paul J. Wagner on 26-Aug-2019
 */
package sqlfe.general;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

@SuppressWarnings("unused")
public class FrontEnd {
	// data
	private String dbmsChoice;			// DBMS being used for evaluation (e.g. Oracle, MySQL)
	private String dbmsHost;			// host name or number for evaluation DBMS
	private String dbmsSystemID;		// host system/ID to identify system on host
	private String dbmsUsername;		// DBMS username
	private String dbmsPassword;		// DBMS password
	private String evaluationFolder;	// top-level folder on run system for files, evaluations, assignment properties, etc.
	private String assignPropFile;		// name of assignment properties file to use in evaluation
	
	private BackEnd aBackEnd;			// backend evaluation system to pass data to
	
	// methods
	// constructors
	// default constructor
	public FrontEnd () {
		// nothing right now
	}
	
	public FrontEnd (BackEnd aBackEnd) {
		this.aBackEnd = aBackEnd;
	}
	
	// getters and setters
	public String getDbmsChoice() {
		return dbmsChoice;
	}

	public void setDbmsChoice(String dbmsChoice) {
		this.dbmsChoice = dbmsChoice;
	}

	public String getDbmsHost() {
		return dbmsHost;
	}

	public void setDbmsHost(String dbmsHost) {
		this.dbmsHost = dbmsHost;
	}

	public String getDbmsSystemID() {
		return dbmsSystemID;
	}

	public void setDbmsSystemID(String dbmsSystemID) {
		this.dbmsSystemID = dbmsSystemID;
	}

	public String getDbmsUsername() {
		return dbmsUsername;
	}

	public void setDbmsUsername(String dbmsUsername) {
		this.dbmsUsername = dbmsUsername;
	}

	public String getDbmsPassword() {
		return dbmsPassword;
	}

	public void setDbmsPassword(String dbmsPassword) {
		this.dbmsPassword = dbmsPassword;
	}

	public String getEvaluationFolder() {
		return evaluationFolder;
	}

	public void setEvaluationFolder(String evaluationFolder) {
		this.evaluationFolder = evaluationFolder;
	}

	public String getAssignPropFile() {
		return assignPropFile;
	}

	public void setAssignPropFile(String assignPropFile) {
		this.assignPropFile = assignPropFile;
	}

	public BackEnd getaBackEnd() {
		return aBackEnd;
	}

	public void setaBackEnd(BackEnd aBackEnd) {
		this.aBackEnd = aBackEnd;
	}

	
	// processInput - process the GUI input and set for backend usage
	public void processInput(String dbmsChoice, String dbmsHost, String dbmsSystemID, String dbmsUsername, String dbmsPassword,
								String evaluationFolder, String assignPropFile) {
		// populate FrontEnd
		//System.out.println("in FrontEnd, method processInput() - setting data members using passed values");
		setDbmsChoice(dbmsChoice);
		setDbmsHost(dbmsHost);
		setDbmsSystemID(dbmsSystemID);
		setDbmsUsername(dbmsUsername);
		setDbmsPassword(dbmsPassword);
		setEvaluationFolder(evaluationFolder);
		setAssignPropFile(assignPropFile);
		//System.out.println("in FrontEnd, method processInput() - after setting data, before calling BackEnd.process()");
		
		// write FrontEnd information out to properties file (set if new, replace if a value already there)
		final String CONFIG_PROP_NAME = "config.properties";
		Properties configProp = new Properties();
		File propFile = new File(CONFIG_PROP_NAME);
/*		if (propFile.exists()) {			// replace old values
			configProp.replace("dbmsChoice", dbmsChoice);
			configProp.replace("dbmsHost", dbmsHost);
			configProp.replace("dbmsSystemID", dbmsSystemID);
			configProp.replace("dbmsUsername", dbmsUsername);
			configProp.replace("evalFolder", evaluationFolder);
			configProp.replace("assignPropFile", assignPropFile);	
		} else {						// write new values
*/			configProp.setProperty("dbmsChoice", dbmsChoice);
			configProp.setProperty("dbmsHost", dbmsHost);
			configProp.setProperty("dbmsSystemID", dbmsSystemID);
			configProp.setProperty("dbmsUsername", dbmsUsername);
			configProp.setProperty("evalFolder", evaluationFolder);
			configProp.setProperty("assignPropFile", assignPropFile);
/*		}
*/		try {
			configProp.store(new FileOutputStream(CONFIG_PROP_NAME), null);
		} catch (IOException ioe) {
			System.err.println("error saving config properties");
		}		
		// send FrontEnd information to BackEnd for processing
		aBackEnd.process(this);
	}	// end - method processInput
	
}	// end - class FrontEnd
