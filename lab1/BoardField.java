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
    int gScore = Integer.MAX_VALUE / 2;
    int fScore = Integer.MAX_VALUE / 2;
    double sizeOfField;
    boolean occupied = false;
    boolean closed = false;
    boolean open = false;
    
Point2D setCenterOfField(){
    double corX = this.col + 20;
    double corY = this.row + 20;
    Point2D point = new Point2D.Double(corX, corY);    
    return point;
}
    
int getCol(){ return col; }

int getRow(){ return row; }

void setGScore(int score){ this.gScore = score;}

int getGScore(){ return this.gScore ;}

void setFScore(int score){ this.fScore = score;}

Point2D getCenter(){ return this.center;}

int getScore(){ return (gScore + fScore);}

double getSizeOfField(){ return sizeOfField; }

boolean isOccupied(){ return occupied;}

boolean isClosed(){ return closed;}

boolean isOpen(){ return open;}

void setOccupied(){ 
    this.occupied = true;
    this.closed = true;
    this.open = false;
    }
void setClosed(){ 
    this.closed = true;
    this.open = false;
    }
void setOpen(){ 
    this.open = true;
    }
}
