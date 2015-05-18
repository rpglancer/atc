package atc;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import atc.lib.Aircraft;
import atc.lib.Airport;
import atc.lib.Entity;
import atc.lib.Handler;
import atc.lib.Hud;
import atc.lib.MouseInput;
import atc.type.TYPE;

public class Game extends Canvas implements Runnable{
	public static final int sweepLength = 5;
	private static final long serialVersionUID = 6797603345816214805L;
	public static final int WIDTH = 800;
	public static final int HEIGHT = 480;
	public static final int GAMEWIDTH = WIDTH - 192;
	public static final int HUDWIDTH = 192;
	public static final int HUDHEIGHT = 256;
	public static final int INFOHEIGHT = 480 - HUDHEIGHT;
	private boolean running = false;
	private BufferedImage image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
	private static Handler handler = new Handler();
	private Thread thread;
	
	public static void main(String[] args){
		Game game = new Game();
		game.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		game.setMaximumSize(new Dimension(WIDTH, HEIGHT));
		game.setMinimumSize(new Dimension(HEIGHT, WIDTH));
		game.setFocusable(true);
		game.requestFocus();
			
		JFrame frame = new JFrame("ATC");
		
			
		frame.add(game);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		game.start();
	}

	private synchronized void start(){
		if(running){
			return;
		}
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	private synchronized void stop(){
		if(!running){
			return;
		}
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.exit(1);
	}
	
	private void init(){
		this.addMouseListener(new MouseInput(this, handler));
		this.addMouseMotionListener(new MouseInput(this, handler));
		Airport airport = new Airport();
		handler.add(airport);
		Aircraft aircraft = new Aircraft(256, 64, 180, 140, TYPE.AIRCRAFT_ARRIVE);
		Hud hud = new Hud();
		handler.add(aircraft);
		handler.add(hud);
	}
	
	private void render(){
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null){
			createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();	
		g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
		handler.render(g);
		g.dispose();
		bs.show();
	}
	
	private void tick(){
		handler.tick();
	}
	
	@Override
	public void run() {
		init();
		long lastTime = System.nanoTime();
//		final double amountOfTicks = 30.0;			//	Keep @ 30 for now, no need to run 60fps, may drop lower.
		final double amountOfTicks = 1.0/sweepLength;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		
		while(running){
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			if(delta >= 1){
				tick();
//				render();		// Tick Limited FPS
				delta--;
			}
			if(System.currentTimeMillis() - timer > 1000){
				timer += 1000;
			}
			render();
		}
		stop();	
	}
	
	public static void registerWithHandler(Entity entity){
		handler.add(entity);
	}
	
}
