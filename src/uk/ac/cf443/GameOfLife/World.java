package uk.cam.ac.cf443.GameOfLife;

public abstract class World implements Cloneable {
	
	private int mGeneration;
	private Pattern mPattern;
	
	public World(String format) throws PatternFormatException {
		mPattern = new Pattern(format);
		mGeneration = 0;
	}
	
	public World(World toCopy) {
		this.mPattern    = toCopy.getPattern();
		this.mGeneration = toCopy.getGenerationCount();
	}
	
	public int getWidth() {return mPattern.getWidth();}
	public int getHeight() {return mPattern.getHeight();}
	public int getGenerationCount() {return mGeneration;}
	protected Pattern getPattern() {return mPattern;}
	protected void incrementGenerationCount() {mGeneration++;}

	public void nextGeneration() {
		nextGenerationImpl();
		incrementGenerationCount();
	}
	
	protected abstract void nextGenerationImpl();
	public abstract boolean getCell(int col, int row);
	public abstract void setCell(int col, int row, boolean val);

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	protected int countNeighbours(int col, int row) {
		int count = 0;
		//Cycle through all cells
		for (int c = -1; c <= 1; c++) {
			for (int r = -1; r <= 1; r++) {
				//Exclude the current cell
				if (c == 0 && r == 0) continue;
				if (getCell(col+c,row+r)) count++;
			}
		}
		return count;
	}
	
	protected boolean computeCell(int col, int row) {
		//If arguments are outwith the world, return false
		if (row < 0 || row >= mPattern.getHeight()) return false;
		if (col < 0 || col >= mPattern.getWidth())  return false;

		//The number of living neighbours the current cell has
		int neighbours = countNeighbours(col,row);

		if (getCell(col,row))	return (neighbours == 2 || neighbours == 3) ? true : false;
		else 					return (neighbours == 3) ? true : false;

	}

}
