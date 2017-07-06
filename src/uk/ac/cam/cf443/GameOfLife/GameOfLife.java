package uk.ac.cam.cf443.GameOfLife;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * The core class of the Game of Life implementation, contains a main() method for exectution.
 * 
 * @author cf443
 */
public final class GameOfLife {
	private World mWorld;
	private PatternStore mStore;
	private ArrayList<World> mCachedWorlds = new ArrayList<>();

	public GameOfLife (PatternStore ps) {mStore = ps;}

	/**
	 * Play the game of life from the current state using the console
	 * @throws java.io.IOException	Unexpected input from System.in.readLine()
	 */
	public void play() throws java.io.IOException {

		String response="";
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Please select a pattern to play (l to list)");
		while (!response.equals("q")) {
			response = in.readLine();
			System.out.println(response);

			//Go forward one generation
			if (response.equals("f")) {
				if (mWorld==null) System.out.println("Please select a pattern to play (l to list):");
				else {

					//If cache size is insufficient, generate next world and add to the cache
					if (mWorld.getGenerationCount()+1 >= mCachedWorlds.size()) { //NB: GenCount is initially 0
						mCachedWorlds.add(mWorld);
						mWorld = this.copyWorld(true);
						mWorld.nextGeneration();

					//Else get the world state from the cache
					} else {
						mWorld = mCachedWorlds.get(mWorld.getGenerationCount()+1);
					}

					print();
				}
			}
			
			//Go back one generation
			else if (response.equals("b")) {
				if (mWorld == null) System.out.println("Please select a pattern to play (l to list)");
				
				if (mWorld.getGenerationCount() != 0) {
					//If current world is not in the cache, add it
					if (!mCachedWorlds.contains(mWorld)) mCachedWorlds.add(mWorld);
					
					mWorld = mCachedWorlds.get(mWorld.getGenerationCount() - 1);
				}
				print();
			}
			
			//List all the available patterns
			else if (response.equals("l")) {
				List<Pattern> names = mStore.getPatternsNameSorted();
				int i=0;
				for (Pattern p : names) {
					System.out.println(i+" "+p.getName()+"  ("+p.getAuthor()+")");
					i++;
				}
			}

			//Load the pattern with the given number
			else if (response.startsWith("p")) {
				Pattern p = null;

				try {
					int patternNo = Integer.parseInt(response.substring(2));
					if (patternNo < 0) throw new NumberFormatException();

					p = mStore.getPatternsNameSorted().get(patternNo);

					if (p.getHeight()*p.getWidth() > 64) {
						mWorld = new ArrayWorld(p.getSerial());
					} else {
						mWorld = new PackedWorld(p.getSerial());
					}

				} catch (NumberFormatException nfe) {
					throw new IOException("Could not interpret "+response+" as a pattern number");
				} catch (IndexOutOfBoundsException iobe) {
					throw new IOException("No pattern found with number "+response);
				} catch (PatternFormatException e) {
					throw new IOException("Invalid pattern within the pattern store: "+p.getSerial());
				} 

				print();
			}

		}
	}

	/**
	 * Prints a simple console output of the current world state
	 */
	public void print() { 
		System.out.println("- "+mWorld.getGenerationCount()); 
		for (int row = 0; row < mWorld.getPattern().getHeight(); row++) { 
			for (int col = 0; col < mWorld.getPattern().getWidth(); col++) {
				System.out.print(mWorld.getCell(col, row) ? "#" : "_"); 
			}
			System.out.println(); 
		} 
	}

	private World copyWorld(boolean useCloning) {
		if(!useCloning) {
			if (mWorld instanceof ArrayWorld) {
				return new ArrayWorld((ArrayWorld) mWorld);
			} else if (mWorld instanceof PackedWorld) {
				return new PackedWorld((PackedWorld) mWorld);
			} else {
				throw new RuntimeException("Unsupported world type detected when attempting copyWorld()");
			}
		} else {
			try {
				return (World) mWorld.clone();
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException("Unsupported world type detected when attempting copyWorld()");
			}
		}
	}


	public static void main(String args[]) throws IOException {

		if (args.length!=1) {
			System.out.println("Usage: java GameOfLife <path/url to store>");
			return;
		}

		try {
			PatternStore ps = new PatternStore(args[0]);
			GameOfLife gol = new GameOfLife(ps);    
			gol.play();
		}
		catch (IOException ioe) {
			System.out.println("Failed to load pattern store");
		}

	}
}
