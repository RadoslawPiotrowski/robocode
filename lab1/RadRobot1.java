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
    // Zero point to compare
    Point2D zeroPoint = new Point2D.Double(0,0);
    
    boolean endPointSetted = false;
    
    
// cordinates of scanned Robot
    int scannedX = Integer.MIN_VALUE;
    int scannedY = Integer.MAX_VALUE;
// size of Fields
    int sizeOfFields = 15;      
// offset to Setting fields to close list
    int howManyFieldsToCheck = 15 / sizeOfFields  + ((15 % sizeOfFields == 0) ? 0 : 1);
       
// list2d of scanned Robots
    List<Point2D> obstaclList = new ArrayList<>();
// list of field objects
    List<BoardField> listOfFields = new ArrayList<>();
// Open list of A* algorithm
    List<FieldIdx> openList = new ArrayList<>();
// Close list of A* algorithm
    List<FieldIdx> closeList = new ArrayList<>();
// Path list of all possible moves
    List<PathIdx> pathList = new ArrayList<>();
// one of the way to end the algorithm
    List<FieldIdx> pathToPoint = new ArrayList<>();
    boolean algorithmEnd = false;
    List<FieldIdx> myList = new ArrayList<>();
        
    public void run() {
        // Set up the Fields
        setUpFields();
        // Seting the robot place
        setUpStartPoint();        
        // Sets the colors of the robot
	// body = black, gun = white, radar = red
        setColors(Color.BLACK, Color.WHITE, Color.RED);
        // sets the step of rotating the radar       
        double scanStep = 0.5;    
        // turning radar Right to keep once scanning 
        turnRadarRight(1);
        // Zero point to compare
        Point2D zeroPoint = new Point2D.Double(0,0);
        
        
        // flag to check if setting fields occupied is done
        boolean occupiedFieldsSetted = false;
        // flag to start the algorithm
        boolean doneSetUpBeforeAlgorithm = false;
        // debug variable
        boolean debugStop = false;
        // ruch robota
        int counter = 0;
        while (true) {
            if (Math.abs(getRadarHeading() - getHeading()) > 0.5){
                turnRadarRight(scanStep);
            }
            else endOfScanning = true;
            if (endOfScanning == true && occupiedFieldsSetted == false){
                setOccupiedFields();
                occupiedFieldsSetted = true ;
            }
            if (occupiedFieldsSetted == true && !endPoint.equals(zeroPoint) && debugStop == false){
                if(doneSetUpBeforeAlgorithm == false){
                    setUpBeforeAlgorithm();
                    doneSetUpBeforeAlgorithm = true;
                }
            }
            if(doneSetUpBeforeAlgorithm == true && debugStop == false){
//                out.printf("\nBEFORE");
//                printDebugInformation();
                
                if(openList.isEmpty() != true && algorithmEnd == false){ 
                    runAlgorithm();
                    counter++ ;
                }
//                
//                out.printf("\nAFTER");
//                printDebugInformation();
//                
            }
            if(counter > 5){
               debugStop = false; 
            }

            
            execute();
    }
}
 
private void createPath(){
    int startFieldIdx = translatePointToArrayPos(startPoint);
    int endFieldIdx = translatePointToArrayPos(endPoint);
    out.printf("\nJESTEM1");
    int indexOfLast = getIndexOfPathField(endFieldIdx);
    addFieldWithIndexToPathToPoint(endFieldIdx);
    out.printf("\n sizeOfPathToPoin: %d",pathToPoint.size());
    out.printf("\n lastElementIndex: %d", pathToPoint.get(pathToPoint.size()-1).getFieldIdx());
    int fieldIdxToAdd = 0;
    while(fieldIdxToAdd != startFieldIdx){
        int indexToSearch = pathToPoint.get(pathToPoint.size()-1).getFieldIdx();
        out.printf("\n IndexInArray: %d",indexToSearch);
        out.printf("\n IndexOstatniego elemntu: %d",pathList.get(pathList.size()-1).getActualIdx());
        int indexOfFieldInPathList = getIndexOfPathField(indexToSearch);
        out.printf("\n IndexInPathList: %d",indexOfFieldInPathList);
        fieldIdxToAdd = pathList.get(indexOfFieldInPathList).getFieldIdx();
        out.printf("\n IndexInArray of next node: %d",fieldIdxToAdd);
        addFieldWithIndexToPathToPoint(fieldIdxToAdd);
        out.printf("\nJESTEM2");
//        for(FieldIdx field : pathList){  
//            if(isNeighbor(index, field.getFieldIdx())){
//                if(listOfFields.get(field.getFieldIdx()).getScore() < lowerCost ){
//                  lowerCostNeghborIdx = field.getFieldIdx();
//                }
//            }
//        }
    }
    
}

