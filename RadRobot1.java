/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Radosław
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



public class RadRobot1 extends AdvancedRobot {
// offset to save the proper once scanning of each obsticale
    int offsetInScannig = 5;
    boolean endOfScanning = false;
    
    // cordinates of aiming by mouse
    int aimX, aimY;
    
    // Point of Robot
    Point2D startPoint;
    
    // Point of Finish
    Point2D endPoint = new Point2D.Double(0,0);
    
    boolean endPointSetted = false;
    
    
// cordinates of scanned Robot
    int scannedX = Integer.MIN_VALUE;
    int scannedY = Integer.MAX_VALUE;
// size of Fields
    int sizeOfFields = 8;      
// offset to Setting fields to close list
    int howManyFieldsToCheck = 20 / sizeOfFields  + ((20 % sizeOfFields == 0) ? 0 : 1);
       
// list2d of scanned Robots
    List<Point2D> obstaclList = new ArrayList<>();
// list of field objects
    List<BoardField> listOfFields = new ArrayList<BoardField>();
// Open list of A* algorithm
    List<Point2D> openList = new ArrayList<>();
// Close list of A* algorithm
    List<Point2D> closeList = new ArrayList<>();
    
    
    public void run() {
        // Set up the Fields
        setUpFields();
        // Seting the robot place
        setUpStartPoint();        
        // Sets the colors of the robot
	// body = black, gun = white, radar = red
        setColors(Color.BLACK, Color.WHITE, Color.RED);
        // sets the step of rotating the radar       
        double scanStep = 0.1;    
        // turning radar Right to keep once scanning 
        turnRadarRight(1);
        
        
        
        boolean occupiedFieldsSetted = false;
        // ruch robota
        while (true) {
            if (Math.abs(getRadarHeading() - getHeading()) > 0.5){
                turnRadarRight(scanStep);
            }
            else endOfScanning = true;
            if (endOfScanning == true && occupiedFieldsSetted == false){
                setOccupiedFields();
                occupiedFieldsSetted = true ;
            }
            if (occupiedFieldsSetted == true && !endPoint.equals(0)){
                runAlgorithm();
            }
            execute();
    }
}
private void runAlgorithm(){
    
}


private boolean isNeighborOcupied(BoardField field){
    boolean isToClose = false;
    
    return isToClose;
}
    
private boolean checkIfIsOccupied(Point2D point){
    boolean isOccupied = false;
    int corX = (int)point.getX()/ sizeOfFields;
    int corY = (int)point.getY()/ sizeOfFields;
    int fieldIdx = translateToArrayPos(corX, corY);
    if (listOfFields.get(fieldIdx).isOccupied() == true) isOccupied = true;
    return isOccupied;
}

private void setUpStartPoint(){
    startPoint = new Point2D.Double(getX() , getY());
}

private void setUpFields(){
    double mapHeight = getBattleFieldHeight();
    double mapWidth = getBattleFieldWidth();
    int numOfRow = (int)mapHeight/sizeOfFields;
    int numOfCol = (int)mapWidth/sizeOfFields;
    for (int row = 0; row < numOfRow ; row++){
        for (int col = 0; col < numOfCol ; col++){
            BoardField field = new BoardField();
            field.col = col;
            field.row = row;
            field.sizeOfField = sizeOfFields;
            listOfFields.add(field);
        }
    }
}

private void setOccupiedFields(){
    double mapHeight = getBattleFieldHeight();
    double mapWidth = getBattleFieldWidth();
    int numOfRow = (int)mapHeight/sizeOfFields;
    int numOfCol = (int)mapWidth/sizeOfFields;
    for(Point2D obstacle : obstaclList){
//        out.printf("\nOBIEKT X: %f Y: %f",obstacle.getX(), obstacle.getY());
        
        Point2D leftDownCorner = new Point2D.Double( obstacle.getX() - 20 , obstacle.getY() - 20);
        Point2D rightDownCorner = new Point2D.Double( obstacle.getX() + 20 , obstacle.getY() - 20);
        Point2D leftUpCorner = new Point2D.Double( obstacle.getX() - 20 , obstacle.getY() + 20);
        Point2D rightUpCorner = new Point2D.Double( obstacle.getX() + 20 , obstacle.getY() + 20);
        
        int corX = ((int)leftDownCorner.getX()) / sizeOfFields ;
        int corY = ((int)leftDownCorner.getY()) / sizeOfFields ;
        
//        out.printf("\nCORDS [%d , %d]",corX, corY);
        
        int fromLeftCrn = translateToArrayPos(corX, corY);
        corX = ((int)rightDownCorner.getX()) / sizeOfFields ;
        corY = ((int)rightDownCorner.getY()) / sizeOfFields ;
        int toRightCrn = translateToArrayPos(corX, corY);
        
        corX = ((int)leftUpCorner.getX()) / sizeOfFields ;
        corY = ((int)leftUpCorner.getY()) / sizeOfFields ;
        int toLeftUpCrn = translateToArrayPos(corX, corY);
        
        int howManyRows =   ((toLeftUpCrn - fromLeftCrn) / numOfCol) ;
        int rightEnd = toRightCrn;
        int leftBegining = fromLeftCrn;
        for (int k = 0; k <= howManyRows ; k++){
            for(int i = leftBegining ; i <= rightEnd ; i++){
                listOfFields.get(i + numOfCol * k).setOccupied();
                closeFieldsTooCloseToWall(k, i);
            }
        }
    }  
}

private void closeFieldsTooCloseToWall(int row, int col){
    int numOfColumns = (int)getBattleFieldWidth()/sizeOfFields;
    for(int i = row - howManyFieldsToCheck ; i < row + howManyFieldsToCheck ; i++){
        for(int j = col - howManyFieldsToCheck; j < col + howManyFieldsToCheck; j++){
            if( listOfFields.get(j + numOfColumns * i).isClosed() == false){
                listOfFields.get(j + numOfColumns * i).setClosed();
            }
        }   
    }
}

private int translateToArrayPos(int corx, int cory){
    double mapWidth = getBattleFieldWidth();
    int numOfCol = (int)mapWidth/sizeOfFields;
    return (corx  + cory * numOfCol);
}

private int translatePointToArrayPos(Point2D point){
    int corX = (int)point.getX()/ sizeOfFields;
    int corY = (int)point.getY()/ sizeOfFields;
    int fieldIdx = translateToArrayPos(corX, corY);
    return fieldIdx;
}
    
public void printTheObstacles(){
    obstaclList.forEach(out::println);
    out.println();
//    for(int i=1 ; i < obstaclList.size(); i++){
//        out.printf()
}
public void onScannedRobot(ScannedRobotEvent e) {
    double angle = Math.toRadians((getHeading() + e.getBearing()) % 360);
    scannedX = (int)(getX() + Math.sin(angle) * e.getDistance());
    scannedY = (int)(getY() + Math.cos(angle) * e.getDistance());
    Point2D obstacle = new Point2D.Double(scannedX,scannedY);
    if (endOfScanning == false){
        if(obstaclList.isEmpty()){
            obstaclList.add(obstacle);
        }
        else{
            addObstacleIfIsNotInList(obstacle);
        }
    }
}

public void addObstacleIfIsNotInList(Point2D obstacle){
    boolean addObstacle = true;
        for(Point2D obsListelem : obstaclList){
            if(Math.abs(obsListelem.getX() - obstacle.getX()) < offsetInScannig && Math.abs(obsListelem.getY() - obstacle.getY()) < offsetInScannig ){
                addObstacle = false;
            }
        }
        if (addObstacle == true){
            obstaclList.add(obstacle);
        }       
}

public void onMousePressed(MouseEvent e){
    if (e.getButton() == MouseEvent.BUTTON1){
        aimX = e.getX();
        aimY = e.getY();
        out.printf("KLIK [%d , %d]",aimX, aimY);
        out.printf("NUMBER OF FIELDS TO CHECK %d", howManyFieldsToCheck );
//        driveToMouse();
//        out.println("VECTOR");
//        printTheObstacles();
        out.printf("Lenght: %d ", listOfFields.size());
        out.println("BUSSY");
        Point2D point = new Point2D.Double(aimX,aimY);
        if(checkIfIsOccupied(point) == true){
            out.println("IS OCCUPIED");
        }
        else{
            if (endPointSetted == false) {
                endPoint = point;
                endPointSetted = true;
            }
            out.println("IS FREE");
        }
    }      
}

public void driveToMouse(){
    double angleBetweenRobotPoint = Math.atan2(aimX - getX(), aimY - getY());
    angleBetweenRobotPoint = (angleBetweenRobotPoint > 0 ? angleBetweenRobotPoint : (2*Math.PI + angleBetweenRobotPoint)) * 360 / (2*Math.PI);
    out.printf("Ang: %f \n", angleBetweenRobotPoint);
    if (Math.abs(getHeading() - angleBetweenRobotPoint)>3){
        if (getHeading() > angleBetweenRobotPoint){
            turnLeft(getHeading() - angleBetweenRobotPoint);
        }
        else
            turnRight(angleBetweenRobotPoint - getHeading());
    }
    double forwardDrive = Math.sqrt( Math.pow(aimX - getX(), 2 ) + Math.pow(aimY - getY(), 2)  );
    setAhead(forwardDrive);
}


public void onPaint(Graphics2D g) {
    drawPosition(g);
    drawCrossOnClicked(g);
    drawRectOnScannedRobots(g);
    drawRectOnClossedField(g);
    drawRectOnOccupiedFields(g);
    drawStartPointRect(g);
    if (endPointSetted) drawEndPointRect(g);
        
}
public void drawEndPointRect(Graphics2D g){
    Color color = new Color(0x00, 0xff, 0x00, 0xcc);
    drawRectOnField(g, color, endPoint);
}
public void drawStartPointRect(Graphics2D g){
    Color color = new Color(0xff, 0x00, 0x00, 0xcc);
    drawRectOnField(g, color, startPoint);
}

public void drawRectOnClossedField(Graphics2D g){
    g.setColor(new Color(0xff, 0x8c, 0x00, 0x50));
    for(BoardField field : listOfFields){
        if (field.isClosed() == true){
            g.fillRect((int)field.getCol()*sizeOfFields, (int)field.getRow()*sizeOfFields, (int)field.getSizeOfField(), (int)field.getSizeOfField());
        }   
    }
}

public void drawRectOnField(Graphics2D g, Color color, Point2D point){
    int fieldIdx = translatePointToArrayPos(point);
    g.setColor(color);
    g.fillRect((int)listOfFields.get(fieldIdx).getCol()*sizeOfFields, (int)listOfFields.get(fieldIdx).getRow()*sizeOfFields, sizeOfFields, sizeOfFields);
}

public void drawRectOnOccupiedFields(Graphics2D g){
    g.setColor(new Color(0x00, 0x00, 0xff, 0x50));
    for(BoardField field : listOfFields){
        if (field.isOccupied() == true){
            g.fillRect((int)field.getCol()*sizeOfFields, (int)field.getRow()*sizeOfFields, (int)field.getSizeOfField(), (int)field.getSizeOfField());
        }   
    }
}

public void drawRectOnScannedRobots(Graphics2D g){
    g.setColor(new Color(0xff, 0x00, 0x00, 0x80));
//    g.drawLine(scannedX, scannedY, (int)getX(), (int)getY());
    for(Point2D obstacle : obstaclList){
        g.fillRect((int)obstacle.getX() - 20, (int)obstacle.getY() - 20, 40, 40);
    }
}

public void drawCrossOnClicked(Graphics2D g){
    g.setColor(Color.RED);
    g.drawOval(aimX - 15, aimY - 15, 30, 30);
    g.drawLine(aimX, aimY - 4, aimX, aimY + 4);
    g.drawLine(aimX - 4, aimY, aimX + 4, aimY);
}

public void drawPosition(Graphics2D g){
    g.setColor(new Color(0xff, 0x00, 0x00, 0xf0));
    int x = (int)getX();
    int y = (int)getY();
    g.setFont(new Font("Lato", Font.BOLD, 20)); 
    g.drawString(" " + x + " , " + y , (int)(getX()-50), (int)(getY()-60));
}

public static double round(double value, int places) {
    if (places < 0) throw new IllegalArgumentException();

    long factor = (long) Math.pow(10, places);
    value = value * factor;
    long tmp = Math.round(value);
    return (double) tmp / factor;
}

public void onWin(WinEvent e) {
        for (int i = 0; i < 50; i++) {
                turnRight(90);
                turnLeft(90);
        }
    }
}
