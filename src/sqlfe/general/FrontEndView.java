/*
 * FrontEndView - JavaFX front end view for SQLFE
 * 
 * Created - Paul J. Wagner, 2020-DEC-04
 * Last Updated - Paul J. Wagner, 2021-JAN-11, added display area label, hooked up to backend and properties
 */
package sqlfe.general;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
 
public class FrontEndView extends Application {
	// data
	// -- GUI fields
    private final Text scenetitle 					= new Text("SQLFE: SQL File Evaluation");
    private final Label dbmsChoiceLabel 			= new Label("DBMS Choice:");
    private ComboBox<String> dbmsComboBox 			= null;									// constructed later
    private final Label dbmsHostLabel 				= new Label("DBMS Host:");
    private TextField hostTextField 				= new TextField();
    private final Label dbmsPortLabel 				= new Label("DBMS Port:");
    private TextField portTextField 				= new TextField();
    private final Label dbmsSysIDLabel 				= new Label("DBMS SysID:");
    private TextField sysidTextField				= new TextField();
    private final Label dbmsUserLabel 				= new Label("DBMS Username:");
    private TextField userTextField					= new TextField();
    private final Label dbmsPasswordLabel 			= new Label("DBMS Password:");
    private PasswordField pwPasswordField			= new PasswordField();
    private final Label asmtPropLabel 				= new Label("Assignment Properties file:");
    private TextField apTextField 					= new TextField();
    private final Label workAreaText 				= new Label("Work Area Folder:");
    private TextField waTextField 					= null;									// will assign later
    private final DirectoryChooser waFolderChooser	= new DirectoryChooser();
    private final Button wafButton 					= new Button("Change Work Area Folder");
    private Label statusDisplayText 				= new Label("Work Display");
    private TextArea statusTextArea 				= new TextArea(); 
    private final Button evalButton 				= new Button("Evaluate Files");
    // -- connection to rest of system
    private BackEnd aBackEnd						= new BackEnd();
    private FrontEnd aFrontEnd						= new FrontEnd(aBackEnd);				// front end needs back end tied to it to pass information to
    // -- console output to text area
    private Console console							= new Console(statusTextArea);		
	private PrintStream printStream					= new PrintStream(console, true);

    // methods
    // -- start (gets called indirectly by thread created when FrontEndView is launched in main)
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("SQL File Evaluation");
        
        // set up the grid
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);						// align from the center
        grid.setHgap(10);									// between each item horizontally
        grid.setVgap(10);									// between each item vertically
        grid.setPadding(new Insets(25, 25, 25, 25));		// margins around the whole grid

        // add the title
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 1, 0, 2, 1);
        
        // add the DBMS choices label and combo box
        grid.add(dbmsChoiceLabel, 0, 1);
        ObservableList<String> options = FXCollections.observableArrayList(
        	"Oracle", 
        	"MySQL 5.x", 
        	"MySQL 8.0",
        	"Mock"
        );
        dbmsComboBox = new ComboBox<String>(options);
        dbmsComboBox.getSelectionModel().selectFirst();
        dbmsComboBox.setVisibleRowCount(3);
        grid.add(dbmsComboBox, 1, 1);

        // add the DBMS Host label and text field
        grid.add(dbmsHostLabel, 0, 2);
        grid.add(hostTextField, 1, 2);

        // add the DBMS Port label and text field
        grid.add(dbmsPortLabel, 0, 3);
        grid.add(portTextField, 1, 3);
        
        // add the DBMS System/ID label and text field
        grid.add(dbmsSysIDLabel, 0, 4);
        grid.add(sysidTextField, 1, 4);
        
        // add the DBMS username label and text field
        grid.add(dbmsUserLabel, 0, 5);
        grid.add(userTextField, 1, 5);
                
        // add the DBMS pw label and text/password field
        grid.add(dbmsPasswordLabel, 0, 6);
        grid.add(pwPasswordField, 1, 6);        

        // add the assignment properties label and text field
        grid.add(asmtPropLabel, 0, 7);
        grid.add(apTextField, 1, 7);

        // add the work folder label and text field
        grid.add(workAreaText, 2, 2);
        String os = System.getProperty("os.name");
        if (os.contains("Windows")) {
        	waTextField = new TextField("C:\\");
        } else //if (os.contains("Linux")) {
        {
        	waTextField = new TextField("/");
        }
        grid.add(waTextField, 3, 2);
        
        // add the file chooser and button for the file chooser
        waFolderChooser.setTitle("Choose WAF");        
        wafButton.setOnAction(
        	new EventHandler<ActionEvent>() {
        		public void handle(final ActionEvent e) {
        			waFolderChooser.setInitialDirectory(new File(waTextField.getText()) );
        			File file = waFolderChooser.showDialog(primaryStage);
        			if (file != null) {
        				waTextField.setText(file.getAbsolutePath());
        			} else {
        				System.err.println("null folder from chooser");
        			}
        		}
        	}
        ); 
        grid.add(wafButton, 3, 1);
        
        // add the label and status text area
        grid.add(statusDisplayText, 2, 3);               
        grid.add(statusTextArea, 2, 4);
                
        // add the evaluate button
        evalButton.setOnAction(
        	new EventHandler<ActionEvent>() {
        		public void handle(final ActionEvent e) {
        			// start the evaluation process by processing the information from the view through the front end
					aFrontEnd.processInput( (String)dbmsComboBox.getValue(),
						hostTextField.getText(), portTextField.getText(), sysidTextField.getText(),
						userTextField.getText(), pwPasswordField.getText(), waTextField.getText(),
						apTextField.getText() );
        		}	// end - method handle
        	}	// end - new EventHandler
        );
        grid.add(evalButton, 3, 7);
        
	    // read in the config properties file
		final String CONFIG_PROP_NAME = "config.properties";
	    Properties configProp = new Properties();
		File propFile = new File(CONFIG_PROP_NAME);
		if (propFile.exists()) {			// replace old values
			try {
				InputStream in = new FileInputStream(CONFIG_PROP_NAME);
				configProp.load(in);
			    dbmsComboBox.getSelectionModel().select(configProp.getProperty("dbmsChoice"));
				hostTextField.setText(configProp.getProperty("dbmsHost"));
				portTextField.setText(configProp.getProperty("dbmsPort"));
				sysidTextField.setText(configProp.getProperty("dbmsSystemID"));
				userTextField.setText(configProp.getProperty("dbmsUsername"));
				waTextField.setText(configProp.getProperty("evalFolder"));
				apTextField.setText(configProp.getProperty("assignPropFile"));
				in.close();
			} catch (IOException e) {
				System.err.println("problem reading config properties");
			}
		}
		
		// connect console to display text area
		System.setOut(printStream);
		//System.setErr(printStream);
        
        // configure the grid
        GridPane.setRowSpan(statusTextArea, 3);
        GridPane.setColumnSpan(statusTextArea, 2);

        // create the scene
        Scene scene = new Scene(grid, 780, 300);
        primaryStage.setScene(scene);

        // display the scene
        primaryStage.show();
    }	// end - method start
    
}	// end - class FrontEndView
