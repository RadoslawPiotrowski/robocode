/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import java.awt.*;
import java.awt.event.MouseEvent;
import robocode.HitWallEvent;
import java.lang.Math;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import robocode.WinEvent;
import java.util.concurrent.ThreadLocalRandom;

public class Flayer extends AdvancedRobot {
    int counterForTracking = 0;
    double gunTurnForSearching;
    String trackName;
    double handDistance = 10;
    double lastDistance;
    boolean runAway = false;

    int distanceToStartEscaping = 500;
    
    int distanceToClaimTheCatch = 30;

    
        
    public void run(){
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
                    gunTurnForSearching = 10;
            }
            // If we still haven't seen our target for 5 turns, look right
            if (counterForTracking > 3) {
                    gunTurnForSearching = -10;
            }
		}  
    }      
    
    private int setRadarDirection(){
        int leftOrRight = ThreadLocalRandom.current().nextInt(0, 1 + 1);
        return leftOrRight;
    }
    
    private void setUpColors(){
        setBodyColor(Color.YELLOW);
        setGunColor(Color.BLACK);
        setRadarColor(Color.YELLOW);
        setScanColor(Color.YELLOW);
        setBulletColor(Color.YELLOW);
    }
    
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
        double angleToPursuer = (getHeading() + e.getBearing()) % 360;
        out.printf("\nAngle to follower: %f", angleToPursuer);
        double distanceToPursuer = e.getDistance();
        checkIfHaveToEscape(distanceToPursuer);
        if(runAway == true){
            
            tryToEscape(angleToPursuer, distanceToPursuer);
        }
        else{
            out.printf("\nIZZZI");
        }
    }
    
    private double calculateTheEscapeAngle(double angleToPursuer){
        double angleToSet = angleToPursuer + 90;
        return angleToSet;
    }
    
    private void tryToEscape(double anglePerpendicular, double distanceToPursuer){
        anglePerpendicular = calculateTheEscapeAngle(anglePerpendicular);
        setDirectionAndAngle(anglePerpendicular);
        setForwardMove(distanceToPursuer);
        out.printf("\nI WANNA RUN");
    }
    
    private void setForwardMove(double distanceToPursuer){
        setAhead(distanceToPursuer + handDistance);
        
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
    
    private void checkIfHaveToEscape(double distance){
        if(distance <= distanceToClaimTheCatch){
            runAway = false;
        }
        else if( distance <= distanceToStartEscaping){
            runAway = true;
        }
        else{
            runAway = false;
        }
    }
}