private int getIndexOfPathField(int fieldIdx){
    int cameFromFieldIndex = 0;
    int endFieldIdx = translatePointToArrayPos(endPoint);
    for(PathIdx field : pathList){
        if( field.getActualIdx() == endFieldIdx){
            out.printf("\nDOCELOWY ELEMENT JEST W LISCIE \n NA INDEXIE: %d", pathList.indexOf(field));
        }
    }
    for(PathIdx field : pathList){
        if( field.getActualIdx() == fieldIdx){
            cameFromFieldIndex = pathList.indexOf(field);
        }
    }
    return cameFromFieldIndex;
}

private boolean isNeighbor(int startIdx, int endIdx){
    boolean isNeighbor = false;
    int numOfColumns = (int)getBattleFieldWidth()/sizeOfFields;
    if(startIdx == endIdx -1 || startIdx == endIdx +1){
        isNeighbor = true;
    }
    if(startIdx == endIdx + numOfColumns || startIdx == endIdx - numOfColumns){
       isNeighbor = true; 
    }
    if(startIdx == endIdx + numOfColumns +1 || startIdx == endIdx - numOfColumns +1 ){
       isNeighbor = true; 
    }
    if(startIdx == endIdx + numOfColumns -1 || startIdx == endIdx - numOfColumns -1 ){
       isNeighbor = true; 
    }

    return isNeighbor;
}

private void setUpBeforeAlgorithm(){
    int startFieldIdx = translatePointToArrayPos(startPoint);
    int endFieldIdx = translatePointToArrayPos(endPoint);
    addFieldWithIndexToOpenList(startFieldIdx);
    listOfFields.get(startFieldIdx).setGScore(0);
    listOfFields.get(startFieldIdx).setFScore(heuristicCostEstimate(startFieldIdx, endFieldIdx));
}    

private void runAlgorithm(){
        printDebugInformation();
        int endFieldIdx = translatePointToArrayPos(endPoint);
        int currentNodeIdx = getNodeWithLowestCost();
        if (currentNodeIdx == endFieldIdx){
            checkNeighborNodes(currentNodeIdx);
            createPath();
            algorithmEnd = true;
        }
        openList = removeNodeFromArrayList(openList, currentNodeIdx);
        addFieldWithIndexToCloseList(currentNodeIdx);
        
        checkNeighborNodes(currentNodeIdx);

        out.printf("\nN OF ELEM IN PATH %d", pathList.size() );
}

private void checkNeighborNodes(int fieldIdx){
    out.printf("\nAnalize: %d", fieldIdx);
    int endFieldIdx = translatePointToArrayPos(endPoint);
    int numOfColumns = (int)getBattleFieldWidth()/sizeOfFields;
    int centerElementRow = listOfFields.get(fieldIdx).getRow();
    int centerElementCol = listOfFields.get(fieldIdx).getCol();
    
    out.printf("\nTUT1");
    out.printf("  Analize: %d", fieldIdx);
    for(int i = centerElementRow - 1 ; i <= centerElementRow + 1 ; i++){
        for(int j = centerElementCol - 1; j <= centerElementCol + 1; j++){
            int index = j + numOfColumns * i ;
            if(fieldIdx == index){
                continue;
            }
            
            out.printf("\nNode: %d", index);
            if (isInCloseList(index)){
                continue;
            }  
            if (listOfFields.get(index).isClosed() == true){
                continue;
            }
            if (isInOpenList(index) == false && listOfFields.get(index).isClosed() == false){
                addFieldWithIndexToOpenList(index);
            }
            int unCertainGScore = listOfFields.get(fieldIdx).getScore() + distanceBetween(fieldIdx, index);
            if (unCertainGScore >= listOfFields.get(index).getScore()){
                continue;
            }
            addFieldWithIndexToPathList(fieldIdx, index);
            listOfFields.get(index).setGScore(unCertainGScore);
            listOfFields.get(index).setFScore(listOfFields.get(index).getGScore() + heuristicCostEstimate(index, endFieldIdx) );  
        }   
    }
    out.printf("\nTUT2");
}

