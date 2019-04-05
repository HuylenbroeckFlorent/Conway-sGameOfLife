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
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.KeyListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;

public class game_of_life{

	private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private static final int MAX_SCREEN_HEIGHT = 3*screenSize.height/4;
	private static final int MAX_SCREEN_WIDTH = 3*screenSize.width/4;

	private static JFrame frame;
	private static JMenuBar menuBar;
	private static JPanel mainPanel;
	private static CellPlane cellPlane;

	private static final MouseListener ml = new MouseAdapter(){
		@Override
		public void mouseClicked(MouseEvent e){
			int x=(int)(e.getX()/size);
			int y=(int)(e.getY()/size);
			int state = cells[x][y]=(cells[x][y]+1)%2;
			if(!modif.contains(new Point(x, y)))
				modif.add(new Point(x, y));
			cellPlane.fastUpdate();
		}
	};
	private static final MouseMotionListener mml = new MouseMotionAdapter() {
		@Override
		public void mouseDragged(MouseEvent e){
			int x=(int)(e.getX()/size);
			int y=(int)(e.getY()/size);

			if(x>=0 && x<nX*size && y>=0 && y<nY*size && cells[x][y]==DEAD){
				cells[x][y]=ALIVE;
				cellPlane.fastUpdate(x, y);
			}
		}
	};
	private static final KeyListener kl = new KeyAdapter(){
		@Override
		public void keyReleased(KeyEvent e){
			if(e.getKeyCode() == KeyEvent.VK_SPACE){
				if(evolver!=null && evolver.isAlive())
					stop();
				else
					start();
			}
		}
	};

	private static final int LONG_TICK=250;
	private static final int REGULAR_TICK=100;
	private static final int SHORT_TICK=25;
	private static final int NO_TICK=1;
	private static int tick = REGULAR_TICK;

	private static final int SMALL_SIZE=15;
	private static final int MEDIUM_SIZE=10;
	private static final int LARGE_SIZE=5;
	private static final int MAX_SIZE=3;
	private static int size=MEDIUM_SIZE;
	private static int nX=(int)(MAX_SCREEN_WIDTH/MEDIUM_SIZE);
	private static int nY=(int)(MAX_SCREEN_HEIGHT/MEDIUM_SIZE);

	private static int[][] cells = new int[nX][nY];
	private static final int ALIVE = 1;
	private static final int DEAD = 0;
	private static ArrayList<Point> modif = new ArrayList<Point>();

	private static Evolver evolver;

