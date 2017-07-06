package uk.cam.ac.cf443.GameOfLife;

public class PackedWorld extends World implements Cloneable {

	private long world;
	public PackedWorld(String serial) throws PatternFormatException {
		super(serial);
		
		if(getPattern().getHeight()*getPattern().getWidth() > 64) {
			throw new PatternFormatException("PackedWorld has a maximum size of 64 cells."
					+getPattern().getWidth()*getPattern().getHeight()+" specified.");
		}
		
		getPattern().initialise(this);
	}
	
	public PackedWorld(PackedWorld toCopy) { 
		super(toCopy);
		this.world = toCopy.getLong();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	protected void nextGenerationImpl() {
		long next = 0L;
		for (int row = 0; row < getPattern().getWidth(); row++) {
			for (int col = 0; col < getPattern().getHeight(); col++) {
				
				if (computeCell(col,row)) {
					next = next | (1L << row*getPattern().getWidth()+col);
				} else {
					next = next & ~(1L << row*getPattern().getWidth()+col);
				}
			}
		}
		world = next;
	}

	@Override
	public boolean getCell(int col, int row) {
		//If the arguments are outwith the world, return false
		if (row < 0 || row >= getPattern().getHeight()) return false;
		if (col < 0 || col >= getPattern().getWidth())  return false;
				
		return get(row*getPattern().getWidth()+col);
	}

	@Override
	public void setCell(int col, int row, boolean val) {
		//If the arguments are outwith the world, return without doing anything
		if (row < 0 || row >= getPattern().getHeight()) return;
		if (col < 0 || col >= getPattern().getWidth())  return;
		set(row*getPattern().getWidth()+col,val);
	}

	
	private boolean get(int position) {
		return ((world >>> position) & 1) == 1;
	}
	
	private void set(int position, boolean val) {
		if (val) {
			world = world | (1L << position);
		} else {
			world = world & ~(1L << position);
		}
	}
	
	protected long getLong() {return world;}
}
