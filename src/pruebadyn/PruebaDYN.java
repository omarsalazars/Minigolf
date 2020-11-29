package pruebadyn;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.contact.ContactAdapter;
import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.dynamics.contact.ContactPointId;
import org.dyn4j.geometry.Vector2;
import static pruebadyn.PruebaDYN.players;

public class PruebaDYN extends SimulationFrame{

    int turno;
    private Point point,linePoint;
    public static double SCALE=10;
    SimulationBody ball,hole;
    Level level;
    public static int players;
    public static int[][] score;
    SimulationFrame frame;
    boolean tiro,start;
    boolean[] isPlaying;
    static String soundName = "impact.wav";
    static Clip clip = null;
    static AudioInputStream audioInputStream;
    int pause=0;
    
    private class ContactListener extends ContactAdapter { 
		@Override
		public boolean begin(ContactPoint point) {
                    try{
                        audioInputStream = AudioSystem.getAudioInputStream(new File("impact.wav").getAbsoluteFile());
                        clip = AudioSystem.getClip();
                        clip.open(audioInputStream);
                    }
                    catch(Exception e){}
                    
                    clip.start();
                    return true;
		}
    }
	
    private class CustomKeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
                                case KeyEvent.VK_P:
                                    if (isPaused())
                                        resume();
                                    else
                                        pause();
                                    break;        
			}	
		}

	}
    
    private final class CustomMouseAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            boolean stoppedBalls=true;
            for(SimulationBody b:level.getBalls())
                if(b.getLinearVelocity().getMagnitude()!=0)
                    stoppedBalls=false;
            
            if(stoppedBalls)
            {
                point = new Point(e.getX(), e.getY());
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            point = null;
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            //linePoint=null;
            boolean stoppedBalls=true;
            for(SimulationBody b:level.getBalls())
                if(b.getLinearVelocity().getMagnitude()!=0)
                    stoppedBalls=false;
            if(stoppedBalls)
            {
                linePoint = new Point(e.getX(), e.getY());
            }
            super.mouseDragged(e);
        }
    }
    
    protected void update(Graphics2D g, double elapsedTime) {
        
        ball.setActive(true);
        
        if(this.turno>players)
            turno=1;
        if(!this.isPlaying[turno-1])
        {
            turno++;
            return;
        }
        
        AffineTransform yFlip = AffineTransform.getScaleInstance(1, -1);
        g.transform(yFlip);
        g.setColor(Color.CYAN);
        g.drawString("NIVEL: " + String.valueOf(Level.id) ,-360,-260);
        g.transform(yFlip);
        
        boolean stoppedBalls=true;
        for(SimulationBody b:level.getBalls())
            if(b.getLinearVelocity().getMagnitude()!=0)
                stoppedBalls=false;
        if(stoppedBalls && tiro)
        {
            ball=level.getBalls().get(players-turno);
            this.linePoint=MouseInfo.getPointerInfo().getLocation();
            this.linePoint.x-=this.canvas.getLocationOnScreen().x;
            this.linePoint.y-=this.canvas.getLocationOnScreen().y;
            tiro=false;
            clip.start();
        }
 
        
        int bx = (int) (ball.getWorldCenter().x*this.scale);
        int by = (int) (ball.getWorldCenter().y*this.scale);
        int hx = (int) (hole.getWorldCenter().x*this.scale);
        int hy = (int) (hole.getWorldCenter().y*this.scale);
        int gravityChange = 100;
        //Si la pelota entró al hoyo
        if((bx-hx)*(bx-hx) + (by-hy)*(by-hy) < 100){
            if(ball.getLinearVelocity().getMagnitude()<30 && ball.getLinearVelocity().getMagnitude()>0 ){
                this.world.setGravity(new Vector2((hx-bx)*gravityChange , (hy-by)*gravityChange));
                
                try{
                    audioInputStream = AudioSystem.getAudioInputStream(new File("putt.wav").getAbsoluteFile());
                    clip = AudioSystem.getClip();
                    clip.open(audioInputStream);
                }
                catch(Exception e){}
                clip.start();
                this.ball.setLinearVelocity(0,0);
                
                this.world.removeBody(ball);
                int out=this.level.getBalls().indexOf(ball);
                if(out==0)
                    this.isPlaying[players-1]=false;
                else
                    this.isPlaying[players-1-out]=false;
                
                boolean end=true;
                for(int i=0;i<players;i++)
                {
                    if(isPlaying[i])
                       end=false; 
                }
                if(end)//Si ya acabaron todos
                {
                    this.stop();
                    this.dispose();
                    new Score(score);
                }
                return;
            }
        }
        else {
            this.world.setGravity(World.ZERO_GRAVITY);
        }
        
        if(this.linePoint!=null && ball.getLinearVelocity().getMagnitude()==0){
            int x =  (int) ((this.linePoint.getX() - this.canvas.getWidth() / 2.0));
            int y =  (int) (-(this.linePoint.getY() - this.canvas.getHeight() / 2.0));
            g.setColor(ball.getColor());
            g.drawLine(bx,by,2*bx-x ,2*by-y);
        }

        if (this.point != null ) {
            // convert from screen space to world space coordinates
            double x =  (this.point.getX() - this.canvas.getWidth() / 2.0) / this.scale;
            double y = -(this.point.getY() - this.canvas.getHeight() / 2.0) / this.scale;
            //GOLPE
            try{
                audioInputStream = AudioSystem.getAudioInputStream(new File("strike.wav").getAbsoluteFile());
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);
            }
            catch(Exception e){}
            clip.setFramePosition(clip.getFrameLength()/3);
            clip.start();
            ball.applyImpulse(new Vector2((ball.getWorldCenter().x - x)*1000, (ball.getWorldCenter().y - y)*1000));
            
            tiro=true;
            linePoint=null;
            this.point=null;
            score[turno-1][Level.id-1]++;
            turno++;
            
            /*
            System.out.println(turno);
            for(int i=0;i<isPlaying.length;i++)
                System.out.print(isPlaying[i]+" ");
            System.out.println();*/
        }
        for(SimulationBody b:level.getBalls())
        if(b.getLinearVelocity().getMagnitude()<.5){
            b.setLinearVelocity(0,0);
        }
        
        checkZones(g);
        
        super.update(g, elapsedTime);
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            Zone.rampImage=ImageIO.read(new File("levels/rampa.jpg"));
            Zone.sandImage=ImageIO.read(new File("levels/arena.jpg"));
        } catch (IOException ex) {
            Logger.getLogger(Level.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
       try{
            audioInputStream = AudioSystem.getAudioInputStream(new File("impact.wav").getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch(Exception e){}
       
       clip.start();
       // clip.stop();
        
        new StartFrame();
    }

    private void checkZones(Graphics2D g)
    {
        for(Ramp r:this.level.getRamps()){
            g.setColor(Color.WHITE);
            /*
            if(r.getGravity().x!=0)
            {
                g.drawLine((int)(r.getX()*SCALE)+10,(int)((r.getY()+r.getHeight()/2)*SCALE),(int)((r.getX()+r.getWidth())*SCALE)-10,(int)((r.getY()+r.getHeight()/2)*SCALE));
                if(r.getGravity().x>0)//flecha a la derecha
                {
                    
                    g.drawLine((int)((r.getX()+r.getWidth())*SCALE)-10,(int)((r.getY()+r.getHeight()/2)*SCALE),(int)((r.getX()+r.getWidth())*SCALE)-50,(int)((r.getY()+r.getHeight()/2)*SCALE)+30);
                    g.drawLine((int)((r.getX()+r.getWidth())*SCALE)-10,(int)((r.getY()+r.getHeight()/2)*SCALE),(int)((r.getX()+r.getWidth())*SCALE)-50,(int)((r.getY()+r.getHeight()/2)*SCALE)-30);
                }
                else//flecha a la izquierda
                {
                    g.drawLine((int)(r.getX()*SCALE)+10,(int)((r.getY()+r.getHeight()/2)*SCALE),(int)(r.getX()*SCALE)+50,(int)((r.getY()+r.getHeight()/2)*SCALE)+30);
                    g.drawLine((int)(r.getX()*SCALE)+10,(int)((r.getY()+r.getHeight()/2)*SCALE),(int)(r.getX()*SCALE)+50,(int)((r.getY()+r.getHeight()/2)*SCALE)-30);
                }
            }
            else if(r.getGravity().y!=0)
            {
                g.drawLine((int) ((r.getX()+r.getWidth()/2)*SCALE),(int) (r.getY()*SCALE)+10,(int) ((r.getX()+r.getWidth()/2)*SCALE),(int) ((r.getY()+r.getHeight())*SCALE)-10);
                if(r.getGravity().y>0)//flecha arriba
                {
                    g.drawLine((int) ((r.getX()+r.getWidth()/2)*SCALE),(int) (r.getY()*SCALE)+10, (int) ((r.getX()+r.getWidth()/2)*SCALE)-30, (int) (r.getY()*SCALE)+50);
                    g.drawLine((int) ((r.getX()+r.getWidth()/2)*SCALE),(int) (r.getY()*SCALE)+10, (int) ((r.getX()+r.getWidth()/2)*SCALE)+30, (int) (r.getY()*SCALE)+50);
                }
                else//flecha abajo
                {
                    g.drawLine((int) ((r.getX()+r.getWidth()/2)*SCALE),(int) ((r.getY()+r.getHeight())*SCALE)-10, (int) ((r.getX()+r.getWidth()/2)*SCALE)-30, (int) (r.getY()*SCALE)-50);
                    g.drawLine((int) ((r.getX()+r.getWidth()/2)*SCALE),(int) ((r.getY()+r.getHeight())*SCALE)-10, (int) ((r.getX()+r.getWidth()/2)*SCALE)+30, (int) ((r.getY()+r.getHeight())*SCALE)-50);
                }
            }
            */
            if(r.getGravity().x!=0)
            {
                if(r.getGravity().x>0)//flecha a la derecha
                {
                    
                    g.drawString(">",(int)((r.getX()+r.getWidth()/2)*scale),(int) ((r.getY()+r.getHeight()/2)*scale));
                }
                else//flecha a la izquierda
                {
                    g.drawString("<",(int)((r.getX()+r.getWidth()/2)*scale),(int) ((r.getY()+r.getHeight()/2)*scale));
                }
            }
            else if(r.getGravity().y!=0)
            {
                if(r.getGravity().y>0)//flecha arriba
                {
                    g.drawString("v",(int)((r.getX()+r.getWidth()/2)*scale),(int) ((r.getY()+r.getHeight()/2)*scale));
                }
                else//flecha abajo
                {
                    g.drawString("^",(int)((r.getX()+r.getWidth()/2)*scale),(int) ((r.getY()+r.getHeight()/2)*scale));
                }
            }
            
            for(SimulationBody b:level.getBalls())
            {
                if(r.ballInside(b)){
                    b.applyForce(r.getGravity());
                }
            }
        }
        
        for(SimulationBody b:level.getBalls()){        
            for(SandZone sZ:this.level.getSandZones()){
                if(sZ.ballInside(b)){
                    b.setLinearDamping(sZ.getDamping());
                    break;
                }
                else{
                    b.setLinearDamping(.8);
                }
            }
        }
        
    }
    
public PruebaDYN() {
        super("MINIGOLF IN THA HOOD", SCALE);
        MouseAdapter ml = new PruebaDYN.CustomMouseAdapter();
        this.canvas.addMouseMotionListener(ml);
        this.canvas.addMouseWheelListener(ml);
        this.canvas.addMouseListener(ml);
        frame=this;
        
        JMenuBar menubar=new JMenuBar();
        JMenu menu=new JMenu("PAUSA");
        menu.addMouseListener(new MouseAdapter() {
            int pause=0;
            @Override
            public void mouseClicked(MouseEvent e) {
                if(pause==0)
                {
                    pause();
                    pause++;
                    
                }
                else
                {
                    resume();
                    pause=0;
                    menu.setSelected(false);
                }
            }
        });
        menubar.add(menu);
        this.setJMenuBar(menubar);
        this.isPlaying=new boolean[players];
        for(int i=0;i<players;i++)
            isPlaying[i]=true;
    }

    @Override
    protected void initializeWorld() {
        this.world.setGravity(World.ZERO_GRAVITY);
        this.world.getSettings().setRestitutionVelocity(0);
        
        turno=1;
        level=new Level();
        level.readFile(scale);
        for(SimulationBody o:level.getObjects())
        {
            this.world.addBody(o);
        }
        ball=level.getBalls().get(level.getBalls().size()-1);
        hole=level.getObjects().get(0);
        this.world.addListener(new ContactListener());
        KeyListener listener = new CustomKeyListener();
		this.addKeyListener(listener);
    }
}