/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Radosław
 */
import java.awt.*;
import java.lang.Math;
import java.awt.geom.Point2D;

public class BoardField {
    int col;
    int row;
    Point2D center = setCenterOfField();
    int points = 0;
    double sizeOfField;
    boolean occupied = false;
    boolean closed = false;
    boolean open;
    
Point2D setCenterOfField(){
    double corX = this.col + 20;
    double corY = this.row + 20;
    Point2D point = new Point2D.Double(corX, corY);    
    return point;
}
    
int getCol(){ return col; }

int getRow(){ return row; }

int getpoints(){ return points; }

double getSizeOfField(){ return sizeOfField; }

boolean isOccupied(){ return occupied;}

boolean isClosed(){ return closed;}

void setOccupied(){ 
    occupied = true;
    closed = true;
    }
void setClosed(){ 
    closed = true;
    }
}
