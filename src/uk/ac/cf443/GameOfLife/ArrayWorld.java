package uk.cam.ac.cf443.GameOfLife;

public class ArrayWorld extends World implements Cloneable {
	
	private boolean[][] mWorld;
	private final boolean[] mDeadRow;
	
	public ArrayWorld(String serial) throws PatternFormatException {
		super(serial);

		mWorld = new boolean[getPattern().getHeight()][getPattern().getWidth()];
		mDeadRow = new boolean[getPattern().getWidth()];
		getPattern().initialise(this);
		
		//Set all empty rows to reference mDeadRow
		rowScan:
		for (int row = 0; row < mWorld.length; row++) {
			for (boolean cell : mWorld[row]) {
				if (cell) continue rowScan;
			}
			mWorld[row] = mDeadRow;
		}
	}
	
	public ArrayWorld(ArrayWorld toCopy) {
		super(toCopy);
		this.mDeadRow = toCopy.mDeadRow;
		
		//Deep copy of the array
		mWorld = new boolean[getPattern().getHeight()][getPattern().getWidth()];
		for (int row = 0; row < getPattern().getHeight(); row++) {
			
			//Set all empty rows to reference mDeadRow
			if (toCopy.getArray()[row] == mDeadRow) {
				mWorld[row] = mDeadRow;
			} else {
				//Else copy the columns iteratively
				for (int col = 0; col < getPattern().getWidth(); col++) {
					mWorld[row][col] = toCopy.getCell(col, row);
				}
			}
		}
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		ArrayWorld cloned = (ArrayWorld) super.clone();
		
		//Deep copy of the array
		boolean[][] arrCopy = new boolean[getPattern().getHeight()][getPattern().getWidth()];
		for (int row = 0; row < getPattern().getHeight(); row++) {
			for (int col = 0; col < getPattern().getWidth(); col++) {
				arrCopy[row][col] = this.getCell(col, row);
			}
		}
		
		cloned.mWorld = arrCopy;
		return cloned;
	}
	
	/**
	 * Generates the next generation of the world by computing the next
	 * value of each of the cells individually
	 */
	@Override
	protected void nextGenerationImpl() {
		boolean[][] next = new boolean[getPattern().getHeight()][getPattern().getWidth()];

		for (int row = 0; row < getPattern().getHeight(); row++) {
			for (int col = 0; col < getPattern().getWidth(); col++) {
				next[row][col] = computeCell(col,row);
			}
		}
		mWorld = next;
	}
	
	/**
	 * Gets the value of a cell within the world
	 * @param col	The column of the cell
	 * @param row	The row of the cell
	 * @return 	The state of the cell at [row,col]
	 */
	@Override
	public boolean getCell(int col, int row) {
		//If the arguments are outwith the world, return false
		if (row < 0 || row >= getPattern().getHeight()) return false;
		if (col < 0 || col >= getPattern().getWidth())  return false;

		return mWorld[row][col];
	}

	
	/**
	 * Sets the value of a cell in the world
	 * @param col		The column of the cell to be set
	 * @param row		The row of the cell to be set
	 * @param newval	The boolean value to be assigned
	 */
	@Override
	public void setCell(int col, int row, boolean val) {
		//If the arguments are outwith the world, return without doing anything
		if (row < 0 || row >= getPattern().getHeight()) return;
		if (col < 0 || col >= getPattern().getWidth())  return;
		
		mWorld[row][col] = val;
	}
	
	protected boolean[][] getArray() {return this.mWorld;}
	
}
