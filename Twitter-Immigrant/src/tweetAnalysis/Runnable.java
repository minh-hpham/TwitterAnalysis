package tweetAnalysis;

import org.ahocorasick.trie.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.hankcs.algorithm.AhoCorasickDoubleArrayTrie;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Runnable {
	private static HashSet<String> negative;
	private static HashSet<String> neutral;

	private static Trie negativeTrie;
	private static Trie neutralTrie;

	private static String formatStr = "%-30s %-120s %-15s %-15s %-10s %-10s %-35s %-35s\r\n";
	// patterns used to extract messages
	private static Pattern pId = Pattern.compile(Pattern.quote("<>userid->") + "(.*?)" + Pattern.quote("<>message->"));
	private static Pattern pMessage = Pattern.compile(
			Pattern.quote("<>message->") + "(.*?)" + Pattern.quote("http") + "(.*?)" + Pattern.quote("<>geotag->"));
	private static Pattern pGeo = Pattern
			.compile(Pattern.quote("<>geotag->") + "(.*?)" + Pattern.quote("<>followers->"));

	// run from here
	public static void main(String[] args) throws IOException {
		// set up the list of negative and neutral words
		setWordList();

		/*
		 * Collection<Emit> emits = negativeTrie.parseText(line);
		 * Collection<Emit> emits1 = neutralTrie.parseText(line);
		 */

		// open tweet file
		JFileChooser openedFile = new JFileChooser();
		openedFile.setDialogTitle("Choose file you want to run against");
		openedFile.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = openedFile.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			// Open read text file
			FileReader file = new FileReader(openedFile.getSelectedFile());
			// A buffer to read file
			BufferedReader br = new BufferedReader(file);
			String line = br.readLine();

			JFileChooser savedFile = new JFileChooser();
			savedFile.setDialogTitle("Specify a file to save");
			int userSelection = savedFile.showSaveDialog(null);

			if (userSelection == JFileChooser.APPROVE_OPTION) {
				File fileToSave = savedFile.getSelectedFile();
				// write to file
				try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileToSave))) {
					bw.write(String.format(formatStr, "TWEETID", "MESSAGE", "LATITUDE", "LONGITUDE", "NEGATIVE",
							"NEUTRAL", "LIST OF NEG. WORDS", "LIST OF NEU. WORDS"));
					while (line != null) {
						findMatchedWords(line, bw);
						line = br.readLine();
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
				JOptionPane.showMessageDialog(new JFrame(), "YOUR FILE HAS BEEN PROCESSED");
			} else {
				savedFile.cancelSelection();
			}
			br.close();
		} else {
			openedFile.cancelSelection();
		}

	}

	/*
	 * Extract tweetID, message, latitude and longtitude. Look up negative and
	 * neutral keywords in message
	 */
	private static void findMatchedWords(String line, BufferedWriter bw) throws IOException {
		String id, message, latitude, longitude;
		Collection<Emit> negativeList;
		Collection<Emit> neutralList;
		// find tweet ID
		Matcher m1 = pId.matcher(line);
		// find tweet message and list of negative/neutral words
		// matched in the message
		Matcher m2 = pMessage.matcher(line);
		// find longtitude and latitude
		Matcher m3 = pGeo.matcher(line);

		if (m1.find() && m2.find() && m3.find()) {
			// find tweet ID
			id = m1.group(1);

			// find message
			message = m2.group(1);
			negativeList = negativeTrie.parseText(message);
			// System.out.println(negativeList);
			neutralList = neutralTrie.parseText(message);
			// System.out.println(neutralList);

			// find longtitude and latitude
			String[] geotag = m3.group(1).split("\\s+");
			latitude = geotag[0];
			longitude = geotag[1];

			int hasNegative = negativeList.size() > 0 ? 1 : 0;
			int hasNeutral = neutralList.size() > 0 ? 1 : 0;
			// write to file
			bw.write(String.format(formatStr, id, message, latitude, longitude, hasNegative, hasNeutral,
					negativeList.toString(), neutralList.toString()));
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

		negativeTrie = Trie.builder().caseInsensitive().onlyWholeWordsWhiteSpaceSeparated().addKeywords(negative)
				.build();
		// negativeTrie = Trie.builder().addKeywords(negative).build();
		System.out.println(negative);
		neutralTrie = Trie.builder().caseInsensitive().onlyWholeWordsWhiteSpaceSeparated().addKeywords(neutral).build();
		// neutralTrie = Trie.builder().addKeywords(neutral).build();
		System.out.println(neutral);
	}

}
