package TwitterAnalysis;

import org.ahocorasick.trie.*;
import javax.swing.JFileChooser;
import java.io.File;    
public class Runnable {
	// run from here
	public static void main(String[] args) {
		// Choose twitter file to analyze
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = fileChooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
		    File selectedFile = fileChooser.getSelectedFile();
		    
		    System.out.println("Selected file: " + selectedFile.getAbsolutePath());
		}
		
	}

}
