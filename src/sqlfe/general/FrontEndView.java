/*
 * FrontEndView - GUI class (in Swing) to present a GUI interface to the SQL File Evaluation system
 * 
 * Created by Paul J. Wagner on 21-Aug-2019
 */
package sqlfe.general;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;

import javax.swing.*;

public class FrontEndView extends JFrame {
	private static final long serialVersionUID = 1L;	// to declare serializable, avoid warning
	private FrontEnd aFrontEnd = null;					// holder class for front end view data
	@SuppressWarnings("unused")
	private BackEnd aBackEnd = null;					// main processing system to work with front end
	private JTextArea displayArea = null;				// display for status
	private JScrollPane displayPanel = null;			// scrollable panel to hold display text area
    private JComboBox<String> dbmsBox;					// combo box for DBMS choice
	private JTextField hostBox = null;					// DBMS host (name or number string)
	private JTextField portBox = null;					// DBMS port
	private JTextField idBox = null;					// DBMS id
	private JTextField userBox = null;					// DBMS username
	private JTextField passBox = null;					// DBMS password
	private JFileChooser workAreaChooser = null;		// chooser for work area for evaluation
	private JTextField assignpropBox = null;			// assignment evaluation properties
	
	// constructor
	public FrontEndView(FrontEnd someFrontEnd) {
		super();
		initialize();
		aFrontEnd = someFrontEnd; 
	}	// end - constructor
	
