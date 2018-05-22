

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import robocode.HitRobotEvent;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;
import robocode.AdvancedRobot;
import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.awt.*;


/**
 *
 * @author MichaĹ‚
 */
public class Tracker extends AdvancedRobot {
	int counterForTracking = 0;
	double gunTurnForSearching;
	String trackName;
        double handDistance = 10;
        double lastDistance;
        boolean chaseFlayer = true;

	/**
	 * run:  Tracker's main run function
	 */
	public void run() {
		setUpColors();
		trackName = null; // Initialize to not tracking anyone
		gunTurnForSearching = 10; // Initialize gunTurn to 10
                setAdjustGunForRobotTurn(true); // Keep the gun still when we turn
		while (true) {
			// turn the Gun (looks for enemy)
			turnGunRight(gunTurnForSearching);
			counterForTracking++;
                        
			// If we've haven't seen our target for 2 turns, look left
			if (counterForTracking > 1) {
				gunTurnForSearching = -10;
			}
			// If we still haven't seen our target for 5 turns, look right
			if (counterForTracking > 4) {
				gunTurnForSearching = 10;
			}
		}
	}
        
        private void setUpColors(){
            setBodyColor(Color.LIGHT_GRAY);
            setGunColor(Color.BLUE);
            setRadarColor(Color.YELLOW);
            setScanColor(Color.white);
            setBulletColor(Color.WHITE);
        }

	/**
	 * onScannedRobot:  Here's the good stuff
	 */
	public void onScannedRobot(ScannedRobotEvent e) {

		// If we have a target, and this isn't it, return immediately
		// so we can get more ScannedRobotEvents.
		if (trackName != null && !e.getName().equals(trackName)) {
			return;
		}
                
		// If we don't have a target, well, now we do!
		if (trackName == null) {
			trackName = e.getName();
			out.println("Tracking " + trackName);
		}
		// This is our target.  Reset count (see the run method)
		counterForTracking = 0;
                double angleToFlayer = (getHeading() + e.getBearing()) % 360;
                out.printf("\nAngle: %f", angleToFlayer);
                double distanceToFlayer = e.getDistance();
                if(chaseFlayer == true){
                    tryToCatch(angleToFlayer, distanceToFlayer);
                }
                else{
                    out.printf("\nLet him go");
                }
	}
        
        private void tryToCatch(double angleToFlayer,double distanceToFlayer){
            setDirectionAndAngle(angleToFlayer);
            setMoveToFlayer(distanceToFlayer);
        }
        
        private void setMoveToFlayer(double distanceToFlayer){
            setAhead(distanceToFlayer - handDistance + 10);
        }
        
        private void setDirectionAndAngle(double anglePerpendicular){
        double difference = getHeading() - anglePerpendicular;
        boolean isBiggerThanHalf = false;
        if(Math.abs(difference) >= 180){
            isBiggerThanHalf = true;
        }
        if(difference < 0){
            if(isBiggerThanHalf == false){
                setTurnRight(Math.abs(difference));
            }
            else{
                setTurnLeft(getHeading() + 360 - anglePerpendicular);
            }
        }
        else{
            if(isBiggerThanHalf == false){
                setTurnLeft(Math.abs(difference));
            }
            else{
                setTurnRight(360 - getHeading() + anglePerpendicular);
            }
        }
    }

	/**
	 * onHitRobot:  Set him as our new target
	 */
	public void onHitRobot(HitRobotEvent e) {
		// Only print if he's not already our target.
		if (trackName != null && !trackName.equals(e.getName())) {
			out.println("Tracking " + e.getName() + " due to collision");
		}
		// Set the target
		trackName = e.getName();
		// Back up a bit.
		// Note:  We won't get scan events while we're doing this!
		// An AdvancedRobot might use setBack(); execute();
		gunTurnForSearching = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
		turnGunRight(gunTurnForSearching);
		
	}

	/**
	 * onWin:  Do a victory dance
	 */
	public void onWin(WinEvent e) {
		for (int i = 0; i < 50; i++) {
			turnRight(30);
			turnLeft(30);
		}
	}
}
