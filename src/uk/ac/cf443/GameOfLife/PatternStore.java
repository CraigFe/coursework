package uk.cam.ac.cf443.GameOfLife;

import java.io.*;
import java.net.*;
import java.util.*;

public class PatternStore {

	//List of all the patterns in the store
	private List<Pattern> mPatterns = new LinkedList<>();

	//Map from author names to lists of patterns from that author
	private Map<String,List<Pattern>> mMapAuths = new HashMap<>();

	//Map from names to pattern objects
	private Map<String,Pattern> mMapName = new HashMap<>();

	/**
	 * Constructor from file input string (URL or filepath)
	 * @param source		A string specifying the filepath or URL of the file to be used
	 * @throws IOException	Throws exception if the file is not found / connection to file could not be made
	 */
	public PatternStore(String source) throws IOException {
		if (source.startsWith("http://")) {
			loadFromURL(source);
		}
		else {
			loadFromDisk(source);
		}
	}
	/**
	 * Constructor from a Reader object
	 * @param source		The reader object pointing to the pattern strings to be used
	 * @throws IOException	Throws an exception if the reader object is invalid
	 */
	public PatternStore(Reader source) throws IOException {
		load(source);
	}

	/**
	 * Loads the patterns from a Reader object into the mPatterns, mMapAuths and mMapName collections
	 * @param r		The reader object containing the patterns to be loaded
	 * @throws IOException	Throws an exception if (?)
	 */
	private void load(Reader r) throws IOException {

		BufferedReader b = new BufferedReader(r);
		String line = b.readLine();
		int lineNo = 1;
		Pattern p;

		while (line != null) {
			System.out.println(line);

			//For each input line, interpret as a pattern
			try {
				p = new Pattern(line);

				mPatterns.add(p);

				//Get the current list of patterns belonging to the author, else create a new one
				List<Pattern> pList = (mMapAuths.containsKey(p.getAuthor())) ? 
						mMapAuths.remove(p.getAuthor()) : new LinkedList<Pattern>();

				pList.add(p);
				mMapAuths.put(p.getAuthor(), pList);

				mMapName.put(p.getName(), p);

			} catch (PatternFormatException e) {
				System.out.println("Error reading line "+lineNo+": "+e.getMessage());
			}

			line = b.readLine();
			lineNo++;
		}
	}

	private void loadFromURL(String url) throws IOException {
		URL destination = new URL(url);
		URLConnection connection = destination.openConnection();

		Reader r = new java.io.InputStreamReader(connection.getInputStream());
		load(r);
	}

	private void loadFromDisk(String filepath) throws IOException {
		try {
			Reader r = new FileReader(filepath);
			load(r);

			//Convert FileNotFoundException to IOException to conform with constructor throws clause
		} catch (FileNotFoundException e) {
			throw new IOException("No file could be found at "+filepath);
		}
	}

	/**
	 * Returns a list of all of the patterns in the store, sorted by name
	 * @return	The name-sorted list
	 */
	public List<Pattern> getPatternsNameSorted() {
		List<Pattern> lstCopy = new LinkedList<Pattern>(mPatterns);
		Collections.sort(lstCopy);
		return lstCopy;
	}
	
	/**
	 * Returns a list of all of the patterns in the store, sorted by author -> name
	 * @return	The author-sorted list
	 */
	public List<Pattern> getPatternsAuthorSorted() {
		List<Pattern> lstCopy = new LinkedList<Pattern>(mPatterns);
		
		Collections.sort(lstCopy, new Comparator<Pattern>() {
			public int compare(Pattern p1, Pattern p2) {
				if (p1.getAuthor().compareTo(p2.getAuthor()) == 0) {
					return p1.compareTo(p2);
				} else {
					return p1.getAuthor().compareTo(p2.getAuthor());
				}
			}
		});
		
		return lstCopy;
	}

	/**
	 * Returns all of the patterns of a certain author, sorted by name
	 * @param author			The author name of the patterns to be listed
	 * @return					The name-sorted list of patterns belonging to the author
	 * @throws PatternNotFound	Throws an error if no patterns can be found
	 */
	public List<Pattern> getPatternsByAuthor(String author) throws PatternNotFound {
		if(!mMapAuths.containsKey(author)) throw new PatternNotFound("Could not find any patterns belonging to "+author);
		List<Pattern> p = new LinkedList<Pattern>(mMapAuths.get(author));
		Collections.sort(p);
		return p;
	}

	/**
	 * Returns the pattern with a given name
	 * @param name				The name of the pattern to be found
	 * @return					The pattern corresponding to the given name
	 * @throws PatternNotFound	Throws an error if the given pattern name is not found
	 */
	public Pattern getPatternByName(String name) throws PatternNotFound {
		if (!mMapName.containsKey(name)) throw new PatternNotFound("Could not find the pattern with name "+name);
		return mMapName.get(name);
	}

	/**
	 * Returns a list of all of the authors with patterns, sorted alphabetically
	 * @return	The sorted list of authors
	 */
	public List<String> getPatternAuthors() {
		List<String> authors = new LinkedList<>(mMapAuths.keySet());
		Collections.sort(authors);
		return authors;
	}

	/**
	 * Returns a list of all of the pattern names in the store, sorted alphabetically
	 * @return	The sorted list of pattern names
	 */
	public List<String> getPatternNames() {
		List<String> patternNames = new LinkedList<>(mMapName.keySet());
		Collections.sort(patternNames);
		return patternNames;
	}

	public static void main(String args[]) throws IOException {
		new PatternStore("http://www.cl.cam.ac.uk/teaching/current/OOProg/ticks/lifetest.txt");
		new PatternStore("C:\\Users\\Craig\\OneDrive - University Of Cambridge\\Code\\Java\\Object-Oriented Programming\\src\\uk\\ac\\cam\\cf443\\oop\\tick3\\patterns.txt");
	}
}