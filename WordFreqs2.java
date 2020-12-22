import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;

/***
 * This program counts the words inside of a text file, using a Red-Black Tree. 
 * It will take a single command-line argument designating the file to open. 
 * The program will then:
 * 1. Count the frequencies of every word in the file.
 * 2. Output the total number of distinct words it found.
 * 3. Offer the user a prompt, to query the exact count of a particular word. 
 * If the user enters a word prepended with a -, it will delete the word.
 * This will continue until the user just hits enter, at which point the program
 * will terminate.
 * 
 * @author Colin Monaghan
 *
 */
public class WordFreqs2{

	
	/**
	 * This method reads the file and adds all the distinct words to the Red-Black Tree (with a value of 1). 
	 * If a word is already inside the tree, it increments the value by 1. At the end of the file, 
	 * this method prints out the total number of words in the tree.
	 * 
	 * @param filename the name of the file to read
	 * @param tree the Red-Black Tree to add the words to
	 * @throws IOException
	 */
	private static void readFile(String filename, RedBlackTree<String, Integer> tree) throws IOException {
		
	
		//start reading the file
		System.out.println("Loading file:\""+filename+"\".");
		
		File file = new File(filename);  
		if(!file.exists()) {
			System.out.println("Invalid file name.");
			System.exit(0);
		}
		FileReader fr=new FileReader(file);   		//reads the file  
		BufferedReader br=new BufferedReader(fr);  	//creates a buffering character input stream  
		
		
		//Count the frequencies of every word in the file.
		String line;
		String delimiter = Pattern.compile("[^a-z0-9_']").toString();
		while((line = br.readLine())!= null) {
			//split the line apart by the the delimiter
			String[] tokens = line.toLowerCase().split(delimiter);
			//cycle through the words of the line
			for(String element: tokens) {
				//add each element to the Tree
				//check for apostrophes at the beginning or end of the word
				while(element.startsWith("'")) element = element.substring(1, element.length());
				while(element.endsWith("'")) element = element.substring(0, element.length()-1);
				
				if(element.length() > 0) {
					//if the element is already in the tree, increment it.
					if(tree.contains(element)) tree.put(element, tree.get(element)+1);
					//else, add it to the tree with the value of 1.
					else tree.put(element, 1);
					
				}
			}
		}
		//Output the total number of distinct words it found
		System.out.println("This text contains "+tree.size()+" distinct words.");
		//close the files
		br.close();
		fr.close();
	}
	
	/**
	 * This method offers the user a prompt to query the Tree for the exact 
	 * count of a particular word. The user can enter a word, it will print out 
	 * the frequency of the word. If the word is preceded with a '-', then the 
	 * method will delete the word from the Tree. < and > will get the first 
	 * key and the last key, respectively. <word and >word will get the predecessor 
	 * and successor of the word in question. &word will get the rank of a word, 
	 * while *rank will get the word with integer rank given. This will continue until 
	 * the user enters an empty prompt, then it will quit.
	 * 
	 * @param tree the Red-Black Tree that maps Strings to Integers.
	 */
	private static void userInterface(RedBlackTree<String, Integer> tree) {

		//Offer the user a prompt, to query the exact count of a particular word
		System.out.println("Please enter a word to get its frequency, or hit enter to leave.");
		boolean repeat = true;
		Scanner scanner = new Scanner(System.in);
		while(repeat) {
			System.out.print("> ");
			String line = scanner.nextLine();
			
			//Continue until the user just hits enter, at which point it will terminate.
			if(line.length() == 0) repeat = false;
			//delete
			else if(line.startsWith("-")) {
				line = line.substring(1, line.length());
				//If the user enters a word prepended with a -, it will delete the word.
				tree.delete(line);
				System.out.println("\""+line+"\" has been deleted.");
			}
			//first word
			else if(line.startsWith("<") && line.length() == 1) {
				System.out.println("The alphabetically-first word in the text is \""+tree.getMinKey()+"\".");
			}
			//last word
			else if(line.startsWith(">") && line.length() == 1) {
				System.out.println("The alphabetically-last word in the text is \""+tree.getMaxKey()+"\".");
			}
			//size of tree
			else if(line.startsWith("#") && line.length() == 1) {
				System.out.println("The size of the tree is "+tree.size()+".");
			}
			//successor word
			else if(line.startsWith(">")) {
				line = line.substring(1, line.length());
				String succ = tree.findSucessor(line);
				if(succ != null) System.out.println("The word \""+succ+"\" comes after \""+line+"\".");
				else System.out.println("\""+line+"\" does not have a successor.");
			}
			//predecessor word
			else if(line.startsWith("<")) {
				line = line.substring(1, line.length());
				String pred = tree.findPredecessor(line);
				if(pred != null) System.out.println("The word \""+pred+"\" comes before \""+line+"\".");
				else System.out.println("\""+line+"\" does not have a predecessor.");
			}
			//get the rank of the word
			else if(line.startsWith("&")) {
				line = line.substring(1, line.length());
				int rank = tree.findRank(line);
				if(rank >= 0) System.out.println("The word \""+line+"\" has a rank of "+rank+".");
				else System.out.println("The word \""+line+"\" does not have a rank.");
				
				
			}
			//get the word with the given rank
			else if(line.startsWith("*")) {
				try{
					line = line.substring(1, line.length());
					int rank = Integer.parseInt(line);
					String word = tree.select(rank);
					if(word != null) System.out.println("The word \""+word+"\" has a rank of "+rank+".");
					else System.out.println("There is no word with rank "+rank+".");
				}
				catch(NumberFormatException e) {
					System.out.println("\""+line+"\" is not an integer.");
				}
			}
			else {
				//query the exact count of that particular word
				int value;
				if(tree.get(line) == null) value = 0;
				else value = tree.get(line);
				
				if(value > 0) System.out.println("\""+line+"\" appears "+value+" times.");
				else  System.out.println("\""+line+"\" does not appear.");
			}
		}
		System.out.println("\nGoodbye.");
		scanner.close();
	}
	
	/**
	 * This is the main method.
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		
		//if arguments are empty, print a polite message and quit
		if(args.length != 1) {
			System.out.println("Invalid amount of arguments.");
			System.exit(0);
		}
		try {
			//Create the RedBlackTree
			RedBlackTree<String, Integer> tree = new RedBlackTree<>();
			
			//read the file and fill Tree
			readFile(args[0], tree);
			//enter user interface loop
			userInterface(tree);
			
			
		}
		catch(Exception e) { 
			e.printStackTrace();  
		}  

	}



}
