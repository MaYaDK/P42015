 
public class Destination {
	public int newBeaconX, newBeaconY;
	
	public int goalPointX;
	public int goalPointY;
	
	//beacon
	boolean isBeacon1Reached = false;
	boolean isBeacon2Reached = false;
	boolean isBeacon3Reached = false;
		//number of beacons reached
	public int numberOfBeaconsReached = 0;
		//Time
	public int counter = 0;
	public int min = 0;
	public int sec = 0;
	Screens screen = new Screens();
	Sound sound = new Sound();
		
	public static void main(String[] args) {

	}
	public void timer(){
		if(counter>=100){
			sec+=1;
			counter = 0;
		}
		if(sec>=60){
			min+=1;
			sec = 0;
		}
		counter++;
	}
	
	public void checkPlayerAtGoal(){
		if(isBeacon3Reached == false && isBeacon2Reached == true && isBeacon1Reached == true){
			sound.beaconHit();
			//set new position of beacon. 
			newBeaconX = 200;
			newBeaconY = 200;
			
			isBeacon3Reached = true;
			System.out.println("Beacon three reached");
			numberOfBeaconsReached +=1;
			System.out.println("Timer:"+ min + ":" + sec + ":" + counter); //Display time when reached.
		}
		if(isBeacon2Reached == false && isBeacon1Reached == true){
			sound.beaconHit();
			//set new position of beacon. 
			newBeaconX = 1000;
			newBeaconY = 1000;
			
			isBeacon2Reached = true;
			System.out.println("Beacon two reached");
			numberOfBeaconsReached +=1;
			System.out.println("Timer:"+ min + ":" + sec + ":" + counter); //Display time when reached.
		}
		if(isBeacon1Reached == false){
			sound.beaconHit();
			//set new position of beacon. 
			newBeaconX += 1500;
			newBeaconY += 100;
			
			isBeacon1Reached = true;
			System.out.println("Beacon one reached");
			numberOfBeaconsReached +=1;
			System.out.println("Timer:"+ min + ":" + sec + ":" + counter); //Display time when reached.
		}
	}
}
