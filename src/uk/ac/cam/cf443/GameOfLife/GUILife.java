package uk.ac.cam.cf443.GameOfLife;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GUILife extends JFrame implements ListSelectionListener {

	private static final long serialVersionUID = -5796556733512204554L;
	private World mWorld; 										//The currently displayed world
	private PatternStore mStore; 								//The set of patterns available to the user
	private ArrayList<World> mCachedWorlds = new ArrayList<>(); //Cache of previously generated worlds to improve performance
	private boolean mPlaying = false; 							//True if the world is currently evolving
	private Timer mTimer;										//The timer for the world evolution
	private final int mFrameRate = 500;							//The duration of a generation in milliseconds
	
	//GUI Elements
	private JButton mPlayButton;	//The 'Play' button within the controls panel
	private JPanel mGamePanel;		//The panel containing the game
	

	/**
	 * Constructor which takes the set of patterns to be used as an input PatternStore
	 * @param ps	The set of patterns available to the user
	 */
	public GUILife(PatternStore ps) {
		super("Game of Life");
	    mStore=ps;
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    setSize(1024,768);
        
	    mGamePanel = createGamePanel();
	    add(createPatternsPanel(),BorderLayout.WEST);
	    add(createControlPanel(),BorderLayout.SOUTH);
	    add(mGamePanel,BorderLayout.CENTER);
	    
    }
	
	//----------------//
	// GUI GENERATION //
	//----------------//
	/**
	 * Add a titled border around a given JComponent
	 * @param component		The component to be bordered
	 * @param title			The text within the border
	 */
	private void addBorder(JComponent component, String title) {
	    Border etch = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
	    Border tb = BorderFactory.createTitledBorder(etch,title);
	    component.setBorder(tb);
	}

	/**
	 * Create a JPanel to contain the game of life
	 * @return	The GamePanel
	 */
	private JPanel createGamePanel() {
	    GamePanel mGamePanel = new GamePanel();
	    addBorder(mGamePanel,"Game Panel");
	    return mGamePanel;
	}

	/**
	 * Create a JPanel to contain the patterns to be used
	 * @return	The PatternsPanel
	 */
	private JPanel createPatternsPanel() {
	    JPanel patt = new JPanel();
	    addBorder(patt,"Patterns");
	    
		JList<Object> list = new JList<>(mStore.getPatternsNameSorted().toArray());
		list.addListSelectionListener(this);
	    JScrollPane scroll = new JScrollPane(list);
	    scroll.setViewportView(list);
	    
	    patt.setLayout(new BorderLayout());
	    patt.add(scroll,BorderLayout.CENTER);
	    return patt; 
	}

	/**
	 * Create a JPanel to contain the user controls (Back, Play/Pause, Forward)
	 * @return	The ControlPanel
	 */
	private JPanel createControlPanel() {
	    JPanel ctrl =  new JPanel();
	    addBorder(ctrl,"Controls");
	    ctrl.setLayout(new GridLayout(1,3));
	    
	    //Button to devolve once & pause playback
	    JButton back = new JButton("< Back");
	    back.addActionListener(e -> {if(mPlaying)runOrPause();moveBack();});
	    ctrl.add(back,BorderLayout.WEST);
	    
	    //Button to pause/play dynamically
	    mPlayButton = new JButton("Play");
	    mPlayButton.addActionListener(e -> runOrPause());
	    ctrl.add(mPlayButton,BorderLayout.CENTER);
	    
	    //Button to evolve once & pause playback
	    JButton fwrd = new JButton("Forward >");
	    fwrd.addActionListener(e -> {if(mPlaying)runOrPause();moveForward();});
	    ctrl.add(fwrd,BorderLayout.EAST);
	    
	    return ctrl;
	}
	
	//------------------------//
	// GAME OF LIFE BEHAVIOUR //
	//------------------------//
	
	/**
	 * Evolve the world state by one generation
	 */
	private void moveForward() {
		if (mWorld != null) {

			//If cache size is insufficient, generate next world and add to the cache
			if (mWorld.getGenerationCount()+1 >= mCachedWorlds.size()) { //NB: GenCount is initially 0
				mCachedWorlds.add(mWorld);
				mWorld = this.copyWorld(true);
				mWorld.nextGeneration();
				((GamePanel) mGamePanel).display(mWorld);

			//Else get the world state from the cache
			} else {
				mWorld = mCachedWorlds.get(mWorld.getGenerationCount()+1);
				((GamePanel) mGamePanel).display(mWorld);
			}

		}
	}
	
	/**
	 * Devolve the world state by one generation
	 */
	private void moveBack() {
		if (mWorld != null) {
	
			if (mWorld.getGenerationCount() != 0) {
				//If current world is not in the cache, add it
				if (!mCachedWorlds.contains(mWorld)) mCachedWorlds.add(mWorld);
				
				//Update the world
				mWorld = mCachedWorlds.get(mWorld.getGenerationCount() - 1);
				((GamePanel) mGamePanel).display(mWorld);
			}
		}
	}
	
	/**
	 * A method to pause/play the game when the user presses the pause/play
	 * button in the control panel
	 */
	private void runOrPause() {
	    if (mPlaying) {
	        mTimer.cancel();
	        mPlaying=false;
	        mPlayButton.setText("Play");
	    }
	    else {
	        mPlaying=true;
	        mPlayButton.setText("Stop");
	        mTimer = new Timer(true);
	        mTimer.scheduleAtFixedRate(new TimerTask() {
	            @Override
	            public void run() {
	                moveForward();
	            }
	        }, 0, mFrameRate);
	    }
	}
	
	/**
	 * A method to change the game state when the user selects a pattern from the list
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		
		@SuppressWarnings("unchecked")
		JList<Pattern> list = (JList<Pattern>) e.getSource();
		Pattern p = list.getSelectedValue();
		
		if(mPlaying) runOrPause(); //Pause the evolution

		try {

			if (p.getHeight()*p.getWidth() > 64) {
				mWorld = new ArrayWorld(p.getSerial());
			} else {
				mWorld = new PackedWorld(p.getSerial());
			} 

		} catch (PatternFormatException pfe) {
			throw new RuntimeException("Invalid pattern within the pattern store: "+p.getSerial());
		} 
		
		mCachedWorlds = new ArrayList<>();
		
		((GamePanel) mGamePanel).display(mWorld);	
	}
	
	/**
	 * A method which returns a deep copy of 'this'
	 * @param useCloning	If true, cloning will be used; else copy constructors will be used
	 * @return				A deep copy of 'this'
	 */
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

	/**
	 * Create an instance of GUILife using patterns from cl.cam.ac.uk
	 * @param ignored	Not used
	 */
	public static void main(String... ignored) {
    	PatternStore ps;
		try {
			ps = new PatternStore("http://www.cl.cam.ac.uk/teaching/1617/OOProg/ticks/life.txt");
			GUILife gui = new GUILife(ps);
	        gui.setVisible(true);
	        
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    


}