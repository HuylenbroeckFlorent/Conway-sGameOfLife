import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.swing.*;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.GridLayout;

public class game_of_life{

	private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private static final int screenHeight = screenSize.height;
	private static final int screenWidth = screenSize.width;

	private static JFrame frame;
	private static JMenuBar menuBar;
	private static JPanel mainPanel;
	private static CellPlane cellPlane;
	private static final MouseListener ml = new MouseAdapter(){
		@Override
		public void mouseClicked(MouseEvent e){
			int x=(int)(e.getX()/size);
			int y=(int)(e.getY()/size);
			cells[x][y]=(cells[x][y]+1)%2;
			cellPlane.revalidate();
			cellPlane.repaint();

			nClicks++;
			if(nClicks==10){
				evolver = new Evolver();
				evolver.start();
			}
		}
	};

	private static int tickMillis = 50;
	private static int nX=50;
	private static int nY=50;
	private static int size=10;

	private static int[][] cells = new int[nX][nY];
	private static final int ALIVE = 1;
	private static final int DEAD = 0;

	private static int nClicks = 0;
	private static Evolver evolver;

	public static void main(String[] args){

		frame = new JFrame("Conway's game of life");
		frame.setPreferredSize(new Dimension(nX*size, nY*size));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		cellPlane=new CellPlane();
		cellPlane.addMouseListener(ml);

		frame.setContentPane(cellPlane);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}

	private static void update(){
		
		int[][] next = new int[nX][nY];
		for(int i=0; i<nX; i++){
			next[i]=cells[i].clone();
		}

		for(int i=0; i<nX; i++){
			for(int j=0; j<nY; j++){
				int sum = 0;

				if(i-1>=0)
					sum+=cells[i-1][j];
				if(i-1>=0 && j-1>=0)
					sum+=cells[i-1][j-1];
				if(j-1>=0)
					sum+=cells[i][j-1];
				if(i+1<nX && j-1>=0)
					sum+=cells[i+1][j-1];
				if(i+1<nX)
					sum+=cells[i+1][j];
				if(i+1<nX && j+1<nY)
					sum+=cells[i+1][j+1];
				if(j+1<nY)
					sum+=cells[i][j+1];
				if(i-1>=0 && j+1<nY)
					sum+=cells[i-1][j+1];

				int state = cells[i][j];

				if(state==DEAD && sum==3)
					next[i][j]=ALIVE;
				else if(state==ALIVE && sum==2)
					next[i][j]=ALIVE;
				else if(state==ALIVE && sum==3)
					next[i][j]=ALIVE;
				else
					next[i][j]=0;
			}
		}

		cells=next;

		cellPlane.removeAll();
		cellPlane.revalidate();
		cellPlane.repaint();
	}

	static class Evolver extends Thread{
		public void run(){
			while(!Thread.currentThread().isInterrupted()){
				try{
					update();
					TimeUnit.MILLISECONDS.sleep(tickMillis);
				}catch(InterruptedException ie){
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	static class CellPlane extends JPanel{

		public CellPlane(){
			super();
			//this.setPreferredSize(new Dimension(nX*size, nY*size));
		}

		public void paintComponent(Graphics g){
			super.paintComponent(g);

			for(int i=0; i<nX; i++){
				for(int j=0; j<nY; j++){
					
					if(cells[i][j]==ALIVE)
						g.setColor(Color.BLACK);
					else
						g.setColor(Color.WHITE);
					g.fillRect(i*size, j*size, size, size);
				}
			}
		}
	}
}