package tweetAnalysis;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.hankcs.algorithm.AhoCorasickDoubleArrayTrie;

import sun.misc.Lock;

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Runnable4 {

	private static HashSet<String> dictionary = new HashSet<>();

	private static TreeMap<String, String> negative;
	private static TreeMap<String, String> neutral;

	private static AhoCorasickDoubleArrayTrie<String> negativeTrie = new AhoCorasickDoubleArrayTrie<String>();
	private static AhoCorasickDoubleArrayTrie<String> neutralTrie = new AhoCorasickDoubleArrayTrie<String>();

	private static String formatStr = "%s<>%s<>%s<>%s<>%s<>%s<>%s<>%s%n";
	// patterns used to extract messages

	private static Pattern p = Pattern.compile("^(.*?)" + Pattern.quote("<>") + "^(.*?)" + Pattern.quote("->"));



	private static Pattern pMessage = Pattern.compile(Pattern.quote("<>message->") + "(.*?)" + Pattern.quote("<>geotag->"));

	// run from here
	public static void main(String[] args) throws IOException {
		// set up the list of negative and neutral words
		setWordList();

		ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
		// read tweet file and put processed string into the "dictionary"
		readWriteLock.readLock().lock();
		try {
			readTweets();
		} finally {
			readWriteLock.readLock().unlock();
		}

		// write processed tweets into new file
		readWriteLock.writeLock().lock();
		try {
			writeTweets();
		} finally {
			readWriteLock.writeLock().unlock();
		}
		// Exit after done
		System.exit(0);
	}

	/*
	 * Used after compare tweets against keywords. Write compared lines into new
	 * file. Inform after done
	 */
	private static void writeTweets() {
		JFileChooser savedFile = new JFileChooser();
		savedFile.setDialogTitle("Specify a file to save");
		int userSelection = savedFile.showSaveDialog(null);

		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = savedFile.getSelectedFile();
			// write to file
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileToSave))) {
			
				if (dictionary.isEmpty() == false) {
					for (String s : dictionary) {
						bw.write(s);
					}
				}
				bw.close();
				JOptionPane.showMessageDialog(new JFrame(), "YOUR FILE HAS BEEN PROCESSED");
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			savedFile.cancelSelection();
			System.exit(0);
		}
	}

	/*
	 * Extract tweetID, message, latitude and longtitude. Look up negative and
	 * neutral keywords in message
	 */
	private static void readTweets() throws FileNotFoundException, IOException {
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
			
			StringBuffer sb = new StringBuffer();
			
			while (line != null) {
				List<AhoCorasickDoubleArrayTrie<String>.Hit<String>> negativeList, neutralList;
				// matched in the message
				Matcher mesg = pMessage.matcher(line);
				if (mesg.find()) {
					// find message
					String message = mesg.group(1);
					negativeList = negativeTrie.parseText(message);
					// System.out.println(negativeList);
					neutralList = neutralTrie.parseText(message);

					if (!(negativeList.isEmpty() && neutralList.isEmpty())) {
						String[] content = line.split("<>.*?->");
						String[] geotag = content[3].split("\\s+");
						
						String sth = content[0] + "<>" + content[1] + "<>" + content[7] + "<>" + content[6] + "<>"+ content[4] + "<>" + geotag[0] + "<>" + geotag[1] + "<>" + content[2];
						sb.append(sth);

						sb.append( "<>" + (negativeList.isEmpty() ? 0 : 1) );
						for (AhoCorasickDoubleArrayTrie<String>.Hit<String> s : negativeList) {
							sb.append("<>" + s);
						}

						sb.append( "<>" + (neutralList.isEmpty() ? 0 : 1) );
						for (AhoCorasickDoubleArrayTrie<String>.Hit<String> s : neutralList) {
							sb.append("<>" + s);
						}
						
						sb.append(System.getProperty("line.separator"));
						
						String output = sb.toString();
						dictionary.add(output);
						sb.delete(0, sb.length());
						
					}

				}

				line = br.readLine();
			}
			br.close();
		} else {
			openedFile.cancelSelection();
			System.exit(0);
		}
	}

	/*
	 * set up the list of negative and neutral words. Use negativeTrie and
	 * neutralTrie as dictionary to search for those keywords
	 */
	private static void setWordList() throws FileNotFoundException, IOException {
		// Choose excel file to get list of negative and neutral words
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Choose file contains categorized words");
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = fileChooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			File excel = fileChooser.getSelectedFile();
			FileInputStream fileIn = new FileInputStream(excel);
			XSSFWorkbook book = new XSSFWorkbook(fileIn);
			// Get Negative sheet
			XSSFSheet negativeSheet = book.getSheet("Negative");
			// List of negative words
			negative = new TreeMap<String, String>();
			// Get Column contains negative words
			Iterator<Row> rows = negativeSheet.rowIterator();
			rows.next();
			while (rows.hasNext()) {
				Row row = rows.next();
				Cell cell = row.getCell((short) 0);
				negative.put(cell.getStringCellValue(), cell.getStringCellValue());

			}

			// Get Neutral sheet
			XSSFSheet neutralSheet = book.getSheet("Neutral");
			// List of negative words
			neutral = new TreeMap<String, String>();
			// Get Column contains negative words
			Iterator<Row> rows1 = neutralSheet.rowIterator();
			rows1.next();
			while (rows1.hasNext()) {
				Row row = rows1.next();
				Cell cell = row.getCell((short) 0);
				neutral.put(cell.getStringCellValue(), cell.getStringCellValue());
			}

			// close xssl file
			book.close();
			// build negative trie
			negativeTrie.build(negative);
			// System.out.println(negative);
			// build neutral trie
			neutralTrie.build(neutral);
			// System.out.println(neutral);
		} else {
			fileChooser.cancelSelection();
			System.exit(0);
		}

	}

}
