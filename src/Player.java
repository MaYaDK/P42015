import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public class Player {
	public static int screenWidth = 1920, screenHeight = 1080-80;
	//Player variables
	public int playerWidth = 600, playerHeight = 800;
	public int xPlayer = screenWidth/2-playerHeight/2, yPlayer = screenHeight/2-playerWidth/2;
	//Line from player to goal
	public int playerPointX, playerPointY; //goalPointX = 250, goalPointY = 0;
	//
	private BufferedImage shipDisplay; //only for displaying the current ship.
				
	private BufferedImage shipLeft; //Player
	private BufferedImage shipRight; //Player
	private BufferedImage ship; //Player
	private BufferedImage shipDown; //Player
		
	boolean isLeft = false;
	boolean isRight = false;
	boolean isUp = false;
	boolean isDown = false;
		
	public Player(){
		try {	
	        ship = ImageIO.read(new File("src/ship.png")); //Player
	        shipLeft = ImageIO.read(new File("src/shipLeft.png")); //Player
	    	shipRight = ImageIO.read(new File("src/shipRight.png")); //Player
	    	shipDown = ImageIO.read(new File("src/shipDown.png")); //Player
		 } catch (IOException ex) {
	            // handle exception...
	     }
		shipDisplay = ship; // initialize the first picture to shipUp.
	}
	
	public void drawShip(Graphics g){
		g.drawImage(shipDisplay, xPlayer, yPlayer, playerWidth, playerHeight, null); //Player
	}
	public void checkPosition(){
		if(isLeft == true){
			shipDisplay = shipLeft;
		}
		if(isRight == true){
			shipDisplay = shipRight;
		}
		if(isUp == true){
			shipDisplay = ship;
		}
		if(isDown == true){
			shipDisplay = shipDown;
		}	
	}

	public static void main(String[] args) {

	}
}
