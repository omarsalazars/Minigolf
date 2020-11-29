package pruebadyn;

import java.awt.Image;
import java.awt.image.BufferedImage;
import org.dyn4j.geometry.Vector2;

public abstract class Zone {
   
    private final double leftBound;
    private final double rightBound;
    private final double lowerBound;
    private final double upperBound;
    public static BufferedImage rampImage;
    public static BufferedImage sandImage;
    protected Image img;
    
    public abstract boolean ballInside(SimulationBody ball);

    public Zone(double leftBound, double rightBound, double upperBound,double lowerBound) {
        this.leftBound = leftBound;
        this.rightBound = rightBound;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }
    
    public Image getImage()
    {
        return this.img;
    }
    
    public double getWidth()
    {
        return this.rightBound-this.leftBound;
    }
    
    public double getHeight()
    {
        return this.upperBound-this.lowerBound;
    }
    
    public double getX()
    {
        return this.leftBound;
    }
    
    public double getY()
    {//EL VALOR QUE REGRESAMOS AQUI SE USA SOLAMENTE PARA DIBUJAR, POR ESO REGRESAMOS EL DE ABAJO
        return this.lowerBound;
    }

    public double getLeftBound() {
        return leftBound;
    }

    public double getRightBound() {
        return rightBound;
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public double getUpperBound() {
        return upperBound;
    }
    
}