private int distanceBetween(int startIndex, int endIndex){
    Point2D startField = listOfFields.get(startIndex).getCenter();
    int distance = (int)(startField.distance(listOfFields.get(endIndex).getCenter()));
    return distance;
}

public boolean isInPathToPointList(int index){
    boolean inPathList = false;
    if(pathToPoint.isEmpty() == false){
        for(FieldIdx fieldIndex : pathToPoint){
            if( index == fieldIndex.getFieldIdx()){
               inPathList = true;
            }   
        }
    }
    return inPathList;
}

public boolean isInOpenList(int index){
    boolean inOpenList = false;
    if(openList.isEmpty() == false){
        for(FieldIdx fieldIndex : openList){
            if( index == fieldIndex.getFieldIdx()){
               inOpenList = true;
            }   
        }
    }
    return inOpenList;
}

public boolean isInCloseList(int index){
    boolean inCloseList = false;
    if(closeList.isEmpty() == false){
        for(FieldIdx fieldIndex : closeList){
            if(index == fieldIndex.getFieldIdx()){
               inCloseList = true;
            }   
        }
    }
    return inCloseList;
}

public void printPathInformation(){
    if(openList.isEmpty() == false){
        out.printf("\nLIST");
        for(PathIdx fieldIndex : pathList){out.printf("\nIdx: %d ", fieldIndex.getFieldIdx());}
    }  
}

public void printDebugInformation(){
    if(openList.isEmpty() == false){
        out.printf("\nLIST");
        for(FieldIdx fieldIndex : openList){out.printf("\nIdx: %d ,Cost: %d", fieldIndex.getFieldIdx(),listOfFields.get(fieldIndex.getFieldIdx()).getScore() ); }
    }  
}

public static List<FieldIdx> removeNodeFromArrayList(List<FieldIdx> inputArray, int nodeToDeleteIdx){
    int i = 0;
    while(i < inputArray.size()){
        if(inputArray.get(i).getFieldIdx() == nodeToDeleteIdx){
            inputArray.remove(i);
        }
        else{
            i++;
        }
    }
    return inputArray;
}

public int getNodeWithLowestCost(){
    int lowestCost = Integer.MAX_VALUE;
    int lowestCostIndex = 0;
    for (FieldIdx fieldIndex : openList){
        int index = fieldIndex.getFieldIdx();
        if(listOfFields.get(index).getScore() <= lowestCost){
            lowestCost = listOfFields.get(index).getScore();
            lowestCostIndex = index;
        }
    }
    return lowestCostIndex;
}

public void addToMyList(int index){
    FieldIdx fieldIndex = new FieldIdx();
    fieldIndex.setFieldIdx(index);
    myList.add(fieldIndex);
}

public void addFieldWithIndexToPathToPoint(int index){
    FieldIdx fieldIndex = new FieldIdx();
    fieldIndex.setFieldIdx(index);
    pathToPoint.add(fieldIndex);
}

public void addFieldWithIndexToPathList(int index, int trunkIdx){
    PathIdx fieldIndex = new PathIdx();
    fieldIndex.setFieldIdx(index);
    fieldIndex.setActualIdx(trunkIdx);
    pathList.add(fieldIndex);
}

public void addFieldWithIndexToOpenList(int index){
    FieldIdx fieldIndex = new FieldIdx();
    fieldIndex.setFieldIdx(index);
    openList.add(fieldIndex);
    listOfFields.get(index).setOpen();
}

public void addFieldWithIndexToCloseList(int index){
    FieldIdx fieldIndex = new FieldIdx();
    fieldIndex.setFieldIdx(index);
    closeList.add(fieldIndex);
    listOfFields.get(index).setClosed();
}

