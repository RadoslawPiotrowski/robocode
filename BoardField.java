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
    Point2D center;
    int points = 0;
    double sizeOfField;
    boolean occupied = false;
    
//Point2D setCenterOfField(){
//    double corX = this.col + 20;
//    double corY = this.
//    Point2D point = new Point2D
//    
//    return Point2D;
//}
    
int getCol(){ return col; }

int getRow(){ return row; }

int getpoints(){ return points; }

double getSizeOfField(){ return sizeOfField; }

boolean isOcucpied(){ return occupied;}

void setOccupied(){ occupied = true;}
}
