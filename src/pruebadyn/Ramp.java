/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pruebadyn;

import java.awt.Image;
import org.dyn4j.geometry.Vector2;
import static pruebadyn.Zone.rampImage;

public class Ramp extends Zone{
    private final Vector2 gravity;
    public boolean ballInside(SimulationBody ball){
        double s = PruebaDYN.SCALE;
        double ballX=ball.getWorldCenter().x*s;
        double ballY=ball.getWorldCenter().y*s;
        if(ballX>this.getLeftBound()*s+5 && ballX<this.getRightBound()*s-5 && 
                ballY<this.getUpperBound()*s-5 && ballY>this.getLowerBound()*s+5){
            return true;
        }
        else{
            return false;
        }
    }
    
    public Vector2 getGravity() {
        return gravity;
    }
    
    public Ramp(Vector2 gravity, double leftBound, double rightBound, double upperBound, double lowerBound) {
        super(leftBound, rightBound, upperBound, lowerBound);
        this.gravity = gravity;
        this.img=rampImage.getScaledInstance((int)(this.getWidth()*PruebaDYN.SCALE),(int)(this.getHeight()*PruebaDYN.SCALE), Image.SCALE_DEFAULT);        
    }   
}
