package uk.cam.ac.cf443.GameOfLife;

public class Pattern implements Comparable<Pattern> {

	private String mName;
	private String mAuthor;
	private int mWidth;
	private int mHeight;
	private int mStartCol;
	private int mStartRow;
	private String mCells;
	
	public String getName()   {return mName;}
	public String getAuthor() {return mAuthor;}
	public int getWidth()     {return mWidth;}
	public int getHeight()    {return mHeight;}
	public int getStartCol()  {return mStartCol;}
	public int getStartRow()  {return mStartRow;}
	public String getCells()  {return mCells;}
	public String getSerial() {
		return mName + 
			":" + mAuthor + 
			":" + mWidth + 
			":" + mHeight + 
			":" + mStartCol + 
			":" + mStartRow + 
			":" + mCells;
	}
	
	public Pattern(String format) throws PatternFormatException {
		String[] args = format.split(":");
		
		if (args.length == 0) throw new PatternFormatException("Please specify a pattern.");
		if (args.length != 7) throw new PatternFormatException("Invalid pattern format: Incorrect number of fields in pattern (found "+args.length+").");
		
		mName = args[0];
		mAuthor = args[1];
		
		try {mWidth = Integer.parseInt(args[2]);} 
		catch (NumberFormatException e) {
			throw new PatternFormatException("Invalid pattern format: Could not interpret the width field as a number ('"+args[2]+"' given).");}
		
		try {mHeight = Integer.parseInt(args[3]);} 
		catch (NumberFormatException e) {
			throw new PatternFormatException("Invalid pattern format: Could not interpret the height field as a number ('"+args[3]+"' given).");}
		
		try {mStartCol = Integer.parseInt(args[4]);} 
		catch (NumberFormatException e) {
			throw new PatternFormatException("Invalid pattern format: Could not interpret the startCol field as a number ('"+args[4]+"' given).");}

		try {mStartRow = Integer.parseInt(args[5]);} 
		catch (NumberFormatException e) {
			throw new PatternFormatException("Invalid pattern format: Could not interpret the startRow field as a number ('"+args[5]+"' given).");}
		
		for (int i = 0; i < args[6].length(); i++) {
			if (args[6].charAt(i) != '0' && args[6].charAt(i) != '1' && args[6].charAt(i) != ' ') {
				throw new PatternFormatException("Invalid pattern format: Malformed pattern '"+args[6]+"'.");
			}
		}
		mCells = args[6];
	}
	
	public void initialise(World world) throws PatternFormatException {
		String[] rows = mCells.split(" ");
		for (int i = 0; i < rows.length; i++) {
			char[] row = rows[i].toCharArray();
			for (int j = 0; j < row.length; j++) {
				if (row[j] != '0' && row[j] != '1') {
					throw new PatternFormatException("Invalid initialisation character: "+row[j]);
				}
				world.setCell(mStartCol+j,mStartRow+i,(row[j]=='1'));
			}
		}
	}
	
	public static void main(String args[]) {
		Pattern p;
		try {
			p = new Pattern(args[0]);
			
			System.out.println(p.mAuthor);
			System.out.println(p.mCells);
			System.out.println(p.mHeight);
			System.out.println(p.mName);
			System.out.println(p.mStartCol);
			System.out.println(p.mStartRow);
			System.out.println(p.mWidth);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public int compareTo(Pattern arg0) {
		return getName().compareTo(arg0.getName());
	}
	
	@Override
	public String toString() {
		return mName + " ("+mAuthor+")";
	}
	
}
