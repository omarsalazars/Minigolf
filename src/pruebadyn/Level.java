package pruebadyn;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

public class Level{
    private static ArrayList<Ramp> ramps;
    private static ArrayList<SandZone> sandZones;  
    static int id=0;
    private SimulationBody ball,hole;
    private ArrayList<SimulationBody> objects,balls;
    String FILENAME;
    
    public Level()
    {
        id++;
        FILENAME="levels/"+id+".txt";
        objects=new ArrayList<>();
        balls=new ArrayList<>();
        ramps = new ArrayList<>();
        sandZones = new ArrayList<>();
    }
    
    public void readFile(double scale)
    {
        BufferedReader br=null;
        FileReader fr=null;
        double bx,by,hx,hy;
        try{
            fr=new FileReader(FILENAME);
            br=new BufferedReader(fr);
            String cl;
            double vals[]=new double[10];//{x,y height,width}
            
            for(int i=0;i<2;i++)
            {
                SimulationBody b=new SimulationBody();
                for(int j=0;j<2;j++)
                {
                    cl=br.readLine();
                    vals[j]=Double.parseDouble(cl);
                }
                b.translate(vals[0],vals[1]);
                objects.add(b);
            }
            
            hole=objects.get(0);
            ball=objects.get(1);
            
            //hole
            hole.addFixture(Geometry.createCircle(1));
            hole.setActive(false);
            hole.setColor(Color.BLACK);
            //ball
            ball.addFixture(Geometry.createCircle(.5),180,0,.8);
            ball.setLinearDamping(1);
            ball.setMass(MassType.FIXED_ANGULAR_VELOCITY);
            ball.setColor(Color.white);
            ball.setBullet(true);
            balls.add(ball);
            objects.remove(ball);
            //Crear bolas para los demas jugadores
            //System.out.println(PruebaDYN.players);
            for(int i=1;i<PruebaDYN.players;i++)
            {
                SimulationBody b=new SimulationBody();
                b.translate(ball.getWorldCenter());
                b.addFixture(Geometry.createCircle(.5),180,0,.8);
                b.setLinearDamping(1);
                b.setMass(MassType.FIXED_ANGULAR_VELOCITY);
                b.setActive(false);
                b.setBullet(true);
                balls.add(b);
            }
            
            
            Random rand = new Random();
            balls.get(0).setColor(Color.WHITE);
            for(int i=1;i<balls.size();i++){
                float r = (float) (rand.nextFloat());
                float g = (float) (rand.nextFloat());
                float b = (float) (rand.nextFloat());
                Color randomColor = new Color(r, g, b);
                balls.get(i).setColor(randomColor);
            }
            
            Collections.reverse(balls);
            for(int i=0;i<balls.size();i++)
            {
                objects.add(balls.get(i));
            }
            
            //read Walls
            cl=br.readLine();
            int l=Integer.parseInt(cl);
            for(int i=0;i<l;i++)
            {
                for(int j=0;j<4;j++)
                {
                    cl=br.readLine();
                    vals[j]=Double.parseDouble(cl);
                }
                SimulationBody wall=new SimulationBody();
                wall.translate(vals[0],vals[1]);
                if(vals[2]!=1)
                    vals[2]++;
                if(vals[3]!=1)
                    vals[3]++;
                wall.addFixture(Geometry.createRectangle(vals[2],vals[3]));
                wall.setMass(MassType.INFINITE);
                objects.add(wall);
                br.readLine();
            }
            //read GravityZones
            cl=br.readLine();
            l=Integer.parseInt(cl);
            if(l==0)br.readLine();
            for(int i=0;i<l;i++)
            {
                for(int j=0;j<6;j++)
                {
                    cl=br.readLine();
                    vals[j]=Double.parseDouble(cl);
                }
                //Vector2 x es gravedadX
                //Vector2 y es gravedadY
                //x, x + ancho, y, y-alto
                ramps.add(new Ramp(new Vector2(vals[0], vals[1]),vals[2],vals[2]+vals[4],vals[3],vals[3]-vals[5]));
                br.readLine();
            }
            //Read molinos
            cl=br.readLine();
            l=Integer.parseInt(cl);
            if(l==0)br.readLine();
            for(int i=0;i<l;i++)
            {
                for(int j=0;j<5;j++)
                {
                    cl=br.readLine();
                    vals[j]=Double.parseDouble(cl);
                }
                SimulationBody wall=new SimulationBody();
                wall.translate(vals[0],vals[1]);
                if(vals[2]!=1)
                    vals[2]++;
                if(vals[3]!=1)
                    vals[3]++;
                wall.addFixture(Geometry.createRectangle(vals[2],vals[3]));
                wall.setAngularVelocity(vals[4]);

                wall.setMass(MassType.INFINITE);
                objects.add(wall);
                br.readLine();
            }
            
            //read sand
            cl=br.readLine();
            l=Integer.parseInt(cl);
            if(l==0)br.readLine();
            for(int i=0;i<l;i++)
            {
                for(int j=0;j<5;j++)
                {
                    cl=br.readLine();
                    vals[j]=Double.parseDouble(cl);
                }
                //Vector2 x es gravedadX
                //Vector2 y es gravedadY
                //x, x + ancho, y, y-alto
                sandZones.add(new SandZone(vals[0],vals[1],vals[1]+vals[3],vals[2],vals[2]-vals[4]));
                br.readLine();
            }
        }
        
        
        
        catch(IOException e)
        {
            e.printStackTrace();
        }
        finally {
            try {
                if (br != null)
                    br.close();
                if (fr != null)
                    fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public ArrayList<SimulationBody> getBalls(){
        return this.balls;
    }
    
    public ArrayList<SimulationBody> getObjects(){
        return this.objects;
    }

    public static ArrayList<Ramp> getRamps() {
        return ramps;
    }

    public static ArrayList<SandZone> getSandZones() {
        return sandZones;
    }
}