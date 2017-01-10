package tweetAnalysis;

import org.ahocorasick.trie.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.JFileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Runnable {
	private static HashSet<String> negative;
	private static HashSet<String> neutral;

	private static Trie negativeTrie;
	private static Trie neutralTrie;

	//patterns used to extract messages
	private static String pattern1 = "message->";
	private static String pattern2 = "geotag->";
	private static Pattern p = Pattern.compile(Pattern.quote(pattern1) + "(.*?)" + Pattern.quote(pattern2));
	// run from here
	public static void main(String[] args) throws IOException {
		// set up the list of negative and neutral words
		setWordList();

		// open tweet file
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = fileChooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			FileReader file = new FileReader(fileChooser.getSelectedFile());
			// A buffer to read file
			BufferedReader br = new BufferedReader(file);
			String line = br.readLine();
			while(line != null){
				Matcher m = p.matcher(line);
				if(m.find()){
					//Collection<Emit> emits = negativeTrie.parseText(line);
					Collection<Emit> emits1 = neutralTrie.parseText(line);
					//System.out.println(emits);
					System.out.println(emits1);
				}
				line = br.readLine();
			}
			br.close();
		}

	}

	/*
	 * set up the list of negative and neutral words. Use negativeTrie and
	 * neutralTrie as dictionary to search for those keywords
	 */
	private static void setWordList() throws FileNotFoundException, IOException {
		// Choose excel file to get list of negative and neutral words
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = fileChooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			File excel = fileChooser.getSelectedFile();
			FileInputStream fileIn = new FileInputStream(excel);
			XSSFWorkbook book = new XSSFWorkbook(fileIn);
			// Get Negative sheet
			XSSFSheet negativeSheet = book.getSheet("Negative");
			// List of negative words
			negative = new HashSet<String>();
			// Get Column contains negative words
			Iterator<Row> rows = negativeSheet.rowIterator();
			rows.next();
			while (rows.hasNext()) {
				Row row = rows.next();
				Cell cell = row.getCell((short) 0);
				negative.add(cell.getStringCellValue());

			}

			// Get Neutral sheet
			XSSFSheet neutralSheet = book.getSheet("Neutral");
			// List of negative words
			neutral = new HashSet<>();
			// Get Column contains negative words
			Iterator<Row> rows1 = neutralSheet.rowIterator();
			rows1.next();
			while (rows1.hasNext()) {
				Row row = rows1.next();
				Cell cell = row.getCell((short) 0);
				neutral.add(cell.getStringCellValue());
			}
			
			// close xssl file
			book.close();

		}

		// setup trie
		
//		negativeTrie = Trie.builder().onlyWholeWordsWhiteSpaceSeparated().addKeywords(negative).build();
		negativeTrie = Trie.builder().addKeywords(negative).build();
		System.out.println(negative);
//		neutralTrie = Trie.builder().onlyWholeWordsWhiteSpaceSeparated().addKeywords(neutral).build();
		neutralTrie = Trie.builder().addKeywords(neutral).build();
		System.out.println(neutral);
	}

}