	public static void main(String[] args){

		frame = new JFrame("Conway's game of life");

		menuBar = new JMenuBar();

		JMenu actions = new JMenu("Actions");

		JMenuItem clear = new JMenuItem("Clear");
		clear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				updateFrame();
			}
		});

		actions.add(clear);

		menuBar.add(actions);

		JMenu settingsMenu = new JMenu("Settings");

		JMenu sizeMenu = new JMenu ("Cell size");
		ButtonGroup sizes = new ButtonGroup();
		JRadioButtonMenuItem smallSize = new JRadioButtonMenuItem("Large");
		smallSize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				size=SMALL_SIZE;
				updateFrame();
			}
		});
		JRadioButtonMenuItem mediumSize = new JRadioButtonMenuItem("Medium");
		mediumSize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				size=MEDIUM_SIZE;
				updateFrame();
			}
		});
		JRadioButtonMenuItem largeSize = new JRadioButtonMenuItem("Small");
		largeSize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				size=LARGE_SIZE;
				updateFrame();
			}
		});
		JRadioButtonMenuItem maxSize = new JRadioButtonMenuItem("Tiny");
		maxSize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				size=MAX_SIZE;
				updateFrame();
			}
		});
		sizes.add(smallSize);
		sizes.add(mediumSize);
		sizes.add(largeSize);
		sizes.add(maxSize);
		sizeMenu.add(smallSize);
		sizeMenu.add(mediumSize);
		sizeMenu.add(largeSize);
		sizeMenu.add(maxSize);
		mediumSize.setSelected(true);
		settingsMenu.add(sizeMenu);
		menuBar.add(settingsMenu);

		JMenu tickMenu = new JMenu ("Speed");
		ButtonGroup ticks = new ButtonGroup();
		JRadioButtonMenuItem longTick = new JRadioButtonMenuItem("Slow");
		longTick.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				tick=LONG_TICK;
			}
		});
		JRadioButtonMenuItem regularTick = new JRadioButtonMenuItem("Normal");
		regularTick.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				tick=REGULAR_TICK;
			}
		});
		JRadioButtonMenuItem shortTick = new JRadioButtonMenuItem("Fast");
		shortTick.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				tick=SHORT_TICK;
			}
		});
		JRadioButtonMenuItem noTick = new JRadioButtonMenuItem("Max");
		noTick.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				tick=NO_TICK;
			}
		});
		ticks.add(longTick);
		ticks.add(regularTick);
		ticks.add(shortTick);
		ticks.add(noTick);
		tickMenu.add(longTick);
		tickMenu.add(regularTick);
		tickMenu.add(shortTick);
		tickMenu.add(noTick);
		regularTick.setSelected(true);
		settingsMenu.add(tickMenu);
		menuBar.add(settingsMenu);

		frame.setJMenuBar(menuBar);

		frame.addKeyListener(kl);

		cellPlane=new CellPlane();
		cellPlane.addMouseListener(ml);
		cellPlane.addMouseMotionListener(mml);

		frame.setPreferredSize(new Dimension(MAX_SCREEN_WIDTH, MAX_SCREEN_HEIGHT));
		frame.setSize(MAX_SCREEN_WIDTH, MAX_SCREEN_HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setContentPane(cellPlane);
		updateFrame();
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}

	private static void start(){
		evolver = new Evolver();
		evolver.start();
	}

	private static void update(){
		
		int[][] next = new int[nX][nY];
		for(int i=0; i<nX; i++){
			next[i]=cells[i].clone();
		}

		modif.clear();

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

				if(state==DEAD && sum==3){
					next[i][j]=ALIVE;
					modif.add(new Point(i, j));
				}
				else if(state==ALIVE && sum==2){
					next[i][j]=ALIVE;
					modif.add(new Point(i, j));
				}
				else if(state==ALIVE && sum==3){
					next[i][j]=ALIVE;
					modif.add(new Point(i, j));
				}
				else if(state==ALIVE){
					next[i][j]=DEAD;
					modif.add(new Point(i, j));
				}
				else
					next[i][j]=DEAD;
			}
		}

		cells=next;
		cellPlane.fastUpdate();
	}

	private static void stop(){
		evolver.interrupt();
	}

	static class Evolver extends Thread{
		public void run(){
			while(!Thread.currentThread().isInterrupted()){
				try{
					update();
					TimeUnit.MILLISECONDS.sleep(tick);
				}catch(InterruptedException ie){
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	static class CellPlane extends JPanel{

		public CellPlane(){
			super();
			this.setBackground(Color.WHITE);
		}

		public void paint(Graphics g){
			Rectangle r = g.getClipBounds();
			if(r.getWidth()<=size && r.getHeight()<=size){
				g.setColor(Color.BLACK);

				if(cells[(int)(r.getX()/size)][(int)(r.getY()/size)]==ALIVE)
					((Graphics2D)g).fill(r);
				else
					g.clearRect((int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight());
			}
		}

		public void fastUpdate(){
			for(int i=0; i<modif.size(); i++){
				this.paintImmediately((int)modif.get(i).getX()*size, (int)modif.get(i).getY()*size, size, size);
			}
		}

		public void fastUpdate(int x, int y){
			this.paintImmediately(x*size, y*size, size, size);
		}
	}

	static void updateFrame(){
		if(evolver!=null && evolver.isAlive())
			stop();
		modif.clear();
		cellPlane.removeAll();
		cellPlane.repaint();
		frame.pack();
		nX=(int)(cellPlane.getWidth()/size);
		nY=(int)(cellPlane.getHeight()/size);
		cells=new int[nX][nY];
	}
}