package uk.cam.ac.cf443.GameOfLife;

import java.awt.Color;
import javax.swing.JPanel;

public class GamePanel extends JPanel {
    
	private static final long serialVersionUID = 1493712047298731915L;
	private World mWorld = null;

    @Override
    protected void paintComponent(java.awt.Graphics g) {
        // Paint the background white
        g.setColor(java.awt.Color.WHITE);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
       
        
        //Draw the world
        if (mWorld != null) {
        	
            int xs = this.getWidth()/mWorld.getWidth();
            int ys = this.getHeight()/mWorld.getHeight();
            int size = (xs < ys) ? xs : ys;
        
        	for (int col = 0; col < mWorld.getWidth(); col++) {
        		for (int row = mWorld.getHeight()-1; row >= 0; row--) {
        			//Border
        			g.setColor(Color.GRAY);
        			g.drawRect(col*(size+1),row*(size+1),size,size);
        			
        			//Cell
        			g.setColor((mWorld.getCell(col, row)) ? Color.BLACK : Color.WHITE);
        			g.fillRect(col*(size+1),row*(size+1),size,size);
        		}
        	}
        	
        	g.setColor(Color.BLACK);
        	g.drawString("Generation: "+mWorld.getGenerationCount(), 20, this.getHeight()-20);
        }
    }

    public void display(World w) {
        mWorld = w;
        repaint();
    }
}