package atc;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import atc.lib.Airport;
import atc.lib.Entity;
import atc.lib.Handler;
import atc.lib.Hud;
import atc.lib.MouseInput;

public class Game extends Canvas implements Runnable{
	
	public static final int HEIGHT = 480;
	public static final int HUDHEIGHT = 256;
	public static final int HUDWIDTH = 192;
	public static final int INFOHEIGHT = HEIGHT - HUDHEIGHT;
	public static final int sectorSize = 60;
	public static final int sweepLength = 5;
	public static final int sweepsPerMin = 60/sweepLength;
	public static final int WIDTH = 800;
	public static final int GAMEWIDTH = WIDTH - 192;
	
	private boolean running = false;
	
	private static final int VER_MAJOR = 0;
	private static final int VER_MINOR = 8;
	private static final int VER_PATCH = 5;
	
	private static final long serialVersionUID = 6797603345816214805L;
	
	private static final String release = "a";
	
	private BufferedImage image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
	private static Handler handler = new Handler();
	private Thread thread;
	
	public static void finalizeWithHandler(Entity entity){
		handler.remove(entity);
	}
	
	public static void main(String[] args){
		Game game = new Game();
		game.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		game.setMaximumSize(new Dimension(WIDTH, HEIGHT));
		game.setMinimumSize(new Dimension(HEIGHT, WIDTH));
		game.setFocusable(true);
		game.requestFocus();
		JFrame frame = new JFrame("ATC v" + VER_MAJOR + "." + VER_MINOR + "." + VER_PATCH + release);			
		frame.add(game);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		game.start();
	}

	public static void registerWithHandler(Entity entity){
		handler.add(entity);
	}
	
	@Override
	public void run() {
		init();
		long lastTime = System.nanoTime();
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
//				delta--;
				delta = 0;
			}
			if(System.currentTimeMillis() - timer > 1000){
				timer += 1000;
			}
			render();			
		}
		stop();	
	}
	
	private void init(){
		MouseInput mi = new MouseInput(this,handler);
		this.addMouseListener(mi);
		this.addMouseMotionListener(mi);
		Airport airport = new Airport();
		handler.add(airport);
		Hud hud = new Hud();
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
	
	private void tick(){
		handler.tick();
	}
}