	// initialize - method to set up the graphical display
	private void initialize() {
		JPanel sqlFileEvalContentPane = new JPanel();	// overall content panel
	    String[] choices = { "Oracle", "MySQL" };		// DBMS choices currently implemented
	    dbmsBox = new JComboBox<String>(choices);		// combox box for DBMS choices
		hostBox = new JTextField("");					// DBMS hostname/number
		portBox = new JTextField("");					// DBMS port
		idBox = new JTextField("");						// DBMS id 
	    userBox = new JTextField("");					// DBMS username
		passBox = new JTextField("");					// DBMS password
		workAreaChooser = new JFileChooser("");			// file/folder chooser for work area for evaluation
		assignpropBox = new JTextField("assignmentProperties");		// assignment evaluation properties
		displayArea = new JTextArea(10, 35);			// status display area
		displayPanel = new JScrollPane(displayArea);	// status scrollable panel
		JLabel title = new JLabel();					// title in content panel
		JLabel dbLabel   = new JLabel();				// label - DBMS choice
		JLabel hostLabel = new JLabel();				// label - DBMS hostname/number
		JLabel portLabel = new JLabel();				// label - DBMS port
		JLabel idLabel = new JLabel();					// label - DBMS id
		JLabel userLabel = new JLabel();				// label - DBMS username
		JLabel passLabel = new JLabel();				// label - DBMS password
		JLabel workareaLabel = new JLabel();			// label - evaluation work area folder
		JLabel assignpropLabel = new JLabel();			// label - assignment properties file
		JLabel displayLabel = new JLabel();				// label - status display area
		Button startButton = new Button("Start Evaluation"); // button to start evaluation processing
		
		try {
			// set up the application frame
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			setSize(640, 520);
			setTitle("SQL File Evaluation");
	
			// construct the title label
			title.setFont(new java.awt.Font("Arial", 1, 30));
			title.setText("SQL File Evaluation");
			title.setBounds(13, 10, 600, 31);
			title.setForeground(java.awt.Color.blue);
			title.setHorizontalAlignment(SwingConstants.CENTER);
			
			// construct the text labels
			dbLabel.setFont(new java.awt.Font("Arial", 1, 14));
			dbLabel.setText("DBMS Choice:");
			dbLabel.setBounds(50, 40, 140, 35);
			dbLabel.setForeground(java.awt.Color.black);

			hostLabel.setFont(new java.awt.Font("Arial", 1, 14));
			hostLabel.setText("DB Host:");
			hostLabel.setBounds(50, 100, 140, 35);
			hostLabel.setForeground(java.awt.Color.black);

			portLabel.setFont(new java.awt.Font("Arial", 1, 14));
			portLabel.setText("DB Port:");
			portLabel.setBounds(50, 160, 140, 35);
			portLabel.setForeground(java.awt.Color.black);
						
			idLabel.setFont(new java.awt.Font("Arial", 1, 14));
			idLabel.setText("DB System/ID:");
			idLabel.setBounds(50, 220, 140, 35);
			idLabel.setForeground(java.awt.Color.black);
			
			userLabel.setFont(new java.awt.Font("Arial", 1, 14));
			userLabel.setText("DB Username:");
			userLabel.setBounds(50, 280, 140, 35);
			userLabel.setForeground(java.awt.Color.black);

			passLabel.setFont(new java.awt.Font("Arial", 1, 14));
			passLabel.setText("DB Password:");
			passLabel.setBounds(50, 340, 140, 35);
			passLabel.setForeground(java.awt.Color.black);

			assignpropLabel.setFont(new java.awt.Font("Arial", 1, 14));
			assignpropLabel.setText("Assignment Properties file:");
			assignpropLabel.setBounds(50, 400, 210, 35);
			assignpropLabel.setForeground(java.awt.Color.black);
			
			workareaLabel.setFont(new java.awt.Font("Arial", 1, 14));
			workareaLabel.setText("Evaluation Work Area folder:");
			workareaLabel.setBounds(280, 50, 220, 35);
			workareaLabel.setForeground(java.awt.Color.black);
			
			displayLabel.setFont(new java.awt.Font("Arial", 1, 14));
			displayLabel.setText("Evaluation Processing Status:");
			displayLabel.setBounds(280, 350, 220, 35);
			displayLabel.setForeground(java.awt.Color.black);

			// construct combo box for db choices
		    dbmsBox.setBounds(50,  70, 160, 25);
		    dbmsBox.setEditable(true);
			
			// construct the text fields / boxes
			hostBox.setBounds(50, 130, 200, 25);
			hostBox.setEditable(true);

			portBox.setBounds(50, 190, 200, 25);
			portBox.setEditable(true);
			
			idBox.setBounds(50, 250, 200, 25);
			idBox.setEditable(true);
		    
		    userBox.setBounds(50, 310, 200, 25);
			userBox.setEditable(true);
			
			passBox.setBounds(50, 370, 200, 25);
			passBox.setEditable(true);

			assignpropBox.setBounds(50, 430, 210, 25);
			assignpropBox.setEditable(true);
 			
			workAreaChooser.setBounds(280, 80, 300, 270);
			workAreaChooser.setCurrentDirectory(null);
			workAreaChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			workAreaChooser.setControlButtonsAreShown(false);

		    // read in config properties file
			final String CONFIG_PROP_NAME = "config.properties";
		    Properties configProp = new Properties();
			File propFile = new File(CONFIG_PROP_NAME);
			if (propFile.exists()) {			// replace old values
				try {
					InputStream in = new FileInputStream(CONFIG_PROP_NAME);
					configProp.load(in);
				    dbmsBox.setSelectedItem(configProp.getProperty("dbmsChoice"));
					hostBox.setText(configProp.getProperty("dbmsHost"));
					portBox.setText(configProp.getProperty("dbmsPort"));
					idBox.setText(configProp.getProperty("dbmsSystemID"));
					userBox.setText(configProp.getProperty("dbmsUsername"));
					//workAreaChooser.setCurrentDirectory(new File(configProp.getProperty("evalFolder")));
					workAreaChooser.setSelectedFile(new File(configProp.getProperty("evalFolder")));		// ??? also need to set selected file so can retrieve when sending to backend
					assignpropBox.setText(configProp.getProperty("assignPropFile"));
					in.close();
				} catch (IOException e) {
					System.err.println("problem reading config properties");
				}
			}
			
			// construct the display text area
			displayArea.setBounds(280, 380, 300, 80);
			displayArea.setEditable(false);
			displayArea.setLineWrap(true);
			
			displayPanel.setBounds(280, 380, 300, 80);		// temporarily commented for testing TextAreaOutputStream
		
			// redirect standard output to the display text area
			// PrintStream printStream = new PrintStream(new GUIOutputStream(displayArea, displayPanel));
			//PrintStream printStream = new PrintStream(new TextAreaOutputStream(displayArea));		// tested new output stream class - not working
			// System.setOut(printStream);
			// System.setErr(printStream);

			// construct the button
			startButton.setBounds(330, 380, 180, 35);
			startButton.addActionListener( new ActionListener(){
				public void actionPerformed(ActionEvent e){							
					// send the input data to the FrontEnd for passing on to the BackEnd
					//System.out.println("workAreaChooser is: " + workAreaChooser);
					//System.out.println("wac.selected file is: " + workAreaChooser.getSelectedFile());
					//System.out.println("wac.sf.absolute path is: " + workAreaChooser.getSelectedFile().getAbsolutePath());
					//System.out.println();
					
					aFrontEnd.processInput( (String)dbmsBox.getSelectedItem(), hostBox.getText(), portBox.getText(), idBox.getText(), 
																	userBox.getText(), passBox.getText(),
																	workAreaChooser.getSelectedFile().getAbsolutePath(),
																	assignpropBox.getText() );
				} } );

			// --- construct the highest level content-pane
			sqlFileEvalContentPane.setLayout(null);
			sqlFileEvalContentPane.add(title);
			sqlFileEvalContentPane.add(dbLabel);
			sqlFileEvalContentPane.add(dbmsBox);
			sqlFileEvalContentPane.add(hostLabel);
			sqlFileEvalContentPane.add(hostBox);
			sqlFileEvalContentPane.add(portLabel);
			sqlFileEvalContentPane.add(portBox);
			sqlFileEvalContentPane.add(idLabel);			
			sqlFileEvalContentPane.add(idBox);	
			sqlFileEvalContentPane.add(userLabel);
			sqlFileEvalContentPane.add(userBox);			
			sqlFileEvalContentPane.add(passLabel);
			sqlFileEvalContentPane.add(passBox);			
			sqlFileEvalContentPane.add(assignpropLabel);
			sqlFileEvalContentPane.add(assignpropBox);
			
			sqlFileEvalContentPane.add(workareaLabel);
			sqlFileEvalContentPane.add(workAreaChooser);
			
			//sqlFileEvalContentPane.add(displayLabel);	

			//sqlFileEvalContentPane.add(displayArea);		// must be commented out for display panel to work
			//sqlFileEvalContentPane.add(displayPanel);
			sqlFileEvalContentPane.add(startButton);
				
			// --- finally, set the content pane overall
			setContentPane(sqlFileEvalContentPane);

		} catch (java.lang.Throwable ivjExc) {
			System.err.println("Exception occurred in initialize() of SQL File Evaluation application");
			ivjExc.printStackTrace(System.out);
		}
	}	// end - method initialize
	
	// start - method to start execution of the GUI
	public static void start() {
		try {			
			BackEnd theBackEnd = new BackEnd();
			FrontEnd theFrontEnd = new FrontEnd(theBackEnd);
			FrontEndView aFrontEndView = new FrontEndView(theFrontEnd);
	
			aFrontEndView.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosed(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
	
			aFrontEndView.setVisible(true);

		
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of FrontEndView");
			exception.printStackTrace(System.out);
		}		
	}	// end - method start
		
}	// end - class FrontEndView
