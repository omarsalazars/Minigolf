/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pruebadyn;

import java.awt.Image;
import org.dyn4j.geometry.Vector2;
import static pruebadyn.Zone.rampImage;

/**
 *
 * @author Carlos
 */

public class SandZone extends Zone{
    
    private double damping;
    
    public boolean ballInside(SimulationBody ball){
        double ballX=ball.getWorldCenter().x;
        double ballY=ball.getWorldCenter().y;
        double centerX = this.getX() + this.getWidth()/2;
        double centerY = this.getY() + this.getHeight()/2;
        
        return (ballX-centerX)*(ballX-centerX)/(this.getWidth()/2*this.getWidth()/2) + 
                (ballY-centerY)*(ballY-centerY)/(this.getHeight()/2*this.getHeight()/2) <1;
    }

    public double getDamping() {
        return damping;
    }
    
    public SandZone(double damping, double leftBound, double rightBound, double upperBound, double lowerBound) {
        super(leftBound, rightBound, upperBound, lowerBound);
        this.damping  = damping;
        this.img=sandImage.getScaledInstance((int)(this.getWidth()*PruebaDYN.SCALE),(int)(this.getHeight()*PruebaDYN.SCALE), Image.SCALE_DEFAULT);        
    }
    
}
