/*
 * FrontEnd - class to hold data obtained from FrontEndView class
 * 
 * Created by Paul J. Wagner on 26-Aug-2019
 */
package sqlfe.general;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class FrontEnd {
	// data
	private String dbmsChoice;			// DBMS being used for evaluation (e.g. Oracle, MySQL)
	private String dbmsHost;			// host name or number for evaluation DBMS
	private String dbmsPort;			// port used by DBMS on host
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
	
	// one-arg (BackEnd) constructor
	public FrontEnd (BackEnd aBackEnd) {
		this.aBackEnd = aBackEnd;
	}
	
	// all-arg constructor
	public FrontEnd (String dbmsChoice, String dbmsHost, String dbmsPort, String dbmsSystemID, String dbmsUsername, String dbmsPassword,
								String evaluationFolder, String assignPropFile, BackEnd aBackEnd) {
		setDbmsChoice(dbmsChoice);
		setDbmsHost(dbmsHost);
		setDbmsPort(dbmsPort);
		setDbmsSystemID(dbmsSystemID);
		setDbmsUsername(dbmsUsername);
		setDbmsPassword(dbmsPassword);
		setEvaluationFolder(evaluationFolder);
		setAssignPropFile(assignPropFile);
		setABackEnd(aBackEnd);
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

	public String getDbmsPort() {
		return dbmsPort;
	}

	public void setDbmsPort(String dbmsPort) {
		this.dbmsPort = dbmsPort;
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

	public BackEnd getABackEnd() {
		return aBackEnd;
	}

	public void setABackEnd(BackEnd aBackEnd) {
		this.aBackEnd = aBackEnd;
	}
	
	
	// processInput - support transfer of GUI input into this class for later processing
	public void processInput(String dbmsChoice, String dbmsHost, String dbmsPort, String dbmsSystemID, String dbmsUsername, String dbmsPassword,
								String evaluationFolder, String assignPropFile) {
		setDbmsChoice(dbmsChoice); 
		setDbmsHost(dbmsHost); 
		setDbmsPort(dbmsPort);
		setDbmsSystemID(dbmsSystemID); 
		setDbmsUsername(dbmsUsername);
		setDbmsPassword(dbmsPassword); 
		setEvaluationFolder(evaluationFolder);
		setAssignPropFile(assignPropFile);
		
		// write FrontEnd information out to properties file (set if new, replace if a value already there)
		final String CONFIG_PROP_NAME = "config.properties";
		Properties configProp = new Properties();
		
		// write new values
		configProp.setProperty("dbmsChoice", dbmsChoice);
		configProp.setProperty("dbmsHost", dbmsHost);
		configProp.setProperty("dbmsPort",  dbmsPort);
		configProp.setProperty("dbmsSystemID", dbmsSystemID);
		configProp.setProperty("dbmsUsername", dbmsUsername);
		configProp.setProperty("evalFolder", evaluationFolder);
		configProp.setProperty("assignPropFile", assignPropFile);

		try {
			configProp.store(new FileOutputStream(CONFIG_PROP_NAME), null);
		} catch (IOException ioe) {
			System.err.println("error saving config properties");
		}		

		// send FrontEnd information to BackEnd for processing
		aBackEnd.process(this);				// start backend process in same thread - problems with GUI console output to text area
		
	}	// end - method processInput
		
}	// end - class FrontEnd
