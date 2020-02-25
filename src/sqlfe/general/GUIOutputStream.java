/*
 * GUIOutputStream - class to channel standard output to text area in GUI
 * 
 * Created by Paul J. Wagner, 09-OCT-2019
 */
package sqlfe.general;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class GUIOutputStream extends OutputStream {
	private JTextArea textArea;					// local text area
	private JScrollPane scrollPane;				// local scroll pane
	
	public GUIOutputStream(JTextArea textArea, JScrollPane scrollPane) {
		this.textArea = textArea;
		this.scrollPane = scrollPane;
	}

	@Override
	public void write(int b) throws IOException {
		// 0. scrolls the text area to the end of the data
		//textArea.setCaretPosition(textArea.getDocument().getLength());
		//JScrollBar sb = scrollPane.getVerticalScrollBar();
		//sb.setValue(sb.getMaximum());
		// 1. redirects data to the text area
		//textArea.append(String.valueOf((char)b));
		textArea.setText(textArea.getText() + String.valueOf((char)b));
		// 2. scrolls the text area to the end of the data
		textArea.setCaretPosition(textArea.getDocument().getLength());
		// 3. keeps the textArea up to date
		textArea.update(textArea.getGraphics());
	}	// end - method write
	
}	// end - class GUIOutputStream
