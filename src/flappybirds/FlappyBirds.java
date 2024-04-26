package flappybirds;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

import pkg2dgamesframework.AFrameOnImage;
import pkg2dgamesframework.Animation;
import pkg2dgamesframework.GameScreen;

public class FlappyBirds extends GameScreen {

	private BufferedImage birds;
	private Animation bird_anim;
	 
	
	private Bird bird;
	private Ground ground;
	private ChimneyGroup chimneyGroup;
	
	private int SCORE = 0;
	private String HIGH_SCORE = "";
	
	private int BEGIN_SCREEN = 0;
	private int GAMEPLAY_SCREEN = 1;
	private int GAMEOVER_SCREEN = 2;
	
	private int CURRENT_SCREEN = BEGIN_SCREEN;
	
	public static float g = 0.15f;
	
	public FlappyBirds() {
		super(800, 600);
		
		try {
			birds = ImageIO.read(new File("../Assets/bird_sprite.png"));
			
		} catch (IOException e) {
			
		}
		
		bird_anim = new Animation(100);
		AFrameOnImage f;
		f = new AFrameOnImage(0, 0, 60, 60);
		bird_anim.AddFrame(f);
		f = new AFrameOnImage(60, 0, 60, 60);
		bird_anim.AddFrame(f);		
		f = new AFrameOnImage(120, 0, 60, 60);
		bird_anim.AddFrame(f);
		f = new AFrameOnImage(60, 0, 60, 60);
		bird_anim.AddFrame(f);
		
		bird = new Bird(350, 250, 45, 45);
		ground = new Ground();
		chimneyGroup = new ChimneyGroup();
		
		BeginGame();
	}
	
	public static void main(String[] args) {
		new FlappyBirds();
	}
	
	public void resetGame() {
		bird.setPos(350, 250);
		bird.setV(0);
		bird.setLive(true);
		
		SCORE = 0;
		
		chimneyGroup.resetChimneys();
	}
	
	
	public void updateHighScore() {
		int TEMP = -1;
		if(this.getHighScore() != null) TEMP = Integer.parseInt(this.getHighScore());
		if(SCORE > TEMP) HIGH_SCORE = "" + SCORE;
		
		
		File scoreFile = new File("../Assets/high_score.txt");
		if(!scoreFile.exists())
		{
			try {
				scoreFile.createNewFile();
			} catch(IOException e) {
			}
		}
		
		FileWriter fw = null;
		BufferedWriter bw = null;	
		try {
			fw = new FileWriter(scoreFile);
			bw = new BufferedWriter(fw);	
			bw.write(this.HIGH_SCORE);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getHighScore() {
		BufferedReader br = null;
		try {
			FileReader fr = new FileReader("../Assets/high_score.txt");
			br = new BufferedReader(fr);
			return br.readLine();
		} catch (IOException e) {
			return "0";
		} finally {
			try {
				if(br != null) br.close();
			} catch (IOException e) {}
		}
	}
	
	@Override
	public void GAME_UPDATE(long deltaTime) {
		
		if(CURRENT_SCREEN == BEGIN_SCREEN) {
			resetGame();
		}else if(CURRENT_SCREEN == GAMEPLAY_SCREEN) {
			
			if(bird.getLive()) bird_anim.Update_Me(deltaTime);
			bird.update(deltaTime);
			ground.update();
			chimneyGroup.update();
			
			for(int i = 0; i < ChimneyGroup.SIZE; i++) {
				if(bird.getRect().intersects(chimneyGroup.getChimney(i).getRect())) {
					bird.setLive(false);
				}		
			}
			
			
			for(int i = 0; i < ChimneyGroup.SIZE; i++) {
				if(bird.getPosX() > chimneyGroup.getChimney(i).getPosX() && !chimneyGroup.getChimney(i).getIsBehindBird() && i%2==0) {
					SCORE++;
					bird.getScoreSound.play();
					chimneyGroup.getChimney(i).setIsBehindBird(true);
				}
			}
			
			if(bird.getPosY() + bird.getH() > ground.getYGround() || bird.getPosY() + bird.getH() <= 0) {
				CURRENT_SCREEN = GAMEOVER_SCREEN; 
				HIGH_SCORE = this.getHighScore();
				updateHighScore();
			}
			
		}else {
			
		}
	}

	@Override
	public void GAME_PAINT(Graphics2D g2) {
		
		g2.setColor(Color.decode("#b8daef"));
		g2.fillRect(0, 0, MASTER_WIDTH, MASTER_HEIGHT);
		
		chimneyGroup.paint(g2);
		
		ground.paint(g2);
		
		
		if(bird.getIsFlying()) bird_anim.PaintAnims((int) bird.getPosX(), (int) bird.getPosY(), birds, g2, 0, -1);
		else bird_anim.PaintAnims((int) bird.getPosX(), (int) bird.getPosY(), birds, g2, 0, 0);
		
		if(CURRENT_SCREEN == BEGIN_SCREEN) {
			g2.setColor(Color.white);
			g2.drawString("Press space to play game!", 200, 300);
		}else if(CURRENT_SCREEN == GAMEOVER_SCREEN) {
			g2.setColor(Color.white);
			g2.drawString("Press space to turn back begin screen!", 200, 300);
			g2.drawString("Score:" + SCORE, 200, 200);
			g2.setColor(Color.white);
			if(this.getHighScore() != null) g2.drawString("High score:" + HIGH_SCORE, 200, 150);
			else g2.drawString("High score:" + "0", 200, 150);
		}
		
		g2.setColor(Color.white);
		g2.drawString("Score:" + SCORE, 20, 80);
	}

	@Override
	public void KEY_ACTION(KeyEvent e, int Event) {
		if(Event == KEY_PRESSED) {
			if(CURRENT_SCREEN == BEGIN_SCREEN) {
				CURRENT_SCREEN = GAMEPLAY_SCREEN;
			}else if(CURRENT_SCREEN == GAMEPLAY_SCREEN) {
				if(bird.getLive()) bird.fly();
			}else {
				CURRENT_SCREEN = BEGIN_SCREEN;
			}
			

		}
		
	} 
	
}
