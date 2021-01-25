/*
 * Console - class to redirect console output to text area in JavaFX GUI
 * 
 * Created - Paul J. Wagner, 2021-JAN-14
 */
package sqlfe.general;

import java.io.IOException;
import java.io.OutputStream;

import javafx.scene.control.TextArea;

public class Console extends OutputStream {
	// data 
	private TextArea output;

	// methods
	// --constructor
    public Console(TextArea ta) {
         this.output = ta;
    }

    // -- write a character at a time 
    public void write(int i) throws IOException {
         output.appendText(String.valueOf((char) i));
    }

}	// end - class Console