private int heuristicCostEstimate(int startIndex, int endIndex){
    int cost = 10 * (Math.abs(listOfFields.get(startIndex).getCol() - listOfFields.get(endIndex).getCol())+
            Math.abs(listOfFields.get(startIndex).getRow() - listOfFields.get(endIndex).getRow()));
    return cost;
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
            setCloseFieldsTooCloseToEadge(row, col);
        }
    }
}

private void setCloseFieldsTooCloseToEadge(int row, int col){
    int numOfRows = (int)getBattleFieldHeight()/sizeOfFields;
    int numOfColumns = (int)getBattleFieldWidth()/sizeOfFields;
    int index = row * numOfColumns + col;
    if( row == 0 || row == numOfRows - 1 ){
        listOfFields.get(index).setClosed();
        addFieldWithIndexToCloseList(index);
    }
    if( index % numOfColumns == 0 || (index + numOfColumns +1) % numOfColumns == 0){
        listOfFields.get(index).setClosed();
        addFieldWithIndexToCloseList(index);
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
                closeFieldTooCloseToWall(k, i);
                
            }
        }
    }  
}


private void closeFieldTooCloseToWall(int row, int col){
    int numOfColumns = (int)getBattleFieldWidth()/sizeOfFields;
    for(int i = row - howManyFieldsToCheck ; i < row + howManyFieldsToCheck ; i++){
        for(int j = col - howManyFieldsToCheck; j < col + howManyFieldsToCheck; j++){
            if( listOfFields.get(j + numOfColumns * i).isClosed() == false){
                listOfFields.get(j + numOfColumns * i).setClosed();
                addFieldWithIndexToCloseList(j + numOfColumns * i);
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
        out.printf("\nKLIK [%d , %d]",aimX, aimY);
        out.printf("\nNUMBER OF FIELDS TO CHECK %d", howManyFieldsToCheck );
//        driveToMouse();
//        out.println("VECTOR");
//        printTheObstacles();
        out.printf("\nLenght: %d ", listOfFields.size());
        out.println("\nBUSSY");
        Point2D point = new Point2D.Double(aimX,aimY);
        if(checkIfIsOccupied(point) == true){
            out.println("\nIS OCCUPIED");
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
    drawOpenListFields(g);
    if (endPointSetted) drawEndPointRect(g);
    drawPathToPointFields(g);
    drawRedField(g);
    
        
}

public void drawRedField(Graphics2D g){
    g.setColor(new Color(0xff, 0x00, 0x00, 0x80));
    for(FieldIdx fieldIndex : myList){
        int index = fieldIndex.getFieldIdx();
        g.fillRect((int)listOfFields.get(index).getCol()*sizeOfFields, (int)listOfFields.get(index).getRow()*sizeOfFields, (int)listOfFields.get(index).getSizeOfField(), (int)listOfFields.get(index).getSizeOfField());
    }
}

public void drawPathToPointFields(Graphics2D g){
    g.setColor(new Color(0xff, 0xff, 0xff, 0x80));
    for(FieldIdx fieldIndex : pathToPoint){
        int index = fieldIndex.getFieldIdx();
        g.fillRect((int)listOfFields.get(index).getCol()*sizeOfFields, (int)listOfFields.get(index).getRow()*sizeOfFields, (int)listOfFields.get(index).getSizeOfField(), (int)listOfFields.get(index).getSizeOfField());
    }
}

public void drawOpenListFields(Graphics2D g){
    g.setColor(new Color(0xff, 0x00, 0x00, 0x80));
    for(FieldIdx fieldIndex : openList){
        int index = fieldIndex.getFieldIdx();
        g.fillRect((int)listOfFields.get(index).getCol()*sizeOfFields, (int)listOfFields.get(index).getRow()*sizeOfFields, (int)listOfFields.get(index).getSizeOfField(), (int)listOfFields.get(index).getSizeOfField());
    }
}

public void drawEndPointRect(Graphics2D g){
    Color color = new Color(0x00, 0xff, 0x00, 0xcc);
    drawRectOnField(g, color, endPoint);
}
public void drawStartPointRect(Graphics2D g){
    Color color = new Color(0xff, 0xff, 0x00, 0xcc);
    drawRectOnField(g, color, startPoint);
}

public void drawRectOnClossedField(Graphics2D g){
    g.setColor(new Color(0xec, 0x70, 0x63, 0x50));
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
