package flappybirds;

import java.awt.Rectangle;
import java.io.File;

import pkg2dgamesframework.Objects;
import pkg2dgamesframework.SoundPlayer;

public class Bird extends Objects {
	
	private float v = 0;
	
	private boolean isFlying = false;
	private boolean isLive = true;
	
	private Rectangle rect;	
	
	public SoundPlayer flapSound, collideSound, getScoreSound;
	
	public Bird(int x, int y, int w, int h) {
		super(x, y, w, h);
		
		rect = new Rectangle(x, y, w, h);
		
		flapSound = new SoundPlayer(new File("src/Assets/fap.wav"));
		collideSound = new SoundPlayer(new File("src/Assets/fall.wav"));
		getScoreSound = new SoundPlayer(new File("src/Assets/getpoint.wav"));
		
	}
	
	public Rectangle getRect() {
		return rect;
	}
	
	public void update(long deltatime) {
		
		v += FlappyBirds.g;
		
		this.setPosY(this.getPosY() + v);
		this.rect.setLocation((int) this.getPosX(), (int) this.getPosY());
		
		if(v<0) isFlying = true;
		else isFlying = false;
		
	}
	
	public void fly() {
		v = -4;
		flapSound.play();
	}
	
	public boolean getIsFlying() {
		return isFlying;
	}
	
	public void setLive(boolean b) {	
		if(isLive == true && b == false) collideSound.play();
		isLive = b;
	}
	
	public boolean getLive() {
		return isLive;
	}
	
	public void setV(float v) {
		this.v = v;	
	}
	
}