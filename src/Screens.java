import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class Screens {
	public static int screenWidth = 1920, screenHeight = 1080-80;
	//Game screen
	boolean isGameStarted = false;
	//final goal/delivery point
	boolean isGoalReach = false;
	public int i = 0;
	
	private BufferedImage intro1;
	private BufferedImage intro2;
	private BufferedImage intro3;
	private BufferedImage intro4;
	private BufferedImage intro5;
	private BufferedImage intro6;
	private BufferedImage intro7;
	private BufferedImage intro8;
	private BufferedImage intro9;
	private BufferedImage displayIntro;
	
	private BufferedImage end;

	public Screens(){
		try {
			 intro1 = ImageIO.read(new File("src/1.png")); //Background
		        
		        intro2 = ImageIO.read(new File("src/2.png")); //Background
		        intro3 = ImageIO.read(new File("src/3.png")); //Background
		        intro4 = ImageIO.read(new File("src/4.png")); //Background
		        intro5 = ImageIO.read(new File("src/5.png")); //Background
		        intro6 = ImageIO.read(new File("src/6.png")); //Background
		        intro7 = ImageIO.read(new File("src/7.png")); //Background
		        intro8 = ImageIO.read(new File("src/8.png")); //Background
		        intro9 = ImageIO.read(new File("src/9.png")); //Background
		        
		        end = ImageIO.read(new File("src/end.jpg")); //Background
		
	 } catch (IOException ex) {
         // handle exception...
    }
		displayIntro = intro1;
	}
	public void checker(Graphics g){
	if(isGameStarted == false){
		
		//Display picture.
		//Change picture for each press of SPACE
		if(i ==1){
			displayIntro = intro2;
		}
		if(i ==2){
			displayIntro = intro3;
		}
		if(i ==3){
			displayIntro = intro4;
		}
		if(i ==4){
			displayIntro = intro5;
		}
		if(i ==5){
			displayIntro = intro6;
		}
		if(i ==6){
			displayIntro = intro7;
		}
		if(i ==7){
			displayIntro = intro8;
		}
		if(i ==8){
			displayIntro = intro9;
		}
		if(i ==9){
			isGameStarted = true;
		}
		g.drawImage(displayIntro, 0, 0, screenWidth, screenHeight, null); //Player
	}
	if(isGoalReach == true){
		g.drawImage(end, 0, 0, screenWidth, screenHeight, null); //Player
	}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
